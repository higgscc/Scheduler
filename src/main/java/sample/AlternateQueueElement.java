package sample;

import sample.interfaces.QueueElement;

/**
 * Alternate queue element class. Orders elements according to 
 * an algorithm using the message group (in descending order).
 * 
 * This is just a class to demonstrate the production of
 * alternate ordering algorithms. It has no real-world use
 * 
 * @author C.C.Higgs
 */
public class AlternateQueueElement extends AbstractQueueElement
{
	// Comparator interface
	@Override
	public int compareTo(QueueElement t2)
	{
		return getMsg().getGroup().compareTo(t2.getMsg().getGroup()) * -1;
	}
}

