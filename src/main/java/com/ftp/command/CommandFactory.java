package com.ftp.command;

import com.ftp.command.impl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFactory {
    private static final CommandFactory INSTANCE = new CommandFactory();

    private final Map<String, CommandHandler> commands;

    private CommandFactory() {
        this.commands = new ConcurrentHashMap<>();
        registerCommands();
    }

    public static CommandFactory getInstance() {
        return INSTANCE;
    }

    private void registerCommands() {
        commands.put("USER", new UserCommand());
        commands.put("PASS", new PassCommand());
        commands.put("QUIT", new QuitCommand());
        commands.put("NOOP", new NoopCommand());
        commands.put("SYST", new SystCommand());
        commands.put("FEAT", new FeatCommand());
        commands.put("PWD", new PwdCommand());
        commands.put("CWD", new CwdCommand());
        commands.put("CDUP", new CdupCommand());
        commands.put("MKD", new MkdCommand());
        commands.put("RMD", new RmdCommand());
        commands.put("LIST", new ListCommand());
        commands.put("NLST", new NlstCommand());
        commands.put("RETR", new RetrCommand());
        commands.put("STOR", new StorCommand());
        commands.put("DELE", new DeleCommand());
        commands.put("RNFR", new RnfrCommand());
        commands.put("RNTO", new RntoCommand());
        commands.put("SIZE", new SizeCommand());
        commands.put("MDTM", new MdtmCommand());
        commands.put("TYPE", new TypeCommand());
        commands.put("PORT", new PortCommand());
        commands.put("PASV", new PasvCommand());
        commands.put("REST", new RestCommand());
        commands.put("APPE", new AppeCommand());
        commands.put("ALLO", new AlloCommand());
        commands.put("STAT", new StatCommand());
        commands.put("OPTS", new OptsCommand());
        commands.put("SITE", new SiteCommand());
        commands.put("ACCT", new AcctCommand());
        commands.put("REIN", new ReinCommand());
        commands.put("SMNT", new SmntCommand());
        commands.put("MODE", new ModeCommand());
        commands.put("STRU", new StruCommand());
        commands.put("HELP", new HelpCommand());
        commands.put("MLSD", new MlsdCommand());
        commands.put("MLST", new MlstCommand());
        commands.put("EPSV", new EpsvCommand());
        commands.put("EPRT", new EprtCommand());
    }

    public void registerCommand(String name, CommandHandler handler) {
        commands.put(name.toUpperCase(), handler);
    }

    public CommandHandler getCommand(String name) {
        return commands.get(name.toUpperCase());
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name.toUpperCase());
    }
}
