import java.lang.Comparable;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;
import java.util.HashMap;

public class GuardShifts {
	private static class GuardEntry implements Comparable<GuardEntry>{
		private static final int EVENT_BEGINSHIFT 	= 1;
		private static final int EVENT_FALLASLEEP 	= 2;
		private static final int EVENT_WAKESUP 		= 3;
		private int year;
		private int month;
		private int day;

		private int hour;
		private int minute;

		private int guardId;
		private int eventType;

		public GuardEntry(int year, int month, int day, int hour, int minute, int eventType) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
			this.eventType = eventType;
			this.guardId = -1;
		}
		public GuardEntry(int year, int month, int day, int hour, int minute, int eventType, int guardId) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
			this.eventType = eventType;
			this.guardId = guardId;
		}

		public int getType() { return this.eventType; }
		public int getGuardId() { return this.guardId; }
		public int getMinute() { return this.minute; }
		public int getHour() { return this.hour; }
		public int getDay() { return this.day; }
		public int getMonth() { return this.month; }
		public int getYear() { return this.year; }

		public int compareTo(GuardEntry o) {
			if(o.year > this.year) { return -1; } else if(o.year < this.year) { return 1; }
			if(o.month > this.month) { return -1; } else if(o.month < this.month) { return 1; }
			if(o.day > this.day) { return -1; } else if(o.day < this.day) { return 1; }
			if(o.hour > this.hour) { return -1; } else if(o.hour < this.hour) { return 1; }
			if(o.minute > this.minute) { return -1; } else if(o.minute < this.minute) { return 1; }

			if(o.eventType > this.eventType) { return 1; } else if(o.eventType < this.eventType) { return -1; }
			if(o.guardId > this.guardId) { return -1; } else if(o.guardId < this.guardId) { return 1; }

			return 0;
		}

		public String toString() {
			String action;

			if(this.eventType == EVENT_WAKESUP) {
				action = "wakes up";
			} else if(this.eventType == EVENT_BEGINSHIFT) {
				action = "Guard #"+this.guardId+" begins shift";
			} else if(this.eventType == EVENT_FALLASLEEP) {
				action = "falls asleep";
			} else {
				action = "UNDEFINED";
			}
			return "["+this.year+"-"+this.month+"-"+this.day+" "+this.hour+":"+this.minute+"] "+action;
		}
	}

	private static class Guard {
		private int[] asleepMinutes;
		private int sumAsleep;
		private int guardId;

		public Guard(int guardId) {
			this.guardId = guardId;
			this.asleepMinutes = new int[60];
			for(int i = 0; i < 60; i++) {
				this.asleepMinutes[i] = 0;
			}
			this.sumAsleep = 0;
		}

		public int getId() { return this.guardId; }

		public void addSleepTime(int startMinute, int endMinute) {
			for(int i = startMinute; i < endMinute; i++) {
				this.asleepMinutes[i]++;
			}
			sumAsleep = sumAsleep + (endMinute - startMinute);
		}

		public int getSumAsleep() { return this.sumAsleep; }

		public int getMaxSleepMinute() {
			int max = 0;
			int minMax = 0;
			for(int i = 0; i < asleepMinutes.length; i++) {
				if(asleepMinutes[i] > max) {
					max = asleepMinutes[i];
					minMax = i;
				}
			}
			return minMax;
		}

		public int[] getSleepStatistics() {
			return this.asleepMinutes;
		}
	}

	private static List<GuardEntry> loadFile(String filename) {
		FileInputStream fis = null;
		Scanner sc = null;

		ArrayList<GuardEntry> res = new ArrayList<GuardEntry>();

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			sc = new Scanner(fis);

			sc.useDelimiter("\\[|-| |:|\\]|#|\\n");

			while(sc.hasNext()) {
				int y = sc.nextInt();
				int m = sc.nextInt();
				int d = sc.nextInt();
				int h = sc.nextInt();
				int mi = sc.nextInt();

				String p1 = sc.next();

				GuardEntry newEntry;

				p1 = sc.next(); /* This symbol is enough to determine which action will be taken */
				if(p1.equals("wakes")) {
					sc.next();
					if(sc.hasNext()) {
						sc.next();
					}

					newEntry = new GuardEntry(y,m,d,h,mi,GuardEntry.EVENT_WAKESUP);
				} else if(p1.equals("Guard")) {
					sc.next();
					int gid = sc.nextInt();
					sc.next();
					sc.next();
					if(sc.hasNext()) {
						sc.next();
					}

					newEntry = new GuardEntry(y,m,d,h,mi,GuardEntry.EVENT_BEGINSHIFT, gid);
				} else if(p1.equals("falls")) {
					sc.next();
					if(sc.hasNext()) {
						sc.next();
					}

					newEntry = new GuardEntry(y,m,d,h,mi,GuardEntry.EVENT_FALLASLEEP);
				} else {
					return null;
				}

				res.add(newEntry);
			}
		} catch(IOException e) {
			e.printStackTrace();
			res = null;
		} finally {
			if(sc != null) { sc.close(); }
			else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
		}

		return res;
	}

	public static final void main(String args[]) {
		if(args.length < 1) {
			System.err.println("Please specify input filename");
			return;
		}

		List<GuardEntry> guardEntries = loadFile(args[0]);
		Collections.sort(guardEntries);

		HashMap<Integer, Guard> hmGuard = new HashMap<Integer, Guard>();
		Guard currentGuard = null;
		int lastAsleepStart = -1;

		for(GuardEntry e : guardEntries) {
			if(e.getType() == GuardEntry.EVENT_BEGINSHIFT) {
				if(hmGuard.containsKey(e.getGuardId())) {
					currentGuard = hmGuard.get(e.getGuardId());
				} else {
					currentGuard = new Guard(e.getGuardId());
					hmGuard.put(e.getGuardId(), currentGuard);
				}
			} else if(e.getType() == GuardEntry.EVENT_FALLASLEEP) {
				lastAsleepStart = e.getMinute();
			} else if(e.getType() == GuardEntry.EVENT_WAKESUP) {
				currentGuard.addSleepTime(lastAsleepStart, e.getMinute());
			}
		}

		int maxAsleep = 0;
		for (HashMap.Entry<Integer, Guard> e : hmGuard.entrySet()) {
			if(e.getValue().getSumAsleep() > maxAsleep) {
				maxAsleep = e.getValue().getSumAsleep();
			}
		}

		for (HashMap.Entry<Integer, Guard> e : hmGuard.entrySet()) {
			if(e.getValue().getSumAsleep() == maxAsleep) {
				System.out.println("Guard "+e.getValue().getId()+" sleeps "+e.getValue().getSumAsleep()+" minutes (sum)");
				System.out.println("\tThe minute he sleeps most is "+e.getValue().getMaxSleepMinute());
			}
		}

		int maxSleeped[] = new int[60];
		Guard maxSleepedGuard[] = new Guard[60];
		int maxSleepGlobal = 0;
		int maxSleepGlobalMinute = 0;
		Guard maxSleepGlobalGuard = null;
		for(int i = 0; i < maxSleeped.length; i++) { maxSleeped[i] = 0; maxSleepedGuard[i] = null; }

		for (HashMap.Entry<Integer, Guard> e : hmGuard.entrySet()) {
			Guard g = e.getValue();
			int gSleep[] = g.getSleepStatistics();
			for(int i = 0; i < gSleep.length; i++) {
				if(maxSleeped[i] < gSleep[i]) {
					maxSleeped[i] = gSleep[i];
					maxSleepedGuard[i] = g;
				}
				if(gSleep[i] > maxSleepGlobal) {
					maxSleepGlobal = gSleep[i];
					maxSleepGlobalMinute = i;
					maxSleepGlobalGuard = g;
				}
			}
		}

		System.out.println("Guard "+maxSleepGlobalGuard.getId()+" slept times "+maxSleepGlobal+" at minute "+maxSleepGlobalMinute);


		return;
	}
}
