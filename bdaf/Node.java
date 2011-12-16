import java.util.LinkedList;
import java.util.Random;
import java.util.Queue;
import java.util.ArrayList;
import java.lang.Math;

public class Node extends Thread
{

	private static final double RANGE = 35; 		//35 m

	private String name;

	private double x_Cord;
	private double y_Cord;

	private boolean idle;
	private boolean waiting;
	//For threads
	private int time;
	private boolean done;

	private boolean sentRTS;
	private boolean sentCTS;
	private boolean receivedRTS;
	private boolean receivedCTS;

	private double backoff;
	private int sendAttempts;

	private double arrivalRate;
	private int numOfFiles;
	private ArrayList<Node> range;
	private Queue<Double> files;

	public Node()
	{
		name = "";

		idle = true;
		waiting = false;
		time = 0;

		sentRTS = false;
		sentCTS = false;
		receivedRTS = false;
		receivedCTS = false;

		backoff = 0.0;
		sendAttempts = 0;

		files = new LinkedList<Double>();

		//Default packets come in 2 per second
		arrivalRate = 0.5;

		//Set number of files node will send
		Random rand = new Random();
		numOfFiles = rand.nextInt(10);

		//Randomly generates the X,Y cords
		this.setx_Cord(rand.nextInt(200) - 100);
		this.sety_Cord(rand.nextInt(200) - 100);

		//Send the file to random size from 1 to 1000
		for(int i = 0; i < numOfFiles;i++)
		{
			files.offer(rand.nextDouble() * 1000.0);
		}
	}
	//Threading Stuff
	public void run()
	{
		while(!done)
		{
			done = task();
		}
		try
		{
			//1 second sleep?
			Thread.sleep(1000);
		}
		catch(Exception e){}
	}
	protected boolean task()
	{
		if(time == 10) return true;
		time++;
		return false;
	}
	public void halt()
	{
		done = true;
	}
	//See if receiver is in range of sender
	public boolean isRange(Node receiver)
	{
		if((Math.pow(receiver.getx_Cord()-x_Cord,2) + Math.pow(receiver.gety_Cord()-y_Cord,2)) <= RANGE*RANGE)
		{
			return true;
		}
		return false;

	}
	public void reset()
	{
		idle = true;
		waiting = false;

		sentRTS = false;
		sentCTS = false;
		receivedRTS = false;
		receivedCTS = false;

		backoff = 0.0;
		sendAttempts = 0;
	}
	public int getTime()
	{
		return time;
	}
	public ArrayList<Node> getRange()
	{
			return range;
	}
	public Queue<Double> getFiles()
	{
		return files;
	}
	public void setNumOfFiles(int x)
	{
		numOfFiles = x;
	}
	public int getNumOfFiles()
	{
		return numOfFiles;
	}

	public String getName()
	{
		return name;
	}
	public void setName(String x)
	{
		name = x;
	}
	public boolean getIdle()
	{
		return idle;
	}
	public void setIdle(boolean x)
	{
		idle = x;
	}
	public boolean getWaiting()
	{
		return waiting;
	}

	public void setWaiting(boolean x)
	{
		waiting = x;
	}

	public boolean getSentRTS()
	{
		return sentRTS;
	}

	public void setSentRTS(boolean x)
	{
		sentRTS = x;
	}

	public boolean getSentCTS()
	{
		return sentCTS;
	}

	public void setSentCTS(boolean x)
	{
		sentCTS = x;
	}

	public boolean getReceivedRTS()
	{
		return receivedRTS;
	}

	public void setReceivedRTS(boolean x)
	{
		receivedRTS = x;
	}

	public boolean getReceivedCTS()
	{
		return receivedCTS;
	}

	public void setReceivedCTS(boolean x)
	{
		receivedCTS = x;
	}

	public double getBackoff()
	{
		return backoff;
	}

	public void setBackoff(double x)
	{
		backoff = x;
	}

	public int getSendAttempts()
	{
		return sendAttempts;
	}

	public void setSendAttempts(int x)
	{
		sendAttempts = x;
	}
	public double getArrivalRate()
	{
		return arrivalRate;
	}

	public void setArrivalRate(double x)
	{
			arrivalRate = x;
	}

	public double getx_Cord()
	{
		return x_Cord;
	}

	public void setx_Cord(double x)
	{
		this.x_Cord = x;
	}

	public double gety_Cord()
	{
		return y_Cord;
	}

	public void sety_Cord(double x)
	{
		this.y_Cord = x;
	}

}