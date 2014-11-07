package sample;

import sample.interfaces.Message;
import sample.interfaces.QueueElement;

import java.util.logging.Logger;

/**
 * Default queue element class. Orders elements according to 
 * to the group algortihm described in the spec
 * 
 * @author C.C.Higgs
 */
public class DefaultQueueElement extends AbstractQueueElement
{
	private DefaultQueueElementSequenceGenerator seqGenerator;

	// NOTE: the use of a double as a sequence is to avoid issues with the sequence overflowing its type
	// and at the same time allow sequencing both within and between groups. It does not totally avoid
	// the issues with both of these, but makes it far less likely to occur
	
	private Double sequence;
	
	// constructor is the default (pacakge) scope as it should be called from
	// DefaultQueueElementFactory but nowhere else. Making this an inner class of
	// the factory would be another approach to this
	
	DefaultQueueElement (DefaultQueueElementSequenceGenerator seqGenerator)
	{
		this.seqGenerator = seqGenerator;
	}
	
	void setMsg(Message msg)
	{
		super.setMsg(msg);
		sequence = seqGenerator.getSeq(msg);
	}
	
	// Comparator interface
	@Override
	public int compareTo(QueueElement t2)
	{
		if (t2 instanceof DefaultQueueElement)
			return sequence.compareTo(((DefaultQueueElement)t2).sequence);
		else
			return 0; // could throw exception here instead
	}
	
}

