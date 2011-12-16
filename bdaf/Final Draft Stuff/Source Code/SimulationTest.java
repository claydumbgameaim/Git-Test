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
	//Done variables for simulation
	public static boolean done;
	public static boolean answer;
	//Array with all the delays
	public static ArrayList<Double> allDelays = new ArrayList<Double>();

	public void run()
	{
		Simulation simulator = new Simulation();

		ArrayList<Node> theSenders = new ArrayList<Node>();
		ArrayList<Node> theReceivers = new ArrayList<Node>();
		ArrayList<Integer> theSyncLengths = new ArrayList<Integer>();
		slaves = 0;
		Random rand = new Random();

		// Generates Nodes
		char name = 'A';
		//Ask for an input
		while(!done)
		{
			ArrayList<Node> allNodes = new ArrayList<Node>();
			answer = false;
			name = 'A';
			System.out.println("Would you like Asynchronous [0] or Synchronous [1]? Please enter 1 or 0.");
			Scanner in = new Scanner(System.in);
			int option = in.nextInt();
			if(option == 0 || option == 1)
			{
				syncer = option;
				System.out.println("How many nodes would you like?");
				NUMBER_OF_NODES = in.nextInt();
				if(NUMBER_OF_NODES == 1)
				{
					System.out.println("You say 1. I say 2.");
					NUMBER_OF_NODES++;
					System.out.println("NUMBER OF NODES is now" + NUMBER_OF_NODES);
				}
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
			ArrayList<Thread> nodes = new ArrayList<Thread>();
			simulator.startSimulation(theSenders, theReceivers, theSyncLengths);
			for(int i = 0; i < NUMBER_OF_NODES;i++)
			{
				Thread thread1 = new Thread(allNodes.get(i));
				nodes.add(thread1);
				allNodes.get(i).setTest(this);
				thread1.start();
			}
			for(int j = 0; j < NUMBER_OF_NODES; j++)
			{
			    try
			    {
			     	nodes.get(j).join();
			    }
			    catch(InterruptedException ie)
			    {
			     	System.err.println(ie.getMessage());
			    }
			    finally
			    {

			            slaves++;
        		}
			}
			System.out.println("");
			System.out.println("Overall jitter is...");
			System.out.println("*drumroll*");
			System.out.println(jitter(allDelays) + " seconds");
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
						System.exit(0);
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
}
