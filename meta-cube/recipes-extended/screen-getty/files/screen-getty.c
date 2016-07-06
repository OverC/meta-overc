/*
 * screen-getty.c  Start screen on a tty in place of getty
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
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <grp.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <linux/limits.h>
#include <termios.h>
#include <sys/ioctl.h>
#include <stdarg.h>
#include <string.h>


#define _(Text) (Text)

#define	MAX_SPEED	10		/* max. nr. of baud rates */

struct options {
    int     flags;			/* toggle switches, see below */
    int     timeout;			/* time-out period */
    char   *login;			/* login program */
    char   *tty;			/* name of tty */
    char   *initstring;			/* modem init string */
    char   *issue;			/* alternative issue file */
    int     numspeed;			/* number of baud rates to try */
    int     speeds[MAX_SPEED];		/* baud rates to be tried */
    int     eightbits;			/* assume 8bit-clean tty */
};

#define	F_PARSE		(1<<0)		/* process modem status messages */
#define	F_ISSUE		(1<<1)		/* display /etc/issue */
#define	F_RTSCTS	(1<<2)		/* enable RTS/CTS flow control */
#define F_LOCAL		(1<<3)		/* force local */
#define F_INITSTRING    (1<<4)		/* initstring is set */
#define F_WAITCRLF	(1<<5)		/* wait for CR or LF */
#define F_CUSTISSUE	(1<<6)		/* give alternative issue file */
#define F_NOPROMPT	(1<<7)		/* don't ask for login name! */
#define F_LCUC		(1<<8)		/* Support for *LCUC stty modes */

struct Speedtab {
    long    speed;
    int     code;
};

static struct Speedtab speedtab[] = {
    { 50, B50 },
    { 75, B75 },
    { 110, B110 },
    { 134, B134 },
    { 150, B150 },
    { 200, B200 },
    { 300, B300 },
    { 600, B600 },
    { 1200, B1200 },
    { 1800, B1800 },
    { 2400, B2400 },
    { 4800, B4800 },
    { 9600, B9600 },
#ifdef	B19200
    { 19200, B19200 },
#endif
#ifdef	B38400
    { 38400, B38400 },
#endif
#ifdef	EXTA
    { 19200, EXTA },
#endif
#ifdef	EXTB
    { 38400, EXTB },
#endif
#ifdef B57600
    { 57600, B57600 },
#endif
#ifdef B115200
    { 115200, B115200 },
#endif
#ifdef B230400
    { 230400, B230400 },
#endif
    { 0, 0 },
};

/* bcode - convert speed string to speed code; return 0 on failure */
int
bcode(s)
     char   *s;
{
    struct Speedtab *sp;
    long    speed = atol(s);

    for (sp = speedtab; sp->speed; sp++)
	if (sp->speed == speed)
	    return (sp->code);
    return (0);
}

int old_stderr;

#define error log_err
static void log_err(const char *fmt, ...)
{
    va_list ap;

    va_start(ap, fmt);
    vdprintf(old_stderr, fmt, ap);
	dprintf(old_stderr, "\n");
    va_end(ap);

    exit(EXIT_FAILURE);
}

static void log_warn(const char *fmt, ...)
{
    va_list ap;

    va_start(ap, fmt);
    vdprintf(old_stderr, fmt, ap);
	dprintf(old_stderr, "\n");
    va_end(ap);
}

void usage(void)
{
	printf("Usage: screen-getty <tty> [baud] -c <cmd>\n");
	exit(1);
}


void
termio_init(tp, speed, op)
     struct termios *tp;
     int     speed;
     struct options *op;
{

	int cspeed;
    /*
     * Initial termios settings: 8-bit characters, raw-mode, blocking i/o.
     * Special characters are set after we have read the login name; all
     * reads will be done in raw mode anyway. Errors will be dealt with
     * lateron.
     */
    /* flush input and output queues, important for modems! */
    (void) tcflush(0, TCIOFLUSH);

	cspeed = cfgetispeed(tp);
    tp->c_cflag = CS8 | HUPCL | CREAD;

	if (speed) {
		cfsetispeed(tp, speed);
		cfsetospeed(tp, speed);
	} else {
		cfsetispeed(tp, cspeed);
		cfsetospeed(tp, cspeed);
	}
    if (op->flags & F_LOCAL) {
		tp->c_cflag |= CLOCAL;
    }

    tp->c_iflag = tp->c_lflag = tp->c_oflag = 0;
#ifdef HAVE_STRUCT_TERMIOS_C_LINE
    tp->c_line = 0;
#endif
    tp->c_cc[VMIN] = 1;
    tp->c_cc[VTIME] = 0;

    /* Optionally enable hardware flow control */

#ifdef	CRTSCTS
    if (op->flags & F_RTSCTS)
	tp->c_cflag |= CRTSCTS;
#endif

	/* stty sane equivlent */
    tp->c_iflag |= ICRNL | BRKINT | IMAXBEL;
    tp->c_oflag |= ONLCR | OPOST;
	tp->c_lflag |= ECHO | ECHOE | ECHOK | ICANON | ISIG | ECHOKE | ECHOCTL | IEXTEN;

    (void) tcsetattr(0, TCSANOW, tp);

    /* go to blocking input even in local mode */
    fcntl(0, F_SETFL, fcntl(0, F_GETFL, 0) & ~O_NONBLOCK);

}

void parse_speeds(struct options *op, char *arg)
{
    char   *cp;

    for (cp = strtok(arg, ","); cp != 0; cp = strtok((char *) 0, ",")) {
	if ((op->speeds[op->numspeed++] = bcode(cp)) <= 0)
	    error(_("bad speed: %s"), cp);
	if (op->numspeed >= MAX_SPEED)
	    error(_("too many alternate speeds"));
    }
}


