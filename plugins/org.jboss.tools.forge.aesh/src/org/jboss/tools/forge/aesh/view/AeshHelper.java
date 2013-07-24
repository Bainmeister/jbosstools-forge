package org.jboss.tools.forge.aesh.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.aesh.complete.CompleteOperation;
import org.jboss.aesh.complete.Completion;
import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.Console;
import org.jboss.aesh.console.ConsoleCallback;
import org.jboss.aesh.console.ConsoleCommand;
import org.jboss.aesh.console.ConsoleOutput;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.helper.InterruptHook;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.CharacterType;
import org.jboss.aesh.terminal.Color;
import org.jboss.aesh.terminal.TerminalCharacter;
import org.jboss.aesh.terminal.TerminalString;
import org.jboss.aesh.util.ANSI;

public class AeshHelper {

	private static Prompt createPrompt() {
        List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
        chars.add(new TerminalCharacter('['));
        chars.add(new TerminalCharacter('t'));
        chars.add(new TerminalCharacter('e'));
        chars.add(new TerminalCharacter('s'));
        chars.add(new TerminalCharacter('t'));
        chars.add(new TerminalCharacter(']'));
        chars.add(new TerminalCharacter('$'));
        chars.add(new TerminalCharacter(' '));
        return new Prompt(chars);
	}
	
	// private void initAesh() {
	// try {
	// Settings.getInstance().setInputStream(AeshInputStream.INSTANCE);
	// Settings.getInstance().setStdOut(AeshOutputStream.STD_OUT);
	// Settings.getInstance().setStdErr(AeshOutputStream.STD_ERR);
	// final Console console = Console.getInstance();
	// console.setConsoleCallback(new ConsoleCallback() {
	// @Override
	// public int readConsoleOutput(ConsoleOutput output) throws IOException {
	// console.pushToStdOut("======>\"" + output.getBuffer() + "\"\n");
	// return 0;
	// }
	// });
	// console.start();
	// console.setPrompt(createPrompt());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	private static boolean masking = false;
	private static String password;
	private static String username;
	static boolean hasPassword;
	static ConsoleCallback passwordCallback;

