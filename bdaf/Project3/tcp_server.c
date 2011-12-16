#include <sys/socket.h>
#include <netdb.h>
#include <sys/wait.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <windows.h>

#define MYPORT 5432
#define BACKLOG SOMAXCONN
#define SOCKET_ERROR -1

#define MAXDATASIZE 1024

main()
{
	int sockfd, new_fd;
	struct sockaddr_in my_addr; 					/* my address   */
	struct sockaddr_in their_addr; 					/* connector addr */
	int sin_size;
	int totalBytesReceived = 0, totalBytesSent = 0;
	int numbytes, count;

	char buf[MAXDATASIZE + 1];

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0))==-1){
		perror("socket");
		exit(1);
	}

	my_addr.sin_family = AF_INET; 					/* host byte order */
	my_addr.sin_port = htons(MYPORT); 				/* short, network byte order   */
	my_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	/* automatically fill with my IP */
	bzero(&(my_addr.sin_zero), 8);    				/* zero struct */

	if (bind(sockfd, (struct sockaddr *)&my_addr,sizeof(struct sockaddr))== -1) {
		perror("bind");
		exit(1);
	}

	// TCP option, uncomment to enable, currently disabled

	BOOL bOptVal = TRUE;
	BOOL bOptVal2 = TRUE;
		int bOptLen = sizeof(BOOL);
		int iOptVal;
		int iOptVal2;
		int iOptLen = sizeof(int);
		if (getsockopt(sockfd, SOL_SOCKET, SO_KEEPALIVE, (char*)&iOptVal, &iOptLen) != SOCKET_ERROR)
		{
			printf("Previous SO_KEEPALIVE Value: %ld\n", iOptVal);
		}

		if (setsockopt(sockfd, SOL_SOCKET, SO_KEEPALIVE, (char*)&bOptVal, bOptLen) != SOCKET_ERROR)
		{
			printf("Set SO_KEEPALIVE: ON\n");
		}

		if (getsockopt(sockfd, SOL_SOCKET, SO_KEEPALIVE, (char*)&iOptVal, &iOptLen) != SOCKET_ERROR)
		{
			printf("Current SO_KEEPALIVE Value: %ld\n", iOptVal);
		}

		if (getsockopt(sockfd, SOL_SOCKET, SO_DEBUG, (char*)&iOptVal2, &iOptLen) != SOCKET_ERROR)
		{
			printf("Previous SO_DEBUG Value: %ld\n", iOptVal);
		}

		if (setsockopt(sockfd, SOL_SOCKET, SO_DEBUG, (char*)&bOptVal2, bOptLen) != SOCKET_ERROR)
		{
			printf("Set SO_DEBUG: ON\n");
		}

		if (getsockopt(sockfd, SOL_SOCKET, SO_DEBUG, (char*)&iOptVal2, &iOptLen) != SOCKET_ERROR)
		{
			printf("Current SO_DEBUG Value: %ld\n", iOptVal);
		}

	if (listen(sockfd, BACKLOG) == -1) {
		perror("listen");
		exit(1);
	}

	count = 0;


	while(1) {
		sin_size = sizeof(struct sockaddr_in);
		if ((new_fd = accept(sockfd, (struct sockaddr*)&their_addr,&sin_size))== -1) {
			perror("accept");
			continue;
		}

		printf("SERVER: got connection from %s\n",
		inet_ntoa(their_addr.sin_addr));

		int b = 0;

		// loop for recv() to collect all data
		while (b < MAXDATASIZE) {

			// RECEIVING MESSAGE
			numbytes = recv(new_fd, buf, MAXDATASIZE,0);

			if (numbytes == -1) {
				perror("recv");
			}
			else if (numbytes == 0){
				printf("Received nothing\n");
			}
			else {
				count++;
				printf("SERVER: count = %d\n", count);
				buf[numbytes] = '\0';
				printf("Received %d bytes.\n", numbytes);

			}
			totalBytesReceived += numbytes;
			b += numbytes;


			// SENDING MESSAGE
			numbytes = send(new_fd, buf, strlen(buf), 0);
			if (numbytes == -1) {
				perror("send");
			}
			else {
				buf[numbytes] = '\0';
				printf("Sent %d bytes.\n", numbytes);
			}
			totalBytesSent += numbytes;

		}


		close(new_fd);

		printf("TOTAL BYTES RECV = %d \n", totalBytesReceived);
		printf("TOTAL BYTES SENT = %d \n", totalBytesSent);
	}

}
