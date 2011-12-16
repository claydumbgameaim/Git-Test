import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Queue;
import java.util.Date;
public class Node extends Thread
{
	//temp sum
	public double sum = 0;
	//synchronous = 1, asynchronus = 0
	public int sync = 0;
	public boolean done;
	//Transmit delay ness
	public double transDelay = 0;

	//Varanice
	public ArrayList<Double> vary = new ArrayList<Double>();

	private static final int MAX_BACKOFF_ATTEMPTS = 16;
	private static final double BACKOFF_VALUE = 0.0000512;			// time in seconds

	private static final double VIDEO_BIT_RATE = 1536;					// kilobits per second
	private static final double VOICE_BIT_RATE = 64;					// kilobits per second

	private static final double BAND_WIDTH = 54000000;					//Mbps
	private static final double FRAME_MAX_SIZE = 18.328125;				// 18768 bits = 18.328125 kb
	private static final double PAYLOAD_MAX_SIZE = 18.0625;				// 18496 bits = 18.0625 kb
	private static final double FRAME_OVERHEAD_SIZE = FRAME_MAX_SIZE - PAYLOAD_MAX_SIZE;
	private static final double speedOfLight = 299792458;
	private static final double PACKET_NUM = 5000;
	private static final double AVERAGE_RATE = 100;

	private String name;
	private boolean run;
	private static int RANGE = 100;
	private double x_Cord;
	private double y_Cord;

	private boolean idle;
	private boolean waiting;

	private boolean sentRTS;
	private boolean sentCTS;
	private boolean receivedRTS;
	private boolean receivedCTS;

	private double backoff;
	private double averageBackOff = 1;
	private int sendAttempts;

	private double arrivalRate;
	private int numOfFiles;
	private int currentFile;

	//Timer for the delays
	private long timeElapsed;
	//Keep track of Total data
	private double totalData;

	SimulationTest s;
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

		//Start time
		Date date = new Date();
		setTime(date.getTime());

		//iniitalize total data amount
		totalData = 0;

		files = new LinkedList<Double>();
		nodesInRange = new ArrayList<Node>();
		//Default packets come in 2 per second
		arrivalRate = 0.5;

