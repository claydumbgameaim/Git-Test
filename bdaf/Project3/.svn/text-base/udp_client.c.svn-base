#include <sys/socket.h>
#include <stdio.h>
#include <netdb.h>
#include <time.h>

#define PORT 5432


int main(int argc, char* argv[]) {
	int sockfd, numbytes;
	int sin_size;
	struct hostent* he;
	struct sockaddr_in their_addr;							/* connector's address info */
	time_t begintime,endtime;

	int maxDataSize = 0;									/* message size */
	int loop = 100;

	char * buf;												/* holds messages received */
	char * msg;												/* message to send to server */

	if (argc != 2) {
		fprintf(stderr, "usage: client hostname\n");
		exit(1);
	}

	while(maxDataSize <= 0)
	{
		//Ask for packet size
		printf("What message size do you want? (bytes)\n");
		scanf("%d",&maxDataSize);
	}

	buf = (char *) malloc(maxDataSize + 1);
	msg = (char *) malloc(maxDataSize);

	int i;
	for (i = 0; i < maxDataSize; i++)
	msg[i] = '0';

	//Ask for number of packet sent
	printf("How many packets do you want to send?\n");
	scanf("%d",&loop);

	if ((he = gethostbyname (argv[1])) == NULL) {			/* get the host info */
		perror("gethostbyname");
		exit (1);
	}

	if ((sockfd = socket (AF_INET, SOCK_DGRAM, 0)) == -1) {	/* SOCK_STREAM = TCP, SOCK_DGRAM = UDP */
		perror("socket");
		exit (1);
	}

	their_addr.sin_family = AF_INET; 						/* interp’d by host */
	their_addr.sin_port = htons(PORT);
	their_addr.sin_addr = *((struct in_addr*)he->h_addr);
	bzero (&(their_addr.sin_zero), 8);

	if (connect (sockfd, (struct sockaddr*)&their_addr,sizeof(struct sockaddr)) == -1) {
		perror("connect");
		exit (1);
	}

	sin_size = sizeof(struct sockaddr_in);

	// Start the timer
	time(&begintime);

	int count = 0, totalBytesReceived = 0, totalBytesSent = 0;
	for (i = 0; i < loop; i++) {
		// SEND MESSAGE TO SERVER
		numbytes = sendto(sockfd, msg, strlen(msg), 0, (const struct sockaddr *)&their_addr, sin_size);
		if (numbytes == -1) {
			perror("connect");
		}
		else {
			count++;
			printf("Sent %d bytes.\n", numbytes);
			printf("INFO: count = %d\n",count);
		}
		totalBytesSent += numbytes;

		// RECEIVE MESSAGE FROM SERVER
		numbytes = recvfrom(sockfd,buf,maxDataSize,0,(struct sockaddr *)&their_addr,&sin_size);

		totalBytesReceived += numbytes;
		buf[numbytes] = '\0';
		printf("Received %d bytes.\n", numbytes);						// shows bytes only
	//	printf("Received: %s | [%d bytes]", buf, numbytes);			// shows message received + bytes

	}

	// Stop the timer
	time(&endtime);


	printf("\n----- UDP SUMMARY -----\n");
	double totalLatency = difftime(endtime,begintime);
	if (totalLatency == 0.0)
		totalLatency = 0.001;
	double throughput = totalBytesReceived/totalLatency;
	double throughput2 = totalBytesSent/totalLatency;
	double avgRTT = totalLatency / loop;
	printf("Average RTT = %.2f seconds.\n", avgRTT);
	printf("Throughput (receiving) = %.0lf bytes per second.\n", throughput);
	printf("Throughput (sending) = %.0lf bytes per second.\n", throughput2);

	close(sockfd);
	return 0;
}
