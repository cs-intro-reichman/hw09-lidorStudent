import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
        String window = "";
        char c;
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
            if (in.isEmpty()) {
                break;
            }
            window += in.readChar();
        }
        while (!in.isEmpty()) {
            c  = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);
            window = window.substring(1) + c;
        }
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        if (probs.getSize() <= 0 || probs == null) {
            return;
        }
        if (probs.getFirst() == null) {
            return;
        }
        int totalCount = 0;
        ListIterator iterator = probs.listIterator(0);
        while (iterator.hasNext()) {
            totalCount += iterator.next().count;
        }
        if (totalCount == 0) return; 
        iterator = probs.listIterator(0);
        double cp = 0;
        while (iterator.hasNext()) {
            CharData charData = iterator.next();
            double probability = (double) charData.count / totalCount;
            charData.p = probability;
            cp += probability;
            charData.cp = cp;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Your code goes here
        double r = randomGenerator.nextDouble();
        CharData current = null;
        for (int i = 0; i < probs.getSize(); i++) {
            current = probs.get(i);
            if (r <= current.cp) {
                return current.chr;
            }
        }
        return '0';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
        if (initialText.length() < windowLength) {
            return initialText;
        }
        String generatedText = initialText;
        for (int i = 0; i < textLength; i++) {
            String lastSubstring = generatedText.substring(generatedText.length() - initialText.length());
            if (CharDataMap.containsKey(lastSubstring)) {
                char nextChar = getRandomChar(CharDataMap.get(lastSubstring));
                generatedText += nextChar;
            } else {
                break;
            }
        }
        return generatedText;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
