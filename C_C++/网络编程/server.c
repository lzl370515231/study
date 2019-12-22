
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>


#define PORT 12345
int main(int argc, const char *argv[])
{
	int sfd;
	int ret;
	char recvBuffer[1024]={0};
	sfd = socket(AF_INET, SOCK_DGRAM, 0);
	struct sockaddr_in servaddr, cliaddr;
	memset(&servaddr, 0, sizeof(servaddr));
	memset(&cliaddr, 0, sizeof(cliaddr));

	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(PORT);
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);

	ret = bind(sfd, (struct sockaddr *)&servaddr, sizeof(servaddr));
	if (ret < 0)
	{
		printf("bind error : %d\n", errno);
		return -1;
	}
	int len = sizeof(servaddr);
	while(1)
	{
		ret = recvfrom(sfd, recvBuffer, sizeof(recvBuffer), 0, (struct sockaddr *)&servaddr, &len);
		if (ret <= 0)
		{
			printf("recvfrom error :%d\n", errno);
			
		}else
		{
			printf("recvie data: %s\n", recvBuffer);
			memset(recvBuffer, 0, sizeof(recvBuffer));
		}
	}

	return 0;
}
