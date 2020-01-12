package auton.commands;

public abstract class Command {
	
	private String name;

	public Command(String name) {
		this.name = name;
	}

	public abstract void init();

	public abstract void run();

	public abstract boolean isDone();


}