package sample;

import sample.interfaces.Message;
import sample.interfaces.QueueElement;

/**
 * Abstract queue element class. Can be used as the base class
 * of classes implementing the sample.interfaces.QueueElement
 * interface
 * 
 * @author C.C.Higgs
 */

public abstract class AbstractQueueElement implements QueueElement
{
	void setMsg(Message msg)
	{
		this.msg = msg;		
	}
	
	@Override
	public Message getMsg() {return msg;}

	private Message msg;
}

