import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.lang.Character;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class Dependency {
	private static class Task {
		private List<Task> dependencies;
		private char name;

		private boolean finished;

		private int timeElapsed;

		public Task(char name) {
			this.name = name;
			this.dependencies = new ArrayList<Task>();
			this.finished = false;
			this.timeElapsed = -1;
		}

		public void addDependency(Task dep) {
			this.dependencies.add(dep);
		}

		public Task setFinished(boolean fin) { this.finished = fin; return this; }
		public boolean isFinished() { return this.finished; }

		public String toString() {
			String res = "" + this.name;
			if(dependencies.size() > 0) {
				res = res + " (Blocked by";
				for(Task t : dependencies) {
					res = res + " " + t.getName();
				}
				res = res + ")";
			}
			return res;
		}

		public int getDependencyCount() { return this.dependencies.size(); }
		public List<Task> getDependencies() { return this.dependencies; }
		public boolean isBlocked() {
			for(Task t : dependencies) {
				if(!t.isFinished()) { return true; }
			}
			return false;
		}

		public String getName() { return ""+this.name; }

		public int getDuration() {
			return 60 + this.name - 'A' + 1;
		}
		public void workBegin() {
			this.timeElapsed = 0;
		}
		public boolean workAdvance() {
			if(this.finished) { return true; }
			this.timeElapsed++;
			if(this.timeElapsed == this.getDuration()) {
				this.finished = true;
				return true;
			} else {
				return false;
			}
		}
		public boolean workAvail() {
			return (this.timeElapsed == -1);
		}
	}

        public static HashMap<Character, Task> loadTasks(String filename) {
                FileInputStream fis = null;
                InputStreamReader isr = null;
                BufferedReader br = null;

		HashMap<Character, Task> hmTasks = new HashMap<Character, Task>();

                File f = new File(filename);

                try {
                        fis = new FileInputStream(f);
                        isr = new InputStreamReader(fis);
                        br = new BufferedReader(isr);

                        String line;
                        while((line = br.readLine()) != null) {
				if(line.length() < 40) {
					break;
				}

				char dependencyname = line.charAt(5);
				char taskname = line.charAt(36);

				Task dependency;
				if(hmTasks.containsKey(dependencyname)) {
					dependency = hmTasks.get(dependencyname);
				} else {
					dependency = new Task(dependencyname);
					hmTasks.put(dependencyname, dependency);
				}

				Task curTask;
				if(hmTasks.containsKey(taskname)) {
					curTask = hmTasks.get(taskname);
				} else {
					curTask = new Task(taskname);
					hmTasks.put(taskname, curTask);
				}

				curTask.addDependency(dependency);
                        }

                        br.close();
                } catch(IOException e) {
                        e.printStackTrace();
                        hmTasks = null;
                } finally {
                        if(br != null) { try { br.close(); } catch(IOException e) { e.printStackTrace(); } }
                        else if(isr != null) { try { isr.close(); } catch(IOException e) { e.printStackTrace(); } }
                        else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
                }

                return hmTasks;
        }

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("No input filename specified");
			return;
		}
		HashMap<Character, Task> hmTasks = loadTasks(args[0]);

		/* Determine task order by iterativly finish unfinished tasks (in alphabetical order) */
		String taskOrder = "";
		boolean unfinishedTasks = false;

		do {
			unfinishedTasks = false;

			// Iterate that way so we have an alphabetical order anyways
			for(char cCurrent = 'A'; cCurrent <= 'Z'; cCurrent++) {
				if(!hmTasks.containsKey(cCurrent)) { continue; }
				if(hmTasks.get(cCurrent).isFinished()) { continue; }

				if(hmTasks.get(cCurrent).isBlocked()) {
					unfinishedTasks = true;
					continue;
				}

				taskOrder = taskOrder + cCurrent;
				hmTasks.get(cCurrent).setFinished(true);
				unfinishedTasks = true;
				break;
			}
		} while(unfinishedTasks);
		System.out.println("Task order "+taskOrder);

		/*
			Part 2

			Work shedule ...
		*/
		for(Map.Entry<Character, Task> e : hmTasks.entrySet()) {
			e.getValue().setFinished(false);
		}

		Task worker[] = new Task[5];
		for(int i = 0; i < worker.length; i++) { worker[i] = null; }

		int currentSecond = 0; 
		do {
			unfinishedTasks = false;
			for(int i = 0; i < worker.length; i++) {
				/* If an worker is currently working, timestep */
				if(worker[i] != null) {
					if(worker[i].workAdvance()) {
						worker[i] = null;
					} else {
						unfinishedTasks = true;
						continue;
					}
				}

				/* Assign next work item to worker ... */
				for(char cCurrent = 'A'; cCurrent <= 'Z'; cCurrent++) {
					if(!hmTasks.containsKey(cCurrent)) { continue; }
					if(hmTasks.get(cCurrent).isFinished()) { continue; }

					if(hmTasks.get(cCurrent).isBlocked()) {
						unfinishedTasks = true;
						continue;
					}
					if(!hmTasks.get(cCurrent).workAvail()) { continue; }
					worker[i] = hmTasks.get(cCurrent);
					worker[i].workBegin();
					unfinishedTasks = true;
					break;
				}
			}

			System.out.print(currentSecond+"\t");
			for(int i = 0; i < worker.length; i++) {
				if(worker[i] == null) {
					System.out.print(".\t");
				} else {
					System.out.print(worker[i].getName()+"\t");
				}
			}
			System.out.println();

			currentSecond++;
		} while(unfinishedTasks);
	}
}
