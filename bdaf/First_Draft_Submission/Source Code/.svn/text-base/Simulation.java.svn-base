import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;
/**
	Instances of this class simulate IEEE 802.11 (Wireless LAN) and its MAC.

*/
public class Simulation
{

	private static final int MAX_BACKOFF_ATTEMPTS = 16;
	private static final double BACKOFF_VALUE = 0.0000512;			// time in seconds

	private static final double VIDEO_BIT_RATE = 1536;					// kilobits per second
	private static final double VOICE_BIT_RATE = 64;					// kilobits per second

	private static final double FRAME_MAX_SIZE = 18.328125;				// 18768 bits = 18.328125 kb
	private static final double PAYLOAD_MAX_SIZE = 18.0625;				// 18496 bits = 18.0625 kb
	private static final double FRAME_OVERHEAD_SIZE = FRAME_MAX_SIZE - PAYLOAD_MAX_SIZE;

	// Constructor
	public Simulation() {}


	/**
		Performs simulation.
		@param sendingNodes The source Nodes.
		@param receivingNodes The destination Nodes.
		@param syncTransmissionLength The length of a synchronous transmission in seconds.
												O = asynchronous transmission
	*/
	public void startSimulation(ArrayList<Node> sendingNodes, ArrayList<Node> receivingNodes,
											ArrayList<Integer> syncTransmissionLength)
	{
		int numberOfCompletedTransfers = 0;
		int totalNumberOfTransfers = sendingNodes.size();

		while (numberOfCompletedTransfers < totalNumberOfTransfers)
		{

			for (int i = 0; i < totalNumberOfTransfers; i++)
			{

				Node sender = sendingNodes.get(i);
				Node receiver = receivingNodes.get(i);
				int syncLength = syncTransmissionLength.get(i);


				if (receiver.getIdle() == true && sender.getIdle() == true)
				{
					// RTS and CTS performed
					performHandshake(sender,receiver);


					// Operations for Asynchronous transmission
					if (syncLength == 0)
					{
						//Check if the sender has files to send
						int numFiles = sender.getNumOfFiles();

						if(numFiles > 0)
						{
							//Get the first file size
							double fileSize = sender.getFiles().poll();

							//Lower number of files
							numFiles--;
							sender.setNumOfFiles(numFiles);

							//Test simulation
							int simStatus = performAsyncTransmission(fileSize,sender,receiver);
							numberOfCompletedTransfers += simStatus;
							if (simStatus == 1)
							{
								sender.reset();
								receiver.reset();
//								sendingNodes.remove(i);
//								receivingNodes.remove(i);
							}
						}

					}
					// Operations for Synchronous transmission
					else if (syncLength > 0)
					{

						int simStatus = performSyncTransmission(syncLength, sender, receiver);
						numberOfCompletedTransfers += simStatus;

						if (simStatus == 1)
						{
							sender.reset();
							receiver.reset();
//							sendingNodes.remove(i);
//							receivingNodes.remove(i);
						}
					}

				}
				// Operations for sender backoff
				else if (receiver.getIdle() == false && sender.getIdle() == true)
				{
					int simStatus = performBackoff(sender);
					numberOfCompletedTransfers += simStatus;
					if (simStatus == 1)
					{
						sender.reset();
						sendingNodes.remove(i);
						receivingNodes.remove(i);
					}

				}




			}
		}
	}




	/** Helper method to perform CTS, RTS handshake. 	*/
	private void performHandshake(Node sender, Node receiver)
	{
		ArrayList<Node> NodesInRange = receiver.getNodesInRange();
		for(Node i: NodesInRange)
		{	//For each node in receiver side if not the sender node
			if(!i.getNodeName().equals(sender.getNodeName()))
			{
				i.setWaiting(true);
			}
		}
		sender.setIdle(false);
		receiver.setIdle(false);

		sender.setSentRTS(true);
		receiver.setSentCTS(true);
		sender.setReceivedCTS(true);
	}

