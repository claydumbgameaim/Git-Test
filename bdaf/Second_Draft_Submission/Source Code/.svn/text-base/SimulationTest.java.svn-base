import java.awt.event.ActionListener;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;

public class SimulationTest
{
	private static int NUMBER_OF_NODES = 10;
	private static final int NUMBER_OF_TRANSMISSIONS = 20;
	private static final int MAX_LENGTH_SYNC = 10800;				// 3 hours = 10800 seconds
	public static int time = 0;
	//variable for picking aync or not
	public static int syncer = 1;
	//variable for number of nodes alive
	public static int slaves = 0;
	//Keep track of nodes alive
	private static Node[] nodes = new Node[NUMBER_OF_NODES];
	//Done variables for simulation
	public static boolean done;
	public static boolean answer;

	public static void main(String[] args)
	{
		Simulation simulator = new Simulation();

		ArrayList<Node> theSenders = new ArrayList<Node>();
		ArrayList<Node> theReceivers = new ArrayList<Node>();
		ArrayList<Integer> theSyncLengths = new ArrayList<Integer>();
		ArrayList<Node> allNodes = new ArrayList<Node>();
		slaves = 0;
		Random rand = new Random();

		// Generates Nodes
		char name = 'A';
		//Ask for an input
		while(!done)
		{
			System.out.println("Would you like Asynchronous [0] or Synchronous [1]? Please enter 1 or 0.");
			Scanner in = new Scanner(System.in);
			int option = in.nextInt();
			if(option == 0 || option == 1)
			{
				syncer = option;
				System.out.println("How many nodes would you like?");
				NUMBER_OF_NODES = in.nextInt();
			}
			else
			{
				System.out.println("Please enter a 0 or 1 with Async as 1 and Sync as 0");
				continue;
			}
			//Make and add Nodes
			for (int i = 0; i < NUMBER_OF_NODES; i++)
			{
				Node n = new Node();
				n.setNodeName(name +"");
				n.sync = syncer;
				allNodes.add(n);
				nodes[i] = n;
				name++;
			}

			for (int i = 0; i < NUMBER_OF_NODES; i++)
			{
				for (int j = 0; j < NUMBER_OF_NODES; j++)
				{
					if(j!=i)
					{
						if(allNodes.get(i).isRange(allNodes.get(j)))
						{
							allNodes.get(i).AddInRangeNode(allNodes.get(j));
						}
					}
				}
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
					theSenders.add(allNodes.get(senderIndex));
					theReceivers.add(allNodes.get(receiverIndex));

					numberOfCreatedTransmissions++;
				}
			}

			// Creates only synchronous transmissions
			// WILL BE MODIFIED TO BE ABLE TO TAKE ASYNCHRONOUS TRANSMISSIONS (syncLength = 0)
			int transmissionCount = 0;
			while (transmissionCount < NUMBER_OF_TRANSMISSIONS)
			{
				theSyncLengths.add(rand.nextInt(MAX_LENGTH_SYNC));
				transmissionCount++;
			}




	/*

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
			*/
			// Runs the simulation
			simulator.startSimulation(theSenders, theReceivers, theSyncLengths);
			for(int i = 0; i < 10;i++)
			{
				Thread thread1 = new Thread(allNodes.get(i));
				thread1.start();
			}
			for(int j = 0; j < NUMBER_OF_NODES; j++)
			{
			    try
			    {
			     	nodes[j].join();
			    }
			    catch(InterruptedException ie)
			    {
			     	System.err.println(ie.getMessage());
			    }
			    finally
			    {
					System.out.println("Node " + j+ " died");
			            slaves++;
        		}
			}
			if(slaves >= NUMBER_OF_NODES)
			{
				while(!answer)
				{
					System.out.println("Would you like to exit? YES = 1, NO = 0.");
					int cont = in.nextInt();
					if(cont == 1)
					{
						done = true;
						answer = true;
					}
					else if(cont == 0)
					{
						answer = true;
					}
					else
					{
						System.out.println("Please input 1 or 0 accordingly.");
						answer = false;
					}
				}
			}

		}

	}



}
