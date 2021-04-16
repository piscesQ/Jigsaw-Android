package com.kore.jiasaw.apt.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static com.kore.jigsaw.anno.utils.Constants.PREFIX_OF_LOGGER;

/**
 * @author koreq
 * @date 2021-04-13
 * @description
 */
public class Logger {
    private Messager msg;

    public Logger(Messager messager) {
        msg = messager;
    }

    /**
     * Print info log.
     */
    public void info(CharSequence info) {
        if (isNotEmpty(info)) {
            msg.printMessage(Diagnostic.Kind.NOTE, PREFIX_OF_LOGGER + info);
        }
    }

    public void error(CharSequence error) {
        if (isNotEmpty(error)) {
            msg.printMessage(Diagnostic.Kind.ERROR,
                    PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]");
        }
    }

    public void error(Throwable error) {
        if (null != error) {
            String info = PREFIX_OF_LOGGER + "An exception is encountered, [" + error.getMessage() + "]"
                    + "\n" + formatStackTrace(error.getStackTrace());
            msg.printMessage(Diagnostic.Kind.ERROR, info);
        }
    }

    public void warning(CharSequence warning) {
        if (isNotEmpty(warning)) {
            msg.printMessage(Diagnostic.Kind.WARNING, PREFIX_OF_LOGGER + warning);
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private static boolean isNotEmpty(final CharSequence cs) {
        boolean isEmpty = cs == null || cs.length() == 0;
        return !isEmpty;
    }
}