	private double poisson(double rate, int n)
	{
		int factorial = 1;
		for(int i = 1; i <= n; i++)
		{
			factorial = factorial * i;
		}
		return (Math.pow(rate,(double)n)*Math.exp(-rate))/(double)factorial;
	}

	private double exponential(double rate, int n)
	{
		return rate*Math.exp(-rate*n);
	}
	/**
		Helper method to perform asynchronous transmission.
		@param fileSize The size of the file being sent in bytes.
		@param sender The source Node.
		@param receiver The destination Node.
		@return 	1 if whole file successfully transmitted
					0 if file did not successfully transmit
	*/
	private int performAsyncTransmission(double fileSize, Node sender, Node receiver)
	{

		Random rand = new Random();
		//Probability of failure
		int p = rand.nextInt(10);
		//Dice Roll well out of 100
		int dice = rand.nextInt(100);
		if(dice <= p)
		{
			return 0;
		}



		return 10000;		// temporary

	}
	/**
		Helper method to perform synchronous transmission for video.
		@param length The total time of the transmission in seconds.
		@param sender The source Node.
		@param receiver The destination Node.
		@return 	1 if transmission successfully terminates (complete length was reached)
					0 if transmission did not successfully terminate (length cut short)
	*/
	private int performSyncTransmission(int length, Node sender, Node receiver)
	{
		double amountOfDataToSend = VIDEO_BIT_RATE * length;

		// Rounds packets up (i.e. 12.4 payloads --> 13 payloads)
		int numberOfPayloads = (int)Math.round(amountOfDataToSend/PAYLOAD_MAX_SIZE + 0.5);

		double totalDataReceived = numberOfPayloads * FRAME_OVERHEAD_SIZE + amountOfDataToSend;

		double packetArrivalRate = 2;												// 2 packets per second
		double transmitLength = numberOfPayloads/packetArrivalRate;
		double throughput = totalDataReceived/length;				// divide by length
		//Do we have chance of fail here too? Then add more delay?
		/*
		System.out.println("amt data = " +amountOfDataToSend);
		System.out.println("num payloads = " +numberOfPayloads);
		System.out.println("total data = " + totalDataReceived);



		System.out.println("Synchronous transmission from " +sender.getName()
									+ " to " +receiver.getName()
									+ " for " +length +" seconds : ");
		System.out.println("\t Throughput = " +throughput  +" kbps \n");
		*/
		return 1;


		// IMPLEMENATION OF DELAY + JITTER

	}

	/**
		Helper method to calculate sender backoff.
		@param sender The Node that wants to send a file
		@return	1 if attempts > MAX_BACKOFF_ATTEMPTS
					0 if attempts < MAX_BACKOFF_ATTEMPTS
	*/
	private int performBackoff(Node sender)
	{
		int attempts = sender.getSendAttempts();
		double backoff = sender.getBackoff();

		if (attempts > MAX_BACKOFF_ATTEMPTS)
		{
			return 1;
		}
		else
		{
			attempts++;
			sender.setSendAttempts(attempts);

			// CALCULATION FOR BACKOFF
			int upperBound = (int)Math.pow(2,attempts) - 1;		// 2^n - 1

			Random rand = new Random();
			int k = rand.nextInt(upperBound);

			sender.setBackoff(k * BACKOFF_VALUE);

			return 0;
		}
	}



	private void generateReport()
	{
		// not sure if inputTrafficLoad is needed
		double inputTrafficLoad = FRAME_MAX_SIZE * 2;

	}

	private void generateTraffic()
	{
	}

	private boolean isRange(Node sender, Node receiver)
	{
		if(	Math.pow(sender.getx_Cord() - receiver.getx_Cord(),2) + Math.pow(sender.gety_Cord() - receiver.gety_Cord(), 2) <= 35 * 35) return true;
		return false;
	}

}
