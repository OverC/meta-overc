/*
 * daemonize-sigusr1-wait.c  Daemonize child after receiving sigusr1
 *
 * Copyright (c) 2017 Wind River Systems, Inc. - Jason Wessel
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

#define _GNU_SOURCE
#include <signal.h>
#include <stdio.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>

static void daemonize(void)
{
	int ret = fork();
	if (ret == 0) {
		exit(0);
	}
}

static void sig_handler(int signo)
{
	if (signo == SIGUSR1) {
		daemonize();
	}

	return;
}

int main( int argc, char *argv[])
{
	int ret;
	int status;
	char *env_str;

	if( signal( SIGUSR1, sig_handler) == SIG_ERR  )	{
		perror("ERROR: Could not create handler SIGUSR1");
		exit(-1);
	}

	/* Run child process */
	if (argc < 2) {
		printf("Usage: %s <child command>\n", argv[0]);
		exit(-1);
	}

	if (!asprintf(&env_str, "%i", getpid())) {
		perror("Alloc failed");
		exit(-1);
	}

	setenv("SIGUSR1_PARENT_PID", env_str, 1);

	argv += 1;
	ret = fork();

	if (ret == 0) {
		/* Child */
		execvp(argv[0], argv);
		exit(-1);
	}
	if (ret < 0) {
		printf("Error fork failed\n");
		exit(-1);
	}

	wait(&status);
	return(status);
}
