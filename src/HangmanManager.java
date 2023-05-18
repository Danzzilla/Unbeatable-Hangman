/**
 * Hangman Game Manager
 * @author Daniel Svirida
 * @version 1
 */

import java.util.*;

public class HangmanManager {

    /** Stores the desired length of number to be used */
    private int wordLength;
    /** Stores the desired amount of wrong guesses allowed */
    private int maxWrong;
    /** Library of words used for the game */
    private Set<String> consideredWords;
    /** Stores the letters that the player has guessed */
    private SortedSet<Character> guessedLetters;
    /** Stores the pattern displayed of blank / correctly guessed words */
    private String display = "";

    /**
     * Constructs HangmanManager
     *
     * @param dictionary - List containing dictionary of words
     * @param length - Target word length
     * @param max - Maximum number of wrong guesses the player is allowed to make
     *
     * @exception IllegalArgumentException If target word length is less than 1
     * @exception IllegalArgumentException If maximum number of wrong guesses is less than 0
     *
     * @see words()
     * @see guessesLeft()
     * @see guesses()
     * @see pattern()
     * @see record()
     */
    public HangmanManager(List<String> dictionary, int length, int max) {
        if(length < 1) throw new IllegalArgumentException("Target word length needs to be larger than 1");
        if(max < 0) throw new IllegalArgumentException("Maximum number of wrong guesses the player is allowed to make must be greater than 0");

        wordLength = length;
        maxWrong = max;

        guessedLetters = new TreeSet<Character>();

        consideredWords = new TreeSet<String>();
        for(String word : dictionary) {
            if(word.length() == wordLength) consideredWords.add(word);                                         //adds words the desired length into consideredWords
        }

        for(int i = 0; i < wordLength; i++) display += "- ";                                                   //creates the pattern "- - - - -" for however many places the word has
    }

    /**
     * Client calls this method to get the set of words the computer is considering using
     * @return consideredWords - the current set of words being considered by the HangmanManager
     */
    public Set<String> words(){
        return  consideredWords;
    }

    /**
     * Client calls this method to find how many guesses the player has left
     * @return maxWrong - number of guesses the player has left
     */
    public int guessesLeft() {
        return maxWrong;
    }

    /**
     * Client calls this method to find out the set of letters that have already been guessed
     * @return guessedLetters - alphabetically sorted set containing the characters the player has already guessed
     */
    public SortedSet<Character> guesses(){
        return guessedLetters;
    }

    /**
     * Used to return the pattern that is to be displayed to the player
     * @return display - the pattern of dashes and correctly guessed letters that the player uses to guess the next letter
     * @exception IllegalStateException If no words are considered by the HangmanManager
     */
    public String pattern() {
        if(consideredWords.isEmpty()) throw new IllegalStateException("No words are considered by the HangmanManager");

        return display;
    }

    /**
     * Goes through the considered words and puts in new set of words to a pattern key that follows the pattern.
     * Updates the considered words. Returns occurrences.
     * @see updateConsideredWords()
     * @see occurrences(char)
     *
     * @param guess - The character that the player guessed to be in the word
     * @return occurrences - How many times the guessed character occurs in the word
     *
     * @exception IllegalStateException If no words are considered by the HangmanManager
     * @exception IllegalStateException If no guesses remaining, cant guess
     * @exception IllegalArgumentException If the letter was guessed previously
     */
    public int record(char guess) {
        if(consideredWords.isEmpty()) throw new IllegalStateException("No words are considered by the HangmanManager");
        if(maxWrong < 1) throw new IllegalStateException("Sorry, no more guesses left");
        if(!consideredWords.isEmpty() && guessedLetters.contains(guess)) throw new IllegalArgumentException("That letter was already guessed");

        Map<String, Set<String>> patternFamilies = new TreeMap<String, Set<String>>();                         //Stores all the different pattern families in accordance to the considered words
        //Stores a set of words that fit a pattern with the pattern being the key
        for(String word : consideredWords) {                                                                   //goes through all the considered words and stores the considered word in var word
            Set<String> familySet = new TreeSet<String>();                                                     //stores the set of words with a similar word pattern
            String thisWordPattern = createPattern(guess, word);                                               //creates a pattern according to the guessed char to use to create a new familySet
            if(!patternFamilies.containsKey(thisWordPattern)) patternFamilies.put(thisWordPattern, familySet); //if patternFamilies doesnt have a familySet stored with a certain key yet, it creates a familySet for that key/pattern
            patternFamilies.get(thisWordPattern).add(word);                                                    //adds a word to the familySet with that specific pattern key
        }

        guessedLetters.add(guess);                                                                             //adds the guessed letter to the sorted set of guessed characters
        updateConsideredWords(patternFamilies);
        return occurrences(guess);
    }

