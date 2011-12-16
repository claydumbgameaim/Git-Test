import java.util.ArrayList;
import java.util.Random;

public class SimulationTest
{
	private static final int NUMBER_OF_NODES = 10;
	private static final int NUMBER_OF_TRANSMISSIONS = 20;
	private static final int MAX_LENGTH_SYNC = 10800;				// 3 hours = 10800 seconds


	public static void main(String[] args)
	{
		Simulation simulator = new Simulation();

		ArrayList<Node> theSenders = new ArrayList<Node>();
		ArrayList<Node> theReceivers = new ArrayList<Node>();
		ArrayList<Integer> theSyncLengths = new ArrayList<Integer>();
		ArrayList<Node> allNodes = new ArrayList<Node>();

		Random rand = new Random();

		// Generates Nodes
		char name = 'A';

		for (int i = 0; i < NUMBER_OF_NODES; i++)
		{
			Node n = new Node();
			n.setName(name +"");
			allNodes.add(n);
			name++;
		}



		// Generates random sender and receiver
		int numberOfCreatedTransmissions = 0;

		while (numberOfCreatedTransmissions < NUMBER_OF_TRANSMISSIONS)
		{
			int senderIndex = rand.nextInt(NUMBER_OF_NODES);
			int receiverIndex = rand.nextInt(NUMBER_OF_NODES);

			// Senders should not be able to send to itself
			if (senderIndex != receiverIndex)
			{
				Node s = allNodes.get(senderIndex);
				Node r = allNodes.get(receiverIndex);

				if (s.isRange(r) && r.isRange(s))
				{
					theSenders.add(s);
					theReceivers.add(r);
					numberOfCreatedTransmissions++;
				}

			}
		}


/**
		//See what is in what range
		for(int i = 0; i < sendingNodes.size();i++)
		{
			for(int j = 0; j < receivingNodes.size();j++)
			{
				if(isRange(sendingNodes.get(i),receivingNodes.get(j))
				{
					sendingNodes.getRange().add(receivingNodes.get(j));
				}
		}
*/


		// Creates only synchronous transmissions
		// WILL BE MODIFIED TO BE ABLE TO TAKE ASYNCHRONOUS TRANSMISSIONS (syncLength = 0)
		int transmissionCount = 0;
		while (transmissionCount < NUMBER_OF_TRANSMISSIONS)
		{
			theSyncLengths.add(rand.nextInt(MAX_LENGTH_SYNC));
			transmissionCount++;
		}






		// LIST FOR TESTING
		System.out.println("~~~ LIST OF TRANSMISSIONS ~~~");
		for (int i = 0; i < NUMBER_OF_TRANSMISSIONS; i++)
		{

			System.out.println("  "
									+ theSenders.get(i).getName() +" to "
									+ theReceivers.get(i).getName() + " for "
									+ theSyncLengths.get(i) +" seconds");

		}
		System.out.println(" ");



		// Runs the simulation
		simulator.startSimulation(theSenders, theReceivers, theSyncLengths);

	}



}