	public static void initAesh() {
		try {
		// Settings.getInstance().setAnsiConsole(false);
		Settings.getInstance().setReadInputrc(false);
		Settings.getInstance().setLogging(true);
		Settings.getInstance().setLogFile("aesh_example.log");
		Settings.getInstance().setAliasEnabled(true);
		Settings.getInstance().setAliasFile(new File("aesh_aliases.txt"));
		Settings.getInstance().setInputStream(AeshInputStream.INSTANCE);
		Settings.getInstance().setStdOut(AeshOutputStream.STD_OUT);
		Settings.getInstance().setStdErr(AeshOutputStream.STD_ERR);
		// Settings.getInstance().enableOperatorParser(false);
		// Settings.getInstance().setHistoryDisabled(true);
		// Settings.getInstance().setHistoryPersistent(false);
		List<TerminalCharacter> chars = new ArrayList<TerminalCharacter>();
		chars.add(new TerminalCharacter('[', Color.DEFAULT_BG, Color.BLUE_TEXT));
		chars.add(new TerminalCharacter('t', Color.DEFAULT_BG, Color.RED_TEXT,
				CharacterType.ITALIC));
		chars.add(new TerminalCharacter('e', Color.DEFAULT_BG, Color.RED_TEXT,
				CharacterType.INVERT));
		chars.add(new TerminalCharacter('s', Color.DEFAULT_BG, Color.RED_TEXT,
				CharacterType.CROSSED_OUT));
		chars.add(new TerminalCharacter('t', Color.DEFAULT_BG, Color.RED_TEXT,
				CharacterType.BOLD));
		chars.add(new TerminalCharacter(']', Color.DEFAULT_BG, Color.BLUE_TEXT,
				CharacterType.PLAIN));
		chars.add(new TerminalCharacter('$', Color.DEFAULT_BG,
				Color.WHITE_TEXT, CharacterType.UNDERLINE));
		chars.add(new TerminalCharacter(' ', Color.DEFAULT_BG, Color.WHITE_TEXT));

		final Prompt prompt = new Prompt(chars);
		// String prompt = ANSI.redText()+"[test@foo]"+ANSI.reset()+"$ ";

		// a simple interruptHook
		Settings.getInstance().setInterruptHook(new InterruptHook() {
			@Override
			public void handleInterrupt(Console console) {
				try {
					console.pushToStdOut("KILLED!\n");
					console.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		final Console exampleConsole = Console.getInstance();

		final ConsoleCommand test = new ConsoleCommand(exampleConsole) {

			@Override
			protected void afterAttach() throws IOException {
				if (!hasRedirectOut()) {
					console.pushToStdOut(ANSI.getAlternateBufferScreen());
				}

				readFromFile();

				// detach after init if hasRedirectOut()
				if (hasRedirectOut()) {
					detach();
				}
			}

			@Override
			protected void afterDetach() throws IOException {
				if (!hasRedirectOut())
					console.pushToStdOut(ANSI.getMainBufferScreen());
			}

			private void readFromFile() throws IOException {
				if (getConsoleOutput().getStdOut() != null
						&& getConsoleOutput().getStdOut().length() > 0) {
					console.pushToStdOut("FROM STDOUT: "
							+ getConsoleOutput().getStdOut());
				} else
					console.pushToStdOut("here should we present some text... press 'q' to quit");
			}

			@Override
			public void processOperation(Operation operation)
					throws IOException {
				if (operation.getInput()[0] == 'q') {
					detach();
				} else if (operation.getInput()[0] == 'a') {
					readFromFile();
				} else {

				}
			}
		};

		Completion completer = new Completion() {
			@Override
			public void complete(CompleteOperation co) {
				// very simple completor
				List<String> commands = new ArrayList<String>();
				if (co.getBuffer().equals("fo") || co.getBuffer().equals("foo")) {
					commands.add("foo");
					commands.add("foobaa");
					commands.add("foobar");
					commands.add("foobaxxxxxx");
					commands.add("foobbx");
					commands.add("foobcx");
					commands.add("foobdx");
				}
				if (co.getBuffer().equals("p")) {
					commands.add("profile=foo");
					co.setOffset(0);
				}
				/*
				 * if(co.getBuffer().equals("p")) { commands.add("profile=bar");
				 * co.setOffset(0); }
				 */
				if (co.getBuffer().equals("profile="))
					commands.add("profile=foo");
				if (co.getBuffer().equals("profile="))
					commands.add("profile=bar");
				if (co.getBuffer().equals("--")) {
					commands.add("--help-");
				}
				if ("--help-me".startsWith(co.getBuffer())) {
					commands.add("--help-me");
				}
				if (co.getBuffer().equals("fooba")) {
					commands.add("foobaa");
					commands.add("foobar");
					commands.add("foobaxxxxxx");
				}
				if (co.getBuffer().equals("foobar")) {
					commands.add("foobar");
				}
				if (co.getBuffer().equals("bar")) {
					commands.add("bar/");
				}
				if (co.getBuffer().equals("h")) {
					commands.add("help.history");
					commands.add("help");
					co.setOffset(0);
				}
				if (co.getBuffer().equals("help")) {
					commands.add("help.history");
					commands.add("help");
				}
				if (co.getBuffer().equals("help.")) {
					commands.add("help.history");
				}
				if (co.getBuffer().equals("deploy")) {
					commands.add("deploy /home/blabla/foo/bar/alkdfe/en/to/tre");
				}
				if (co.getBuffer().equals("testing")) {
					commands.add("testing YAY");
				}
				co.setCompletionCandidates(commands);
			}
		};

		exampleConsole.addCompletion(completer);

		final ConsoleOutput line;
		final ConsoleCallback consoleCallback = new ConsoleCallback() {
			@Override
			public int readConsoleOutput(ConsoleOutput output)
					throws IOException {
				// To change body of implemented methods use File | Settings |
				// File Templates.

				exampleConsole.pushToStdOut("======>\"" + output.getBuffer()
						+ "\"\n");
				if (masking) {
					exampleConsole.pushToStdOut("got password: "
							+ output.getBuffer() + ", stopping masking");
					masking = false;
					exampleConsole.setPrompt(prompt);
				} else if (output.getBuffer().equalsIgnoreCase("quit")
						|| output.getBuffer().equalsIgnoreCase("exit")
						|| output.getBuffer().equalsIgnoreCase("reset")) {
					exampleConsole.stop();
				} else if (output.getBuffer().equalsIgnoreCase("password")) {
					masking = true;
					exampleConsole
							.setPrompt(new Prompt("password: ", (char) 0));
				} else if (output.getBuffer().startsWith("blah")) {
					exampleConsole.pushToStdErr("blah. command not found.\n");
					exampleConsole.pushToStdOut("BAH"
							+ Config.getLineSeparator());
				} else if (output.getBuffer().equals("clear"))
					exampleConsole.clear();
				else if (output.getBuffer().startsWith("man")) {
					// exampleConsole.attachProcess(test);
					test.attach(output);
				} else if (output.getBuffer().startsWith("login")) {
					exampleConsole.setConsoleCallback(passwordCallback);
					exampleConsole.setPrompt(new Prompt("Username: "));
				}
				return 0;
			}
		};
		exampleConsole.setConsoleCallback(consoleCallback);
		exampleConsole.start();
		exampleConsole.setPrompt(prompt);

		passwordCallback = new ConsoleCallback() {
			private boolean hasUsername = false;

			@Override
			public int readConsoleOutput(ConsoleOutput output)
					throws IOException {
				if (hasUsername) {
					password = output.getBuffer();
					hasPassword = true;
					exampleConsole.pushToStdOut("Username: " + username
							+ ", password: " + password
							+ Config.getLineSeparator());
					exampleConsole.setPrompt(prompt);
					exampleConsole.setConsoleCallback(consoleCallback);
				} else {
					username = output.getBuffer();
					exampleConsole
							.setPrompt(new Prompt("Password: ", (char) 0));
					hasUsername = true;
				}
				return 0;
			}
		};

		// show how we can change the prompt async
		try {
			Thread.sleep(4000);
			exampleConsole.setPrompt(new Prompt(new TerminalString("[FOO] ",
					Color.DEFAULT_BG, Color.RED_TEXT, CharacterType.BOLD)));
			// exampleConsole.pushToStdOut(new TerminalString("PUSHING",
			// Color.DEFAULT_BG, Color.BLUE_TEXT).getAsString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
