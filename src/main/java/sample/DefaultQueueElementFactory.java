package sample;

import java.util.logging.Logger;

import sample.interfaces.Message;
import sample.interfaces.QueueElement;
import sample.interfaces.QueueElementFactory;

public class DefaultQueueElementFactory implements QueueElementFactory 
{
	private static Logger log = Logger.getLogger(DefaultQueueElementFactory.class.getName());

	private DefaultQueueElementSequenceGenerator seqGenerator
			= new DefaultQueueElementSequenceGenerator();

	@Override
	public QueueElement getQueueElement(Message msg) 
	{
		DefaultQueueElement element = new DefaultQueueElement(seqGenerator);
		element.setMsg(msg);
		
		return element;
	}
}