		Random rand = new Random();
		this.setx_Cord(rand.nextInt(200) - 100);
		this.sety_Cord(rand.nextInt(200) - 100);
		this.setRun(true);
	}
	//Adds from Packet Feeder
	public void feed(Queue<Double> p)
	{
		while(p.peek()!=null)
		{
			files.offer(p.poll());
		}
	}
	public void run()
	{
		Random r = new Random();
		double num_frames = PACKET_NUM + r.nextInt(5000);
		this.setcurrentFile(0);
		if(this.getNodesInRange().size() == 0)
		{
			System.out.println(this.getNodeName() + " has no node in range");
			run = false;
		}
		//If async then run packet shooter
		PacketFeeder p = new PacketFeeder(this);
		if(sync == 0)
		{
			//p = new PacketFeeder(this);
			p.start();
		}
		while(getRun())
		{
			if(sync == 1)
			{
				for(int j = this.getcurrentFile(); j < num_frames;j++)
							{
								if(this.getWaiting() == false)
								{
									try {
										sentRTS();
										reset();
										this.setcurrentFile(j);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

				if(this.getcurrentFile() >= PACKET_NUM - 1)
				{
					double throughput;
					run = false;
					Date date = new Date();
					sum = ((double)date.getTime() - timeElapsed) /1000;
					throughput = this.getTotalData()/sum;
					transDelay = ((double) sum - transDelay)/num_frames;
					System.out.println("DONE " + this.getNodeName() + ", time spend= " +sum + " with a throughput of " + throughput + " and has a delay of " + transDelay + " with a jitter of " + jitter(vary));
				}
			}
			else
			{
			//System.out.println(files.peek());
			while(files.peek()!=null)
			{
				if(this.getWaiting() == false)
				{
					try {
						sentRTS();
						reset();
						sleep(1);
						this.setcurrentFile(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(done && files.peek() == null)
				{
					double throughput;
					run = false;
					Date date = new Date();
					sum = ((double)date.getTime() - timeElapsed) /1000;
					throughput = this.getTotalData()/sum;
					transDelay = ((double) sum - transDelay)/p.ctt;
					System.out.println("DONE " + this.getNodeName() + ", time spend= " +sum + " with a throughput of " + throughput + " total delay: " + transDelay + " with a jitter of " + jitter(vary));
				}
			}
			}
		}

	}

	public void AddInRangeNode(Node inRange)
	{
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

	public double distance(Node receiver)
	{
		return(Math.sqrt((Math.pow(receiver.getx_Cord()-x_Cord,2) + Math.pow(receiver.gety_Cord()-y_Cord,2))));
	}

	/**
	 *
	 * @param j
	 * @throws InterruptedException
	 */
	public void sentRTS() throws InterruptedException{
		Random rand = new Random();
		int receiverIndex = 0;
		if(nodesInRange.size() != 0) //if there are nodes in range
		{
			//Randomly generate a receiver from nodes in Range
			receiverIndex = rand.nextInt(nodesInRange.size());
			Node temp = this.nodesInRange.get(receiverIndex);
			int backOfAttempt = 0;
			double packetSize = 0;
			//Random Packet Size
			if(sync == 1)
			{
				packetSize = 1000.0 * (rand.nextInt((int) PAYLOAD_MAX_SIZE) + FRAME_OVERHEAD_SIZE);
			}
			else
			{
				packetSize = 1000.0 * (files.poll() + FRAME_OVERHEAD_SIZE);
			}
			double distance = distance(temp);

			//prop delay
			double propDelay = distance/speedOfLight;
			sleep(0,(int) (propDelay*1000000000.0));

			for(Node i: this.nodesInRange)
			{
				if(!i.equals(temp))
				{
					i.receiveRTSBackOff(propDelay);
				}
			}
			boolean failed = false; //field to see if packet had to back off before
			double lastBackOffTime = 0;
			while(temp.receiveRTS(this,failed, packetSize, lastBackOffTime) == false)
			{
				failed = true;
				backOfAttempt++;

				//Calculation for back off
				double BackOffValue = BACKOFF_VALUE * 1000000000; //in nano seconds
				double upperbound = Math.pow(2, backOfAttempt); //will not -1 cause might get 1 and get 0

				//randomly generate
				double BackOffNum = (double)rand.nextInt((int) upperbound);
				lastBackOffTime = BackOffValue*BackOffNum;

				//Back off for a certain time
				sleep((int)(BackOffValue*BackOffNum)/1000000,(int) (BackOffValue*BackOffNum%1000000.0));

				if(backOfAttempt > MAX_BACKOFF_ATTEMPTS)
				{
					System.out.println(this.getNodeName() + " attempts sending to " + temp.getNodeName());
					failed = true;
					this.setTotalData(this.getTotalData() + packetSize);
					this.setcurrentFile(this.getcurrentFile() - 1);
					break;
				}
			}
		}
		else //if no nodes in range then can't send or receive
		{
			run = false;
		}
	}

	public void receiveRTSBackOff(double backOffTime) throws InterruptedException
	{
		setWaiting(true);
		sleep((int) (backOffTime*1000),(int) backOffTime%1000000);
		setWaiting(false);
	}

	public boolean receiveRTS(Node sendingNodeName,boolean i,double packSize,double lastBackTime) throws InterruptedException
	{
		//if busy or if another node got here first or if node is current receiving
		if(getIdle() == false || this.getReceivedCTS() || this.getSentCTS())
		{
			return false;
		}
		else
		{
			setIdle(false);
			setReceivedRTS(true);
			sendingNodeName.setIdle(false);

			sentCTS(sendingNodeName,packSize);
			//If a node had to backoff but then got through
			if(i == true)
			{
				sendingNodeName.setAverageBackOff((sendingNodeName.getAverageBackOff() + lastBackTime)/2);
			}
			return true;
		}
	}

	public void sentCTS(Node nodeToCTS,double packetSize) throws InterruptedException
	{
		this.setSentCTS(true);
		//if(idle) System.out.println("ERROR");
		for(Node i: nodesInRange){
			if(i.equals(nodeToCTS) == true)
			{
				double distance = (int) distance(nodeToCTS);
				//prop delay
				double propDelay = distance/speedOfLight;
				sleep(0,(int) (propDelay*1000000000.0));
				i.receiveCTS(this,packetSize);
			}
			else
			{
				i.setWaiting(true);
			}
		}
	}

	public void receiveCTS(Node receiverName,double packetSize) throws InterruptedException{
		double distance = distance(receiverName);
		//prop delay
		double propDelay = distance/speedOfLight;
		double transmitDelay = packetSize/BAND_WIDTH;
		double delay = propDelay + transmitDelay;
		//System.out.println(""+delay);
		transDelay = transDelay + transmitDelay;
		sum = sum + delay;
		if(s!=null)
		{
			this.s.allDelays.add(delay);
		}
		if(vary !=null)
		{
			this.vary.add(delay);
		}
		//Add frame size to total
		this.setTotalData(this.getTotalData() + packetSize);
		//System.out.println((int) (delay * 1000.0));
		this.setReceivedCTS(true);
		sleep((int) (delay*1000.0),(int) ((delay)*1000000000.0)%1000000);

		//System.out.println("Packet #" + j + "From " + this.getNodeName() + " Done Packet");
		requestAck(receiverName, delay);
	}

	public void requestAck(Node receivingNode, double delay) throws InterruptedException
	{
		//receivingNode.reset();
		for(Node i:nodesInRange)
		{

			if(i.getNodeName().equals(receivingNode.getNodeName()))
			{

				if(i.getAck(receivingNode,delay) == true)
				{
					//sucessfully sent frame?
				}
				else
				{
					//if failed (in this simulation there are never any packets lost for now
					//decrement the currentFile
					this.setcurrentFile(this.getcurrentFile() - 1);
				}
			}
		}
	}

	public boolean getAck(Node sendingNodeName, double delay) throws InterruptedException
	{
		this.reset();
		for(Node i: nodesInRange)
		{

			if(i.equals(sendingNodeName) == false)
			{
				i.setWaiting(false);
			}
		}
		//received data and return an ACK

		double distance = (int) distance(sendingNodeName);

		//prop delay
		double propDelay = distance/speedOfLight;
		sleep(0,(int) (propDelay*1000000000.0));
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
	//rate is avg x pkt/sec
	//@returns count - 1 aka arrival rate
	private int poisson(double rate)
	{
		double sum = 0;
		int count = 0;
		while(sum < 1.0)
		{
			sum = sum + exponential(rate);
			count++;
		}
		return count - 1;
	}

	//rate is avg x pkt/sec and 1/exponential = actual packet size
	// @return 1/(time until next event aka actual pkt size)
	private double exponential(double rate)
	{
		Random rand = new Random();
		double U = rand.nextDouble();
		if(U < .00001)
		{
			U = .00001;
		}
		return -Math.log(U)/rate;
	}

	private double jitter(ArrayList<Double> delays)
	{
		double sum = 0;
		double avg = 0;
		double jit = 0;
		double num = (double) delays.size();
		for(int i = 0; i < (int)num;i++)
		{
			try
			{
				sum = sum + delays.get(i);
			}
			catch(Exception e)
			{
				continue;
			}
		}
		avg = sum / num;
		for(int i = 0; i < (int)num;i++)
		{
			try
			{
				double D = delays.get(i) - avg;
				jit = jit + (D*D)/num;
			}
			catch(Exception e)
			{
				continue;
			}
		}
		return jit;
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

	public void setAverageBackOff(double averageBackOff) {
		this.averageBackOff = averageBackOff;
	}

	public double getAverageBackOff() {
		return averageBackOff;
	}
	public long getTime()
	{
		return timeElapsed;
	}
	public void setTime(long time)
	{
		timeElapsed = time;
	}
	public void setTotalData(double data)
	{
		totalData = data;
	}
	public double getTotalData()
	{
		return totalData;
	}
	public void setTest(SimulationTest sim)
	{
		this.s = sim;
	}
}
