import java.util.*;


class PacketFeeder extends Thread
{
	private static final double AVERAGE_PER_SEC = 100.0;
	private static final int TIME = 10;
	public Queue<Double> p;
	public Node Node_;
	public double ctt = 0;
	//Constructor
	public PacketFeeder(Node node)
	{
		Node_ = node;
	}
	public void run()
	{
		double actualRate = poisson(AVERAGE_PER_SEC);

		ctt = ctt + actualRate;
		for(int i = 0; i < TIME;i++)
		{
				//give x ammount of packets
				p = new LinkedList<Double>();
				Random rand = new Random();
				for(int j = 0; j < (int) actualRate; j++)
				{
					p.offer(1/exponential(AVERAGE_PER_SEC));
					//p.offer(1000.0 * (rand.nextInt((int) 18) + .2));
				}
				Node_.feed(p);
			try
			{
				sleep(1000);
			}
			catch(Exception e){}
		}
		Node_.done = true;
	}
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
}
