import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Queue;

public class Node extends Thread
{

	private String name;
	private boolean run;
	private static int RANGE = 200;
	private double x_Cord;
	private double y_Cord;

	private boolean idle;
	private boolean waiting;

	private boolean sentRTS;
	private boolean sentCTS;
	private boolean receivedRTS;
	private boolean receivedCTS;

	private double backoff;
	private int sendAttempts;

	private double arrivalRate;
	private int numOfFiles;
	private int currentFile;
	
	private Queue<Double> files;
	private ArrayList<Node> nodesInRange;
	public Node()
	{
		name = "";

		idle = true;
		waiting = false;
		sentRTS = false;
		sentCTS = false;
		receivedRTS = false;
		receivedCTS = false;

		backoff = 0.0;
		sendAttempts = 0;

		files = new LinkedList<Double>();
		nodesInRange = new ArrayList<Node>();
		//Default packets come in 2 per second
		arrivalRate = 0.5;

		//Set number of files node will send
		Random rand = new Random();
		numOfFiles = rand.nextInt(10);
		
		this.setx_Cord(rand.nextInt(200) - 100);
		this.sety_Cord(rand.nextInt(200) - 100);
		//Send the file to random size from 1 to 1000
		for(int i = 0; i < numOfFiles;i++)
		{
			files.offer(rand.nextDouble() * 1000.0);
		}
		run = true;
	}

	public void run()
	{
		//System.out.println("Running");
		this.setcurrentFile(0);
		while(run)
		{
			if(this.getWaiting() == false)
			{
				for(int j = this.getcurrentFile(); j < 50;j++)
				{
					if(this.getWaiting() == false)
					try {
						sentRTS(j);
						this.setcurrentFile(j);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			run = false;
		}
		System.out.println("DONE " + this.getNodeName());
	}
	
	public void AddInRangeNode(Node inRange){
		this.nodesInRange.add(inRange);
	}
	
	public boolean isRange(Node receiver)
	{
		if((Math.pow(receiver.getx_Cord()-x_Cord,2) + Math.pow(receiver.gety_Cord()-y_Cord,2)) <= RANGE*RANGE)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param j
	 * @throws InterruptedException
	 */
	public void sentRTS(int j) throws InterruptedException{
		Random rand = new Random();
		int receiverIndex = 0;
		if(nodesInRange.size()!=0) //if there are nodes in range
		{
			receiverIndex = rand.nextInt(nodesInRange.size());
			Node temp = this.nodesInRange.get(receiverIndex);
			int backOfAttempt = 0;
			
			boolean failed = false; //field to see if packet had to back off before
			while(temp.receiveRTS(this,j,failed) == false)
			{
				failed = true;
				backOfAttempt++;
				
				//Calculation for back off
				int BackOffValue = 51200; //in nano seconds
				int upperbound = (int) Math.pow(2, backOfAttempt); //will not -1 cause might get 1 and get 0
				
				//randomly generate 
				Random r = new Random();
				int BackOffNum = r.nextInt(upperbound);
				
				//Back off for a certain time
				sleep(0,(int) BackOffValue*BackOffNum);
				
				if(backOfAttempt > 14)
				{
					System.out.println("fail :" + this.getNodeName() + j + " to " + temp.getNodeName() + " " + temp.getIdle() + " " + temp.getRun());
					failed = true;
					break;
				}
			}
		}
		else //if no nodes in range then can't send or receive 
		{
			run = false;
		}
	}
	
	public boolean receiveRTS(Node sendingNodeName,int j,boolean i) throws InterruptedException
	{	
		//if busy or if another node got here first or if node is current receiving 
		if(getIdle() == false|| getReceivedRTS() || getSentCTS())
		{
			return false;
		}
		else
		{
			setIdle(false);
			setReceivedRTS(true);
			sendingNodeName.setIdle(true);
			
			sentCTS(sendingNodeName,j);
			//If a node had to backoff but then got through
			if(i == true)
			{
				System.out.println("Col");
			}
			return true;
		}
	}
	
	public void sentCTS(Node nodeToCTS,int j) throws InterruptedException{
		//System.out.println("sentCTS");
		this.setSentCTS(true);
		for(Node i: nodesInRange){
			if(i.equals(nodeToCTS) == true)
			{
				i.receiveCTS(this ,j);
			}
			else
			{
				i.setWaiting(true);
			}
		}
	}
	
	public void receiveCTS(Node receiverName,int j) throws InterruptedException{
		this.setReceivedCTS(true);
		//System.out.println("receiveCTS");
		Random r = new Random();
		sleep(0,r.nextInt(100000));
		//System.out.println("Packet #" + j + "From " + this.getNodeName() + " Done Packet");
		requestAck(receiverName);

	}
	
	public void requestAck(Node receivingNode)
	{
		this.reset();
		for(Node i:nodesInRange)
		{ 
			if(i.equals(receivingNode))
			{
				if(i.getAck(receivingNode) == true){
					//sucessfully sent frame?
				}
				else{
					//if failed (in this simulation there are never any packets lost for now
					//decrement the currentFile
					this.setcurrentFile(this.getcurrentFile() - 1);
				}
			}
		}
	}
	
	public boolean getAck(Node sendingNodeName)
	{
		this.reset();
		sendingNodeName.reset();
		for(Node i: nodesInRange){
			if(i.equals(sendingNodeName) == false)
			{
				i.setWaiting(false);
			}
		}
		//received data and return an ACK
		return true;
	}
	
	public void reset()
	{
		this.idle = true;
		this.waiting = false;

		this.sentRTS = false;
		this.sentCTS = false;
		this.receivedRTS = false;
		this.receivedCTS = false;

		this.backoff = 0.0;
		this.sendAttempts = 0;
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

	public String getNodeName()
	{
		return name;
	}
	public void setNodeName(String x)
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

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean getRun() {
		return run;
	}
	
	public ArrayList<Node> getNodesInRange()
	{
		return nodesInRange;
	}

	public void setcurrentFile(int currentFile) {
		this.currentFile = currentFile;
	}

	public int getcurrentFile() {
		return currentFile;
	}
}
