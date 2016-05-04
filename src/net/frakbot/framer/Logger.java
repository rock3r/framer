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

    void error(String s) {
        System.err.println(s);
    }

    void error(Throwable e) {
        e.printStackTrace();
    }
}
