package sample;

import sample.interfaces.Message;
import sample.interfaces.Gateway;
import sample.interfaces.GatewayFactory;
import sample.interfaces.QueueElement;
import sample.interfaces.QueueElementFactory;

/**
 * Scheduler class 
 * 
 * @author C.C.Higgs
 */
public class Scheduler 
{	
	/**
	 * Get the singleton instance of the Scheduler. Uses the DefaultQueueElementd
	 * to  order messages for processing
	 */
	public static Scheduler instance(int numResources, GatewayFactory gatewayFactory)
	{
	    return instance(numResources, gatewayFactory, new DefaultQueueElementFactory());
	}

	/**
	 * Get the singleton instance of the Scheduler supplying an explicit QueueElementFactory
	 * to generate elements (and hence order messages for processing)
	 */
	public static Scheduler instance(int numResources, GatewayFactory gatewayFactory,
			QueueElementFactory queueElementFactory)
	{
		synchronized(instance)
		{
			if (instance == null)
			{
				instance = new Scheduler(numResources, gatewayFactory, queueElementFactory);
			}
		}
		
		return instance;
	}
	
	/**
	 * Add a message to the queue to be sent to the Gateway 
	 */
	public boolean add(Message msg)
	{
		if (isTerminated()) return false;
		
		String group = msg.getGroup();
		if (lockedGroups.contains(group))
		{
			// last message of group already received,
			// this is an error
			return false;
		}
		
		QueueElement element = queueElementFactory.getQueueElement(msg);
		
		if (element != null)
			queue.add(element);
		
		if (msg.isLastInGroup())
		{
			lockedGroups.add(group);
		}
		
		return true;
	}

	/**
	 * Cancels all pending messages in a specified group
	 */
	public void cancel(String group)
	{
		if (isTerminated()) return;
		
		// the group is now locked against new entries
		lockedGroups.add(group);
		
		// remove any unprocessed elements on the queue
		java.util.Iterator<QueueElement> it = queue.iterator();
		while (it.hasNext())
		{
			QueueElement element = it.next();
			if (element.getMsg().getGroup().equals(group))
			{
				queue.remove(element);
			}
		}		
	}
	
	/**
	 * Shut down the whole scheduler
	 */
	
	public boolean isTerminated()
	{
		return terminateFlag;
	}
	
	public synchronized void terminate()
	{
		if (isTerminated()) return;
		
		terminateFlag = true;
		queue.clear();
		lockedGroups.clear();
		
		threads.interrupt();
		
		instance = null;
	}
	
	//-------------------------------------------------------------------------
	// end of the public methods

	// constructor should only be accessed via Singleton
	// but made package visible for running test cases
	Scheduler(int numResources, GatewayFactory gatewayFactory, QueueElementFactory queueElementFactory) 
	{
		this.queueElementFactory = queueElementFactory;
		
		// one slave thread is started for each resource
		for (int i=0; i<numResources; i++)
		{
			SlaveThread thread = new SlaveThread(threads, gatewayFactory.getGateway());
			thread.start();
		}
	}

	// only used for testing
	Scheduler(int numResources, GatewayFactory gatewayFactory) 
	{
		this(numResources, gatewayFactory, 
			new DefaultQueueElementFactory());
	}
	
	private class SlaveThread extends Thread
	{
		public SlaveThread(ThreadGroup group, Gateway gateway)
		{
			super(group, "");
			this.gateway = gateway;
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					QueueElement element = queue.take();
					Message msg = element.getMsg();
								
					gateway.send(msg);					
					msg.completed();
					
					if (isTerminated()) break;
				}
				catch (java.lang.InterruptedException e)
				{
					break; // work is over, shut down
				}
			}
		}
		
		private Gateway gateway;
	}

	// the main queue containing messages waiting to be processed
	private java.util.concurrent.PriorityBlockingQueue<QueueElement> queue
				= new java.util.concurrent.PriorityBlockingQueue<>();

	// groups for which we have already received the last message, or group has been cancelled
	private java.util.HashSet<String> lockedGroups = new java.util.HashSet<>();
	
	// the slave threads
	private ThreadGroup threads = new ThreadGroup("");
	
	private boolean terminateFlag = false;
	
	private static Scheduler instance = null;
	
	private QueueElementFactory queueElementFactory;
}
