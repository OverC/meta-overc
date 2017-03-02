/*
 * wdt-tool - Simple watchdog tool
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <fcntl.h>
#include <getopt.h>
#include <string.h>
#include <errno.h>
 
#include <linux/watchdog.h>
 
#define WATCHDOGDEV "/dev/watchdog"
static const char *const short_options = "hd:i:sSf";
static const struct option long_options[] = {
   {"help", 0, NULL, 'h'},
   {"dev", 1, NULL, 'd'},
   {"interval", 1, NULL, 'i'},
   {"status", 0, NULL, 's'},
   {"feed", 0, NULL, 'f'},
   {"shutdown", 0, NULL, 'S'},
   {NULL, 0, NULL, 0},
};
 
static void print_usage(FILE * stream, char *app_name, int exit_code)
{
   fprintf(stream, "Usage: %s [options]\n", app_name);
   fprintf(stream,
      " -h  --help                Display this usage information.\n"
      " -d  --dev <device_file>   Use <device_file> as watchdog device file.\n"
      "                           The default device file is '/dev/watchdog'\n"
      " -f  --feed                Feed the watchdog\n"
      " -i  --interval <interval> Change the watchdog interval time\n"
      " -s  --status              Check the watchdog status\n"
      " -S  --shutdown            Attempt to shutdown watchdog if nowayout is 0\n");
 
   exit(exit_code);
}
 
int main(int argc, char **argv)
{
   int fd;         /* File handler for watchdog */
   int interval;      /* Watchdog timeout interval (in secs) */
   int bootstatus;      /* Wathdog last boot status */
   char *dev;      /* Watchdog default device file */
   struct watchdog_info ident;
   
   int next_option;   /* getopt iteration var */
   char kick_watchdog;   /* kick_watchdog options */
   int status;   /* check status and exit */
   int shutdown;
   int feed;
   int read_interval;
 
   /* Init variables */
   dev = WATCHDOGDEV;
   interval = 0;
   kick_watchdog = 0;
   status = 0;
   shutdown = 0;
   feed = 0;
 
   next_option = getopt_long(argc, argv, short_options,
			     long_options, NULL);
   if (next_option == -1)
	   print_usage(stderr, argv[0], EXIT_FAILURE);

   /* Parse options if any */
   do {
	   switch (next_option) {
	   case 'h':
		   print_usage(stdout, argv[0], EXIT_SUCCESS);
	   case 'd':
		   dev = optarg;
		   break;
	   case 'i':
		   interval = atoi(optarg);
		   break;
	   case 'f':
		   feed = 1;
		   break;
	   case 's':
		   status = 1;
		   break;
	   case 'S':
		   shutdown = 1;
		   break;
	   case '?':   /* Invalid options */
		   print_usage(stderr, argv[0], EXIT_FAILURE);
	   default:
		   abort();
	   }
 	   next_option = getopt_long(argc, argv, short_options,
				     long_options, NULL);
   } while (next_option != -1);

   /* Once the watchdog device file is open, the watchdog will be
      activated by the driver */
   fd = open(dev, O_RDWR);
   if (-1 == fd) {
	   fprintf(stderr, "Error: %s\n", strerror(errno));
	   exit(EXIT_FAILURE);
   }

   if (status) {

	   /* Display watchdog identity */
	   if (ioctl(fd, WDIOC_GETSUPPORT, &ident) == 0) {
		   ident.identity[sizeof(ident.identity) - 1] = '\0';
		   printf("Watchdog identity: %s\n", ident.identity);
	   }
 
 
	   /* Display current watchdog interval */
	   if (ioctl(fd, WDIOC_GETTIMEOUT, &read_interval) == 0) {
		   printf("Current watchdog interval is %d\n", read_interval);
	   } else {
		   fprintf(stderr, "Error: Cannot read watchdog interval\n");
		   exit(EXIT_FAILURE);
	   }
 
	   /* Check if last boot is caused by watchdog */
	   if (ioctl(fd, WDIOC_GETBOOTSTATUS, &bootstatus) == 0) {
		   printf("Boot status code: %d\n", bootstatus);
		   printf("Last boot is caused by : %s\n",
			  (bootstatus != 0) ? "Watchdog" : "Power-On-Reset");
	   } else {
		   fprintf(stderr, "Error: Cannot read watchdog status\n");
		   exit(EXIT_FAILURE);
	   }
   }
   /* If user wants to change the watchdog interval */
   if (interval != 0) {
	   if (status)
		   printf("Set watchdog interval to %d\n", interval);
	   if (ioctl(fd, WDIOC_SETTIMEOUT, &interval) != 0) {
		   fprintf(stderr,
			   "Error: Set watchdog interval failed\n");
		   exit(EXIT_FAILURE);
	   }
   }
   
   if (shutdown) {
	   write(fd, "V", 1);
	   close(fd);
	   exit(0);
   }

   if (feed) {
	   ioctl(fd, WDIOC_KEEPALIVE, NULL);
	   close(fd);
	   exit(0);
   }

   if (status) {
	   close(fd);
	   exit(0);
   }

   /* There are two ways to kick the watchdog:
      - by writing any dummy value into watchdog device file, or
      - by using IOCTL WDIOC_KEEPALIVE
    */
   printf("Use:\n"
	  " <w> to kick through writing over device file\n"
	  " <i> to kick through IOCTL\n" " <x> to exit the program\n");
   do {
	   kick_watchdog = getchar();
	   switch (kick_watchdog) {
	   case 'w':
		   write(fd, "w", 1);
		   printf("Kick watchdog through writing over device file\n");
		   break;
	   case 'i':
		   ioctl(fd, WDIOC_KEEPALIVE, NULL);
		   printf("Kick watchdog through IOCTL\n");
		   break;
	   case 'x':
		   printf("Goodbye !\n");
		   break;
	   default:
		   printf("Unknown command\n");
		   break;
	   }
   } while (kick_watchdog != 'x');
 
   /* The 'V' value needs to be written into watchdog device file to indicate
      that we intend to close/stop the watchdog. Otherwise, debug message
      'Watchdog timer closed unexpectedly' will be printed
    */
   write(fd, "V", 1);
   /* Closing the watchdog device will deactivate the watchdog. */
   close(fd);
   return 0;
}
