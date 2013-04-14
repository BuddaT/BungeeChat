package net.buddat.bungeechat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;

import net.md_5.bungee.api.plugin.Plugin;

public class BasicLogger {
	java.util.logging.Logger logger;
	Plugin plugin;
	
	private String stackTraceAsString(Throwable t) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		t.printStackTrace(new PrintWriter(bytes));
		return bytes.toString();
	}

	public BasicLogger(Plugin plugin) {
		logger = plugin.getProxy().getLogger();
		this.plugin = plugin;
	}

	public String getName() {
		return plugin.getDescription().getName();
	}

	public boolean isTraceEnabled() {
		return logger.isLoggable(Level.FINEST);
	}

	public void trace(String msg) {
		logger.finest(msg);
	}

	public void trace(String format, Object... args) {
		logger.finest(String.format(format, args));
	}

	public void trace(String msg, Throwable t) {
		logger.finest(msg + stackTraceAsString(t));
	}

	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.FINE);
	}

	public void debug(String msg) {
		logger.fine(msg);
	}

	public void debug(String format, Object... args) {
		logger.fine(String.format(format, args));
	}

	public void debug(String msg, Throwable t) {
		logger.fine(msg + stackTraceAsString(t));
	}

	public boolean isInfoEnabled() {
		return logger.isLoggable(Level.INFO);
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void info(String format, Object... args) {
		logger.info(String.format(format, args));
	}

	public void info(String msg, Throwable t) {
		logger.info(msg + stackTraceAsString(t));
	}

	public boolean isWarnEnabled() {
		return logger.isLoggable(Level.WARNING);
	}

	public void warn(String msg) {
		logger.warning(msg);
	}

	public void warn(String format, Object... args) {
		logger.warning(String.format(format, args));
	}

	public boolean isErrorEnabled() {
		return logger.isLoggable(Level.SEVERE);
	}

	public void error(String msg) {
		logger.severe(msg);
	}

	public void error(String format, Object... args) {
		logger.severe(String.format(format, args));
	}

	public void error(String msg, Throwable t) {
		logger.severe(msg + stackTraceAsString(t));
	}
}
