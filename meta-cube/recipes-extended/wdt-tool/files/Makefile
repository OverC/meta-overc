
OBJS = wdt-tool

DEST=/

all: $(OBJS)

clean:
	rm -f $(OBJS) *.o

%.o: %.c
	$(CC) $(CFLAGS) -Wall -c $<

wdt-tool: wdt-tool.o
	$(CC) $(LDFLAGS) -Wall -o $@ $^

install: $(OBJS)
	install -d $(DEST)/sbin
	install -m 0755 wdt-tool $(DEST)/sbin
