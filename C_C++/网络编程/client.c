#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>



#define PORT 12345
#define IPADDR  "172.20.1.206"
int main(int argc, const char *argv[])
{
	int sfd = socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in servaddr;
	memset(&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(PORT);
	servaddr.sin_addr.s_addr = inet_addr(IPADDR);



	int ret;
	char sendBuffer[1024]={0};
	char recvBuffer[1024]={0};

	while(1)
	{
		
		printf("please input text:\n");
		scanf("%s", sendBuffer);
		
		sendto(sfd, sendBuffer, strlen(sendBuffer), 0, (struct sockaddr*)&servaddr, sizeof(servaddr));
		memset(sendBuffer, 0, sizeof(sendBuffer));
	}
	return 0;
}
