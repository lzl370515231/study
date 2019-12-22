#include <stdio.h>
#include <sys/socket.h>
#include <assert.h>
#include <sys/types.h>
#include <errno.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>




int main(int argc, const char *argv[])
{
	int sfd;
	struct sockaddr_in sAddr;

	sfd = 0;
	memset(&sAddr, 0, sizeof(sAddr));


	sfd = socket(AF_INET, SOCK_STREAM, 0);
	if (-1 == sfd)
	{
		printf("create socket faild!\n");
		return -1;
	}

	sAddr.sin_family = AF_INET;
	sAddr.sin_port = htons(65533);
	sAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

	int ret = connect(sfd, (struct sockaddr *)&sAddr, sizeof(sAddr));
	if (-1 == ret)
	{
		printf("connect error!\n");
		return -1;
	}
	char buff[1024]={0};
	ret = recv(sfd, buff, sizeof(buff), 0);
	if (-1 != ret)
	{
		printf("recv = %s\n", buff);
	}

	close(sfd);

	return 0;
}