    /**
     * If the guessed letter is in the word, it creates a pattern from the current pattern and puts the
     * guessed character into the placeholder in the new pattern
     *
     * @param guess - The character that the player guessed to be in the word
     * @param word - The word from considered words in which the guessed character will be checked
     * @return createdPattern - The re-created pattern with the guessed letter (if the guessed letter is in the word)
     */
    private String createPattern(char guess, String word) {                                                    //creates a pattern according to the guessed character
        char[] wordArray = word.toCharArray();                                                                 //converts the word into a char array to use character indexes
        char[] displayArray = display.toCharArray();                                                           //converts the currently displayed pattern into a char array

        for(int i = 0; i < word.length(); i++) {                                                               //iterates through all the characters of the word
            if(wordArray[i] == guess) displayArray[i*2] = wordArray[i];                                        //if the character at the index in the word is equal to the guessed character
        }                                                                                                      //then the character is put into the placeholder of the displayed pattern (*2 because of the spaces in the displayed pattern)
        String createdPattern = new String(displayArray);                                                      //converts the created pattern back into a String
        return createdPattern;                                                                                 //returns created string
    }

    /**
     * Updates the consideredWords the manager uses by choosing the familySet with the largest
     * number of different words and passing those words to consideredWords. Those words are
     * later used to choose another candidate set of words until there is only 1 left
     *
     * @param patternFamilies - Local TreeMap in record() containing patterns and
     * familySet's paired to the patterns
     */
    private void updateConsideredWords(Map<String, Set<String>> patternFamilies) {
        int size = 0;                                                                                          //size used to compare other familySets
        for(String keyPattern : patternFamilies.keySet()) {                                                    //goes through all the keySets (patterns) (storing the pattern in currentPattern)

            if(patternFamilies.get(keyPattern).size() > size) {                                                //if the size of the familySet of the current pattern is greater than 0 (or last familySet size checked)
                consideredWords.clear();                                                                       //clears considered words
                consideredWords.addAll(patternFamilies.get(keyPattern));                                       //adds the largest familySet that fits the pattern into consideredWords set

                display = keyPattern;                                                                          //the pattern that was just now checked is set to the pattern that is to be displayed
                size = patternFamilies.get(keyPattern).size();                                                 //sets the size used to compare other familySets to the current largest familySets size
            }
        }
    }

    /**
     * Counts how many times a certain character occurs in the word and updates the amount
     * of wrong guesses allowed.
     *
     * @param guess - The character that the player guessed to be in the word
     * @return occurrences - The number of times the guessed character occurs in the word
     */
    private int occurrences(char guess) {
        int occurrences = 0;

        char[] displayArray = display.toCharArray();                                                           //converts displayed pattern to char array to use indexes
        for(int i = 0; i < display.length(); i++) {                                                            //iterates through the characters of the displayed pattern
            if(displayArray[i] == guess) occurrences++;                                                        //if the guessed letter appears in the pattern it adds 1 to
        }                                                                                                      //the number of occurrences
        if (occurrences == 0) maxWrong--;                                                                      //if the guessed letter is not in the word, the player got it wrong
        //and they have 1 less wrong guesses allowed
        return occurrences;                                                                                    //returns how many times the guessed character occurred in the word
    }
}