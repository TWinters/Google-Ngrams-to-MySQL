package be.thomaswinters.googlengrams.arguments;

import be.thomaswinters.pos.WordTypeCalculator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class ConstraintStringConverter implements IStringConverter<Function<List<String>, Boolean>> {

    private static final Dictionary dictionary = new Dictionary(ClassLoader.getSystemResource("dict/"));

    /*-********************************************-*
     *  WordTypes
     *-********************************************-*/
    private static final MaxentTagger tagger = new MaxentTagger("stanford-pos/english-bidirectional-distsim.tagger");
    private static final WordTypeCalculator wordTypeCalculator = new WordTypeCalculator(dictionary, tagger);

    static {
        try {
            dictionary.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWordOfType(POS partOfSpeech, String word) {
        return wordTypeCalculator.getWordTypes(word).contains(partOfSpeech);
    }

    public static boolean isAdjectiveNounCombination(List<String> words) {
        String word1 = words.get(0);
        String word2 = words.get(1);

        return isWordOfType(POS.ADJECTIVE, word1) && isWordOfType(POS.NOUN, word2);
    }

    @Override
    public Function<List<String>, Boolean> convert(String value) {

        switch (value.toLowerCase()) {
            case "all":
                return e -> true;
            case "adjectivenoun":
                return ConstraintStringConverter::isAdjectiveNounCombination;
            default:
                throw new ParameterException("Unknown constraint type '" + value + "'.");
        }
    }

    /*-********************************************-*/

}
