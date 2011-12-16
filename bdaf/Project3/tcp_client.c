#include <sys/socket.h>
#include <stdio.h>
#include <netdb.h>
#include <string.h>
#include <time.h>
#include <sys/socket.h>
#include <windows.h>


#define PORT 5432


#define MAXDATASIZE 1024							/* note: At MAXDATASIZE > 7300, must loop over recv() */
#define LOOP 100

int main(int argc, char* argv[]) {
	int sockfd, numbytes;
	time_t begintime,endtime;

	char buf[MAXDATASIZE + 1];
	char msg[MAXDATASIZE + 1];

	int i, count = 0, loss = 0;
	int totalBytesReceived = 0, totalBytesSent = 0;

	// Initialize message to send
	char letter = '0';
	for (i = 0; i < MAXDATASIZE; i++) {
		msg[i] = letter;
	}

	struct hostent* he;
	struct sockaddr_in their_addr;									/* connector's address info */

	if (argc != 2) {
		fprintf(stderr, "usage: client hostname\n");
		exit(1);
	}


	time(&begintime);

	// TRANSMISSION LOOP
	for (i = 0; i < LOOP; i++) {

		if ((he = gethostbyname (argv[1])) == NULL) {				/* get the host info */
			perror("gethostbyname");
		}
		if ((sockfd = socket (AF_INET, SOCK_STREAM, 0)) == -1) {
			perror("socket");
		}
		their_addr.sin_family = AF_INET; 							/* interp�d by host */
		their_addr.sin_port = htons(PORT);
		their_addr.sin_addr = *((struct in_addr*)he->h_addr);
		bzero (&(their_addr.sin_zero), 8);
		if (connect (sockfd, (struct sockaddr*)&their_addr,sizeof(struct sockaddr)) == -1) {
			perror("connect");
			exit (1);
		}

		// SENDING MESSAGE
		numbytes = send(sockfd, msg, MAXDATASIZE, 0);

		if (numbytes == -1) {
			perror("send");
			loss++;
			numbytes = 0;
		}
		else if (numbytes == 0) {
			loss++;
			printf("Received nothing\n");
		}
		else {
			count++;
			printf("INFO: count = %d\n", count);
			printf("Sent %d bytes.\n", numbytes);
		}
		totalBytesSent+=numbytes;

		int b = 0;
		while (b < MAXDATASIZE) {

			// RECEIVING MESSAGE
			numbytes = recv(sockfd, buf, MAXDATASIZE, 0);

			if (numbytes == -1) {
				perror("recv");
				loss++;
				numbytes = 0;
			}
			else if (numbytes == 0)
			{
				loss++;
				printf("Received nothing\n");
			}
			else {
				buf[numbytes] = '\0';
				printf("Received %d bytes.\n", numbytes);
			}

			totalBytesReceived+=numbytes;
			b += numbytes;

		}

	}
<<<<<<< .mine
=======

	// TCP option, uncomment to enable, currently disabled
	/*
	BOOL bOptVal = TRUE;
	int bOptLen = sizeof(BOOL);
	int iOptVal;
	int iOptLen = sizeof(int);
>>>>>>> .r170

<<<<<<< .mine
=======
	if (setsockopt(sockfd, SOL_SOCKET, SO_KEEPALIVE, (char*)&bOptVal, bOptLen) != -1) {
	    printf("Set SO_KEEPALIVE: ON\n");
	  }

	*/
>>>>>>> .r170
	close(sockfd);

	time(&endtime);

	printf("\n----- TCP SUMMARY -----\n");

	double totalLatency = difftime(endtime,begintime);
	if (totalLatency == 0.0) {
		totalLatency = 0.001;
	}
	double throughput = totalBytesReceived/totalLatency;
	double throughput2 = totalBytesSent/totalLatency;
	double avgRTT = totalLatency / LOOP;
	printf("Average RTT = %.2f seconds.\n", avgRTT);
	printf("Throughput (recving) = %.0lf bytes per second.\n", ++throughput);
	printf("Throughput (sending) = %.0lf bytes per second.\n", ++throughput2);
	printf("Loss = %d", loss);
	close(sockfd);

	return 0;
}
