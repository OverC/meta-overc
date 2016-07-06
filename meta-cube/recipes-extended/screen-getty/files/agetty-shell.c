/*
 * agetty-shell.c  wrap agetty for use with screen's shell variable
 *
 * Copyright (c) 2016 Wind River Systems, Inc. - Jason Wessel
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
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>
#include <string.h>

void do_exec() {
	execl("/sbin/agetty","agetty","-", NULL);
}

int main(int argc, char *argv[]) {
	int c;
	int status;
	pid_t mypid = getpid();
	int args = 1;
	int loop = 0;
	
	while (args < argc) {
		if (strcmp("--loop", argv[args]) == 0) {
			loop = 1;
		}
		args++;
	}

	if (!loop)
		do_exec();

	signal(SIGINT, SIG_IGN);
	do {
		signal(SIGTTOU, SIG_DFL);
		c = fork();
		if (c < 0) {
			printf("ERROR: exec failed\n");
			sleep(5);
			return -1;
		}
		if (c == 0) {
			pid_t pid = getpid();
			setpgid(pid,pid);
			do_exec();
		} else {
			tcsetpgrp(0, c);
			waitpid(c, &status, 0);
			signal(SIGTTOU, SIG_IGN);
			tcsetpgrp(0, mypid);
			/* Provide a short break in case login is spawning infinitely */
			sleep(1);
		}
	} while(1);

	return 0;
}
