package auton;

import java.util.ArrayList;

import auton.commands.Command;

public class AutonScheduler {

	public AutonScheduler() {
	}

	public void runRoutine(ArrayList<Command> commandsList) {
		for (int i = 0; i < commandsList.size(); i++) {
			commandsList.get(i).init();

			while (!commandsList.get(i).isDone()) {
				commandsList.get(i).run();
			}
		}
	}
}