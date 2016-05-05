package net.frakbot.framer;

class Logger {

    private static Logger INSTANCE;

    static Logger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Logger();
        }
        return INSTANCE;
    }

    private Logger() {
    }

    void error(String message) {
        System.err.println(message);
    }

    void error(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void info(String message) {
        System.out.println(message);
    }
}
