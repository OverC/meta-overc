
OBJS = siphash24.o main.o

DEST=/usr/bin

all: hashmac

clean:
	rm -f $(OBJS) hashmac

%.o: %.c
	$(CC) $(CFLAGS) -Wall -c $<

hashmac: $(OBJS)
	$(CC) $(LDFLAGS) -Wall -o $@ $^

install:
	install -d $(DEST)
	install -m 0755 hashmac $(DEST)
