package sample;

import sample.interfaces.Gateway;
import sample.interfaces.GatewayFactory;
import sample.interfaces.Message;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulerTest {

	public java.util.Vector<Integer> doneMessages = new java.util.Vector<>();

	private class TestGatewayFactory implements GatewayFactory
	{
		public Gateway getGateway()
		{
			return new TestGateway();
		}
	}
	
	private class TestGateway implements Gateway
	{
		public void send(Message in)
		{
			try
			{
				TestMessage msg = (TestMessage)in;
				msg.getTest().doneMessages.add(msg.getSeq());
				Thread.sleep(msg.getSleep());
			}
			catch (Exception e) {}
		}
	}
	
	private class TestMessage implements Message
	{
		public TestMessage (int seq, String content, String group, 
				            boolean lastInGroup, int sleep, SchedulerTest test)
		{
			this.seq = seq;
			this.content = content;
			this.group = group;
			this.lastInGroup = lastInGroup;
			this.sleep = sleep;
			this.test = test;
		}
		
		public void completed()
		{
	    }
		
		public String getGroup()
		{
			return group;
		}
		public boolean isLastInGroup()
		{
			return lastInGroup;
		}
		public int getSleep()
		{
			return sleep;
		}
		public int getSeq()
		{
			return seq;
		}
		public SchedulerTest getTest()
		{
			return test;
		}
		
		boolean lastInGroup;
		String group;
		String content;
		int sleep, seq;
		SchedulerTest test;
	}

	@Test
	public void sequentialTest() {
	  SchedulerTest test = new SchedulerTest();				
	  Scheduler scheduler = new Scheduler(1, test.new TestGatewayFactory()); 

	  try
	  {
		
		test.doneMessages.clear();
			
		scheduler.add(test.new TestMessage(1, "number 1", "group1", false, 100, test));
		scheduler.add(test.new TestMessage(2, "number 2", "group2", false, 100, test));
		scheduler.add(test.new TestMessage(3, "number 3", "group3", false, 100, test));
		scheduler.add(test.new TestMessage(4, "number 4", "group5", false, 100, test));
		scheduler.add(test.new TestMessage(5, "number 5", "group2", false, 100, test));
		scheduler.add(test.new TestMessage(6, "number 6", "group1", true, 100, test));

		boolean res = scheduler.add(test.new TestMessage(7, "number 7", "group1", false, 100, test)); // will be ignored
		assertFalse("message 7 falsely accepted", res);

		scheduler.cancel("group5");
		
		boolean res2 = scheduler.add(test.new TestMessage(8, "number 8", "group5", false, 100, test)); // will be ignored
		assertFalse("message 8 falsely accepted", res2);
		
		Thread.sleep(1000);

		Integer [] compMessages = new Integer[] {1, 6, 2, 5, 3};		
		assertArrayEquals(compMessages, test.doneMessages.toArray());
		
      }
	  catch (Exception e) {}

      scheduler.terminate();
	}

	@Test
	public void sequentialTestAlternate() {
      SchedulerTest test = new SchedulerTest();		
		
      Scheduler scheduler = new Scheduler(1, test.new TestGatewayFactory(), 
		 		new sample.GenericQueueElementFactory<sample.AlternateQueueElement>
									(sample.AlternateQueueElement.class));
      try
	  {
		
		test.doneMessages.clear();
			
		scheduler.add(test.new TestMessage(1, "number 1", "group6", false, 1, test));
		scheduler.add(test.new TestMessage(2, "number 2", "group2", false, 1, test));
		scheduler.add(test.new TestMessage(3, "number 3", "group3", false, 1, test));
		scheduler.add(test.new TestMessage(4, "number 4", "group4", false, 1, test));
		scheduler.add(test.new TestMessage(5, "number 5", "group5", false, 1, test));
		scheduler.add(test.new TestMessage(6, "number 6", "group1", true, 1, test));

		Thread.sleep(1000);

		Integer [] compMessages = new Integer[] {1, 5, 4, 3, 2, 6};		
		assertArrayEquals(compMessages, test.doneMessages.toArray());
				
      }
	  catch (Exception e) {}
      
	  scheduler.terminate();
	}

	@Test
	public void performanceTest() {
	  SchedulerTest test = new SchedulerTest();		
	  Scheduler scheduler = new Scheduler(3, test.new TestGatewayFactory()); 

	  try
	  {		
		test.doneMessages.clear();
			
		scheduler.add(test.new TestMessage(1, "number 1", "group1", false, 100, test));
		scheduler.add(test.new TestMessage(2, "number 2", "group2", false, 100, test));
		scheduler.add(test.new TestMessage(3, "number 3", "group3", false, 100, test));
		scheduler.add(test.new TestMessage(4, "number 4", "group5", false, 100, test));
		scheduler.add(test.new TestMessage(5, "number 5", "group2", false, 100, test));
		scheduler.add(test.new TestMessage(6, "number 6", "group1", true, 100, test));
		
		Thread.sleep(250);
		assertEquals(6, test.doneMessages.size());

     }
	 catch (Exception e) {}
	  
	  scheduler.terminate();
	}

	@Test
	public void terminateTest() {
	  SchedulerTest test = new SchedulerTest();		
	  Scheduler scheduler = new Scheduler(1, test.new TestGatewayFactory()); 
	
	  try
	  {		
		scheduler.terminate();
	
		boolean res = scheduler.add(test.new TestMessage(1, "number 1", "group1", false, 100, test)); // will be ignored
		assertFalse("message 1 falsely accepted", res);
			
		scheduler.cancel("group1");
	 }
	 catch (Exception e) {}
	  
	  scheduler.terminate();
	}
}

