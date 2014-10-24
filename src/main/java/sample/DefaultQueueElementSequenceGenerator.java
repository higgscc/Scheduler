package sample;

import sample.interfaces.Message;
import sample.interfaces.QueueElement;

import java.util.logging.Logger;

/**
 * Default queue element sequence class. Generates a sequence
 * number for the message
 * 
 * @author C.C.Higgs
 */
public class DefaultQueueElementSequenceGenerator
{
	private static Logger log = Logger.getLogger(DefaultQueueElementSequenceGenerator.class.getName());
		
	Double getSeq(Message msg)
	{
		String group = msg.getGroup();
		
		synchronized (groupSequences) // do not bother if does not need to be thread safe
		{
			if (!groupSequences.containsKey(group))
			{
				// first message in the group, set sequence number
				groupSequences.put(group, new Double(msgSequence++));
			}
			else
			{
				// related group message, set a sequence after the previous
				// group message, but before any other group
				double seq = groupSequences.get(group).doubleValue();
				seq += 1.0E-9;
				
				groupSequences.put(group, new Double(seq));
			}

			Double sequence = groupSequences.get(group);
			
			log.fine("Element for group " + group + " sequence "  + sequence);
			
			// a partial cleanup as we will not receive more messages
			// for this group
			if (msg.isLastInGroup())
			{
				groupSequences.remove(group);
			}
			
			return sequence;
		}
	}

	void resetSequences()
	{
		msgSequence = 0;
		groupSequences.clear();
	}
	
	// NOTE: the use of a double as a sequence is to avoid issues with the sequence overflowing its type
	// and at the same time allow sequencing both within and between groups. It does not totally avoid
	// the issues with both of these, but makes it far less likely to occur
	
	private double msgSequence = 0;
	
	// the latest sequence number allocated for a message group
	private java.util.HashMap<String, Double> groupSequences 
					= new java.util.HashMap<>();

}

