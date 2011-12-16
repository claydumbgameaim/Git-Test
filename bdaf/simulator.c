#include <iostream>
#include <math.h>
#include <queue>
typedef struct packet
{
	int packNum;
}packet;

void main(void)
{
	queue<packet> queuePackets;
}
void SimulationStart(void)
{
	
}
void addQueue(struct *packet)
{
	queuePackets.push(packet);
}
int removeQueue()
{
	int ID;
	ID = queuePackets.front().packNum;
	queuePackets.pop();
}
void processArrival()
{

}
void processDeparture()
{

}
void SimulationEnd()
{
	ReportGeneration();
}
void ReportGeneration()
{

}