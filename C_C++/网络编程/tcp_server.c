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
	int ret;
	int cfd;
	struct sockaddr_in sAddr;
	struct sockaddr_in cAddr;


	sfd = 0;
	ret = 0;
	cfd = 0;
	memset(&sAddr, 0, sizeof(sAddr));
	memset(&cAddr, 0, sizeof(cAddr));
	

	sfd = socket(AF_INET, SOCK_STREAM, 0);
	if (-1 == sfd)
	{
		printf("create socket faild!\n");
		return -1;
	}
	printf("create socket success!\n");
	sAddr.sin_family = AF_INET;
	sAddr.sin_port = htons(65533);
	sAddr.sin_addr.s_addr = inet_addr("127.0.0.1");



	ret = bind(sfd, (struct sockaddr *)&sAddr, sizeof(sAddr));
	if (-1 == ret)
	{
		printf("bind faild! errno = %d\n", errno);
		return -1;
	}
	printf("bind success!\n");
	
	ret = listen(sfd, 5);
	if (-1 == ret)
	{
		printf("listen faild!\n");
		return -1;
	}
	printf("listen success!\n");
	

	int len = sizeof(cAddr);
	cfd = accept(sfd, (struct sockaddr *)&cAddr, &len);
	if (-1 == cfd)
	{
		printf("accept faild!\n");
	}else
	{
		printf("accept success!\n");
	}

	char buff[]="HTTP/1.1 200 OK\r\nContent-Length: 5\r\n\r\nabcde\r\n";

	send(cfd, buff, sizeof(buff), 0);


	close(cfd);
	close(sfd);
	return 0;
}
