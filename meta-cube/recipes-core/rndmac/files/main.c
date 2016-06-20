/***
  Implent the MAC hashing function used by systemd.

  Copyright 2016 Wind River Systems, Inc. - Jason Wessel

  systemd is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or
  (at your option) any later version.

  systemd is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with hashmac; If not, see <http://www.gnu.org/licenses/>.
***/

#include "siphash24.h"
#include "sd-id128.h"
#include <stdio.h>
#include <stdlib.h>

void usage(void) {
	printf("Usage: hashmac <string to hash> <interface name> [offset to add]\n");
	
	exit(0);
}

int unhexchar(char c) {

        if (c >= '0' && c <= '9')
                return c - '0';

        if (c >= 'a' && c <= 'f')
                return c - 'a' + 10;

        if (c >= 'A' && c <= 'F')
                return c - 'A' + 10;

        return -1;
}

#define HASH_KEY SD_ID128_MAKE(52,e1,45,bd,00,6f,29,96,21,c6,30,6d,83,71,04,48)

int main(int argc, char *argv[]) {
	uint8_t result[8];
	int j;
	size_t l, sz;
	sd_id128_t t;
	uint8_t *v;

	if (argc < 3) {
		usage();
	}

	l = strlen(argv[2]);
	sz = sizeof(sd_id128_t) + l;
	v = alloca(sz);

	if (strlen(argv[1]) != 32) {
		printf("ERRROR: machine name string must be exactly 32 characters\n");
		return -1;
	}

	/* Process machine name string into bytes */
	for (j = 0; j < 16; j++) {
		int a, b;
		
		a = unhexchar(argv[1][j*2]);
		b = unhexchar(argv[1][j*2+1]);
		
		if (a < 0 || b < 0) {
			printf("ERROR: bad hex value in machine-id\n");
			return -1;
		}
		t.bytes[j] = a << 4 | b;
	}

	memcpy(v, &t, sizeof(t));
	memcpy(v + sizeof(sd_id128_t), argv[2], l);
	siphash24(result, v, sz, HASH_KEY.bytes);

	result[0] &= 0xfe;        /* clear multicast bit */
    result[0] |= 0x02;        /* set local assignment bit (IEEE802) */


	printf("%02x:%02x:%02x:%02x:%02x:%02x\n",
		   result[0], result[1], result[2], result[3],
		   result[4], result[5]);

    return 0;
}
