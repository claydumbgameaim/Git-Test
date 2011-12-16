#include <sys/socket.h>
#include <netdb.h>
#include <sys/wait.h>
#include <stdio.h>

#define MYPORT 5432
#define MAXDATASIZE 65536									// Bytes

main()
{
	int sockfd, numbytes;
	struct sockaddr_in my_addr; 							/* my address */
	struct sockaddr_in their_addr; 							/* connector addr */
	int sin_size;
	char buf[MAXDATASIZE+1];

	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0))==-1){		/* SOCK_STREAM = TCP, SOCK_DGRAM = UDP */
		perror("socket");
		exit(1);
	}

	my_addr.sin_family = AF_INET; 							/* host byte order */
	my_addr.sin_port = htons(MYPORT); 						/* short, network byte order */
	my_addr.sin_addr.s_addr = htonl(INADDR_ANY);			/* automatically fill with my IP */
	bzero(&(my_addr.sin_zero), 8);    						/* zero struct */

	if (bind(sockfd, (struct sockaddr *)&my_addr,sizeof(struct sockaddr))== -1) {
		perror("bind");
		exit(1);
	}

	int count = 0;

	while(1) {
		sin_size = sizeof(struct sockaddr_in);
		// RECEIVE MESSAGE FROM CLIENT
		numbytes = recvfrom(sockfd,buf,MAXDATASIZE,0,(struct sockaddr *)&their_addr,&sin_size);
		if (numbytes == -1) {
			perror("recvfrom");
		} else {
			count++;
			printf("SERVER: got connection from %s\n",inet_ntoa(their_addr.sin_addr));
			printf("SERVER: count = %d\n", count);
			buf[numbytes] = '\0';
			printf("Received %d bytes.\n", numbytes);						// shows bytes only
//			printf("Received: %s [%d bytes]]\n", buf, numbytes);			// shows message received + bytes
		}

		// SEND MESSAGE RECEIVED FROM CLIENT BACK TO CLIENT
		numbytes = sendto(sockfd, buf, strlen(buf), 0, (const struct sockaddr *)&their_addr, sin_size);
		if (numbytes == -1) {
			perror("connect");
		} else
			printf("Sent %d bytes.\n", numbytes);

	}

}
