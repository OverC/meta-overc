class Logger(object):
    DEBUG, INFO, WARNING, ERROR, CRITICAL = list(range(0,5))
    BASECOLOR, BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE = list(range(29,38))
    LEVELNAMES = {
        DEBUG   : "DEBUG",
        INFO    : "INFO",
        WARNING : "WARNING",
        ERROR   : "ERROR",
        CRITICAL: "CRITICAL",
    }

    COLORS = {
        DEBUG   : CYAN,
        INFO    : BASECOLOR,
        WARNING : YELLOW,
        ERROR   : RED,
        CRITICAL: RED,
    }

    BLD = '\033[1;%dm'
    STD = '\033[%dm'
    RST = '\033[0m'

    colored = True

    def disable_color(self):
        self.colored = False

    def colorize(self, levelno, msg):
        color = self.COLORS[levelno]
        levelname = self.LEVELNAMES[levelno]
        if self.colored and color is not None and levelname is not None and msg:
            level = "".join([self.BLD % color, "[%s]" % levelname, self.RST])
            msg = "".join([self.STD % color, msg, self.RST])
            return "%s: %s" % (level, msg)
        return msg

    def debug(self, msg):
        print(self.colorize(self.DEBUG, msg))

    def info(self, msg):
        print(self.colorize(self.INFO, msg))

    def warn(self, msg):
        print(self.colorize(self.WARNING, msg))

    def error(self, msg):
        print(self.colorize(self.ERROR, msg))

    def critical(self, msg):
        print(self.colorize(self.CRITICAL, msg))

logger = Logger()
