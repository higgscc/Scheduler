package sample;

import java.util.logging.Logger;

import sample.interfaces.Message;
import sample.interfaces.QueueElement;
import sample.interfaces.QueueElementFactory;

/**
 * Generic queue element factory  class. Can be used with any class derived from 
 * AbstractQueueElement and passed to the Scheduler class.
 * Due to limitations on generics in Java, the constructor semantics are somewhat clumsy: 
 * 
 * Scheduler scheduler = Scheduler.instance(3, test.new TestGatewayFactory(), 
 * 		new GenericQueueElementFactory<AlternateQueueElement>
 *							               (AlternateQueueElement.class));
 *
 * If you find this too ugly, you can create your own Factory class. The choice is yours...
 *
 * @author C.C.Higgs
 */
public class GenericQueueElementFactory<T extends AbstractQueueElement> implements QueueElementFactory 
{
	private static Logger log = Logger.getLogger(GenericQueueElementFactory.class.getName());

	private Class<T> myClass;
	
	public GenericQueueElementFactory(Class<T> cls)
	{
		myClass = cls;
	}
	
	@Override
	public QueueElement getQueueElement(Message msg) 
	{
		try
		{
			T element = myClass.newInstance();
			element.setMsg(msg);
			
			return element;
		}
		catch (Exception e)
		{
			log.severe("Threw exception " + e.getStackTrace());
		}
		
		return null;
	}
}
