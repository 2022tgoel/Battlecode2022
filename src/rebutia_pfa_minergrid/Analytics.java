package rebutia_pfa_minergrid;

public class Analytics {
	public static final boolean LOGGING_ENABLED = true;

	public static void log(String tag, String data) {
		if (LOGGING_ENABLED) {
			System.out.println("$" + tag + ":" + data);
		}
	}
}