int main(int argc, char *argv[])
{
	char buf[PATH_MAX+1];
	struct group *gr = NULL;
	struct stat st;
	int fd, len;
	pid_t tid;
	gid_t gid = 0;
	pid_t pid = getpid();
	int speed = 0;
	int i;
	struct termios t;
	struct termios *tp = &t;
	static struct options options;
	char *tty = NULL;
	char *baud = NULL;
	char **cmd = NULL;

	if (argc <= 1)
		usage();

	/* Arg parse */
	for (i = 1; i < argc; i++) {
		if (!strcmp(argv[i], "-c") && (i + 1 < argc)) {
			cmd = &argv[i+1];
			break;
		} else if (!tty) {
			tty = argv[i];
		} else if (!baud) {
			baud = argv[i];
		} else {
			usage();
		}
	}

	if (!(cmd && tty))
		usage();

	if (baud)
		if ((speed = bcode(baud)) <= 0)
			error(_("bad speed: %s"), baud);

	setsid();
	old_stderr = dup(fileno(stderr));

    /* Get rid of the present standard { output, error} if any. */

    (void) close(1);
    (void) close(2);
    errno = 0;					/* ignore above errors */

	if (chdir("/dev"))
	    error(_("/dev: chdir() failed: %m"));
	if (stat(tty, &st) < 0)
	    error("/dev/%s: %m", tty);
	if ((st.st_mode & S_IFMT) != S_IFCHR)
	    error(_("/dev/%s: not a character device"), tty);

	/* Open the tty as standard input. */

	(void) close(0);
	errno = 0;				/* ignore close(2) errors */

	if (open(tty, O_RDWR|O_NONBLOCK, 0) != 0)
	    error(_("/dev/%s: cannot open as standard input: %m"), tty);

    /* Set up standard output and standard error file descriptors. */
    if (dup(0) != 1 || dup(0) != 2)		/* set up stdout and stderr */
		error(_("%s: dup problem: %m"), tty);	/* we have a problem */

    /*
     * The following ioctl will fail if stdin is not a tty, but also when
     * there is noise on the modem control lines. In the latter case, the
     * common course of action is (1) fix your cables (2) give the modem more
     * time to properly reset after hanging up. SunOS users can achieve (2)
     * by patching the SunOS kernel variable "zsadtrlow" to a larger value;
     * 5 seconds seems to be a good value.
     */

    if (tcgetattr(0, tp) < 0)
		error("%s: tcgetattr: %m", tty);

    (void) chown(tty, 0, 0);			/* root, sys */
    (void) chmod(tty, 0600);			/* 0622: crw--w--w- */
    errno = 0;					/* ignore above errors */
    tcsetpgrp(0, getpid());
	options.flags |= F_LOCAL;
	termio_init(tp, speed, &options);

	close(old_stderr);
	execv(cmd[0], cmd);

	exit(0);

	/* Use tty group if available */
	if ((gr = getgrnam("tty")))
		gid = gr->gr_gid;

	if (((len = snprintf(buf, sizeof(buf), "/dev/%s", tty)) >=
		 (int)sizeof(buf)) || (len < 0))
	    log_err(_("/dev/%s: cannot open as standard input: %m"), tty);

	/*                                                                                       
	 * There is always a race between this reset and the call to                             
	 * vhangup() that s.o. can use to get access to your tty.                                
	 * Linux login(1) will change tty permissions. Use root owner and group                  
	 * with permission -rw------- for the period between getty and login.                    
	 */
	if (chown(buf, 0, gid) || chmod(buf, (gid ? 0620 : 0600))) {
		if (errno == EROFS)
			log_warn("%s: %m", buf);
		else
			log_err("%s: %m", buf);
	}

	/* Open the tty as standard input. */
	if ((fd = open(buf, O_RDWR|O_NOCTTY|O_NONBLOCK, 0)) < 0)
		log_err(_("/dev/%s: cannot open as standard input: %m"), tty);

	/* Sanity checks... */
	if (fstat(fd, &st) < 0)
		log_err("%s: %m", buf);
	if ((st.st_mode & S_IFMT) != S_IFCHR)
		log_err(_("/dev/%s: not a character device"), tty);
	if (!isatty(fd))
		log_err(_("/dev/%s: not a tty"), tty);

	if (((tid = tcgetsid(fd)) < 0) || (pid != tid)) {
		if (ioctl(fd, TIOCSCTTY, 1) == -1)
			log_warn(_("/dev/%s: cannot get controlling tty: %m"), tty);
	}
	close(STDIN_FILENO);
	errno = 0;

	close(fd);

	if (open(buf, O_RDWR|O_NOCTTY|O_NONBLOCK, 0) != 0)
		log_err(_("/dev/%s: cannot open as standard input: %m"), tty);

	if (((tid = tcgetsid(STDIN_FILENO)) < 0) || (pid != tid)) {
		if (ioctl(STDIN_FILENO, TIOCSCTTY, 1) == -1)
			log_warn(_("/dev/%s: cannot get controlling tty: %m"), tty);
	}

    if (tcsetpgrp(STDIN_FILENO, pid))
        log_warn(_("/dev/%s: cannot set process group: %m"), tty);

    /* Get rid of the present outputs. */
	close(STDOUT_FILENO);
	close(STDERR_FILENO);
	errno = 0;

    /* Set up standard output and standard error file descriptors. */
    /* set up stdout and stderr */
    if (dup(STDIN_FILENO) != 1 || dup(STDIN_FILENO) != 2)
        log_err(_("%s: dup problem: %m"), tty);

    memset(tp, 0, sizeof(struct termios));
    if (tcgetattr(STDIN_FILENO, tp) < 0)
        log_err(_("%s: failed to get terminal attributes: %m"), tty);

}
