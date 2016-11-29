package be.thomaswinters.googlengrams;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class NgramAlphabeticalYearIntervalLoader extends NgramLoader {

	private final static String acceptedWords = "^[a-zA-Z\\-]+$";
	private final static Pattern wordPattern = Pattern.compile(acceptedWords);
	private final static int YEAR = 2008;
	private final static int MINIMAL_OCCURRENCES = 40;

	public NgramAlphabeticalYearIntervalLoader(NgramCsvReader reader, NgramMySQLConnector connector) {
		super(reader, connector);
	}

	public boolean isAlphabetical(String word) {
		return wordPattern.matcher(word).matches();
	}

	public boolean isAcceptedYear(int year) {
		return year == YEAR;
	}

	public boolean hasEnoughCounts(int count) {
		return count >= MINIMAL_OCCURRENCES;
	}

	@Override
	public boolean shouldStore(List<String> words, int year, int count) {
		return words.stream().allMatch(e -> isAlphabetical(e) && e.length() > 0) && isAcceptedYear(year)
				&& hasEnoughCounts(count);
	}


	public static void main(String[] args)
			throws NumberFormatException, ClassNotFoundException, URISyntaxException, SQLException {
		for (int i = 0; i < 100; i++) {
			NgramLoader loader = new NgramLoader(new NgramCsvReader(System.getenv("ngram_csv_file_prefix") + i + ".csv"),
					new NgramMySQLConnector(2, System.getenv("ngram_db_host"),
							Integer.parseInt(System.getenv("ngram_db_port")), System.getenv("ngram_db_username"),
							System.getenv("ngram_db_password"), System.getenv("ngram_db_databaseName")));
			loader.execute();
			System.out.println("Done " + i);
		}
	}
}
