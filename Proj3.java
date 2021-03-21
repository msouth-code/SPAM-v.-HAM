import java.io.FileInputStream;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

/** Project 3 skeleton code.
 *  500.112 Gateway Computing Java
 *  Spring 2021
 */

public class Proj3 {
   
   /**
    * The main method.
    * @param args commandline args
    */
   public static void main(String[] args) throws IOException {
      
      
      //FileOutputStream fileOutStream = new FileOutputStream("above.txt");
      //PrintWriter outFS = new PrintWriter(fileOutStream);
   
   
      // This is just an example, you need to implement the program as 
      // specified in the project writeup.
      
      Scanner scnr = new Scanner(System.in);
      
      System.out.print("enter input spam file: "); //spam.txt
      String spamFile = scnr.nextLine();
      
      System.out.print("enter input ham file: "); //ham.txt
      String hamFile = scnr.nextLine();
   
      System.out.print("enter input stop words file: "); //stop_words.txt
      String stopFile = scnr.nextLine();
      
   
   
      String[] stopWords = loadWordsFromFile(stopFile);
      String[] spam = loadWordsFromFile(spamFile);
      String[] sanitizedSpam = getSanitizedWordArray(spam, stopWords);
      int[] spamHistogram = buildHistogram(sanitizedSpam);
      double[] normalizedSpamHistogram = normalizeHistogram(spamHistogram);
      String[] ham = loadWordsFromFile(hamFile);
      String[] sanitizedHam = getSanitizedWordArray(ham, stopWords);
      int[] hamHistogram = buildHistogram(sanitizedHam);
      double[] normalizedHamHistogram = normalizeHistogram(hamHistogram);
      
      //System.out.println("-----");
      //String sms = "i'm at the parking lot waiting. where are you?";
      
      System.out.println("\nEnter text message: "); 
      //String userText = scnr.nextLine();
      String sms = scnr.nextLine();
   
      while (!("EXIT".equals(sms))) {
         double c = classifySMS(sms, normalizedHamHistogram, 
                             normalizedSpamHistogram, stopWords);
         //System.out.println("SMS: " + sms);
         System.out.println("\nSCORE: " + c);
         if (c > 0) {
            System.out.println("class: HAM");
         } else {
            System.out.println("class: SPAM");
         } 
      
         System.out.println("\nEnter text message: "); 
      //String userText = scnr.nextLine();
         sms = scnr.nextLine();
      
      }
      
      if ("EXIT".equals(sms)) {
         System.out.print("\nBYE!");
         return;
      }
      
      /*
      System.out.println("-----");
      sms = "Text win to 293849384 to receive $100 free, we'll pay " +
            "for your utilities " +
            "claim your prize 0871277810910 p/min";
      c = classifySMS(sms, normalizedHamHistogram, 
                      normalizedSpamHistogram, stopWords);
      System.out.println("SMS: " + sms);
      System.out.println("score: " + c);
      if (c > 0) {
         System.out.println("class: HAM");
      } else {
         System.out.println("class: SPAM");
      }*/
      
      //
      
      
   }

   /**
    * This method classifies and SMS, i.e. it determines if a
    * given text message is spam or ham.
    *
    * @param smsText the SMS
    * @param normalizedHamHistogram the normalized histogram of the words 
    * in the ham dataset
    * @param normalizedSpamHistogram the normalized histogram of the words 
    * in the spam dataset
    * @param stopWords a String array with the stop words, loaded from the 
    * file stop_words.txt
    * @return the score of the SMS, it should be positive if it is HAM and 
    * negative if it is SPAM
    */
   public static double classifySMS(String smsText, 
                                    double[] normalizedHamHistogram, 
                                    double[] normalizedSpamHistogram,
                                    String[] stopWords) {
      String[] words = smsText.split(" ");
      String[] swords = getSanitizedWordArray(words, stopWords);
      double total = 0.0;
      for (int i = 0; i < swords.length; i++) {
         String word = swords[i];
         int hashCode = 0;
         if (isNumber(word)) {
            hashCode = 0;
         } else if (isMoney(word)) {
            hashCode = 1;
         } else if (isURL(word)) {
            hashCode = 2;
         } else {
            hashCode = 3 + (getHashCode(word) % 997);
         }
      
         total = total + Math.log(normalizedHamHistogram[hashCode] / 
                                  normalizedSpamHistogram[hashCode]);
      }
      return total;
   }

   /**
    * This method normalizes the histogram in such a way that it has unit
    * length.
    * @param histogram the input histogram
    * @return the unit normalized histogram
    */
   public static double[] normalizeHistogram(int[] histogram) {
      double[] output = new double[histogram.length];
      double norm = 0.0;
      for (int i = 0; i < histogram.length; i++) {
         norm = norm + histogram[i] * histogram[i];
      }
      norm = Math.sqrt(norm);
      for (int i = 0; i < histogram.length; i++) {
         output[i] = histogram[i] / norm;
      }
      return output;
   }

   /**
    * This method receives an array and copies it into another array that
    * is n times larger than the original array.
    * 
    * @param input the input String array
    * @param n make the output array n times larger than the input array
    * @return the output, enlarged array
    */
   public static String[] increaseArraySize(String[] input, int n) {
      //String[] output = new String[100];
      int increasedArrayLength = input.length * n;
      String[] output = new String[increasedArrayLength];
      
      for (int i = 0; i < input.length; i++) {
         output[i] = input[i];
      }
      
      return output;
   
   }

   /**
    * This method receives an array and copies the first size elements into
    * an output array and discards all the elements after size. 
    *
    * @param input the input String array
    * @param size the number of elements to keep
    * @return the output, trimmed array
    */
   public static String[] trimArray(String[] input, int size) {
      String[] trimmedArray = new String[size];
      
      for (int i = 0; i < size; i++) {
         trimmedArray[i] = input[i];
      }
   
      return trimmedArray;
   }

   /**
    * This method reads the filename line by line and calls split(" ") on 
    * each line. It then includes each word reported by split into an array. 
    * Must return an array with all the words. This method does not know ahead
    * of time how many words are present in the file and may not open and read
    * the file two times.
    *
    * @param filename the filename to load
    * @return a String array with all the words loaded from the file
    */
   public static String[] loadWordsFromFile(String filename) 
                                              throws IOException {
      String[] words = new String[100];
      
      FileInputStream fileInStream = new FileInputStream(filename);
      Scanner inFS = new Scanner(fileInStream);
      int totalWords = 0;
      int j = 0;
      
      while (inFS.hasNextLine()) {
         String lineToSplit = inFS.nextLine();
         String[] splitLine = lineToSplit.split(" ");
         totalWords = totalWords + splitLine.length;
         
         for (int i = 0; i < splitLine.length; i++) {
            
            if (totalWords >= words.length) {
               words = increaseArraySize(words, 2);
            }
            
            words[j] = splitLine[i]; 
            j++;
            //totalWords++;  
            
                    
         }
      }     
      // use while loop
      // use scanner
      // double size of array
      // while(scan.hasNextLine())
      
      fileInStream.close(); 
   
      words = trimArray(words, totalWords);
      
      return words;
   }


   /**
    * This method removes (trims) all the occurences of trimChars from the 
    * left of the string. For example if trimChars is {'(', '*'}:
    *
    * "(*(*(((75": returns "75"
    * "*(something()something)": returns "something()something)"
    * "99.95": returns "99.95"
    * "((": returns ""
    * "": returns ""
    *
    * Also, don't forget that the implementation fo this method must be
    * recursive.
    *
    * @param word the word
    * @param trimChars the characters to trim
    * @return the left trimmed word
    */
   public static String leftTrim(String word, char[] trimChars) {
      int i = 0;
      while (isTrimChar(word, trimChars)) {     
         word = word.substring(1, word.length());
         //i++;
         leftTrim(word, trimChars);
      }
            
      return word;
   
   }
   
   
   /**
    * This method checks if a character within a word.
    * is a trimmable character.
    *
    * @param word the word
    * @param trimChars the characters to trim
    * @return boolean stating if char should be trimmed
    */

   public static boolean isTrimChar(String word, char[] trimChars) {
      //int count = 0;
      if ("".equals(word)) {
         return false;
      } else {
      //for (int i = 0; i < word.length(); i++) {
         for (int j = 0; j < trimChars.length; j++) {
            if (word.charAt(0) == trimChars[j]) {
               return true;
            }
         }
      //}
      
         return false;
      }
            
   }

   /**
    * This method reverses a string.
    *
    * Examples:
    * "Happy": returns "yppaH"
    * "123": returns "321"
    * "radar": returns "radar"
    * "": returns ""
    *
    * @param word the word
    * @return the reversed word
    */
   public static String reverse(String word) {
      if ("".equals(word) || word.length() <= 2) {
         return word;
         
      } else { 
      
         int halfIndex = word.length() / 2;
      
         String firstHalf = word.substring(0, halfIndex);
         String secondHalf = word.substring(halfIndex, word.length());
      
         int countfirst = firstHalf.length() - 1;
         String revfirstHalf = "";
         while (countfirst >= 0) {
            char backwardLetterFirst = firstHalf.charAt(countfirst);
            revfirstHalf = revfirstHalf + backwardLetterFirst;
            countfirst = countfirst - 1;
         }
      
         int countsecond = secondHalf.length() - 1;
         String revsecondHalf = "";
         while (countsecond >= 0) {
            char backwardLetterSecond = secondHalf.charAt(countsecond);
            revsecondHalf = revsecondHalf + backwardLetterSecond;
            countsecond = countsecond - 1;
         }
      
      
         //String toPrint = revfirstHalf + revsecondHalf; 
         word = revsecondHalf + revfirstHalf; 
      
         return word;
      }
   }


   /**
    * Same as leftTrim but trims on the right of the string. This method 
    * should work without modification.
    *
    * @param word the word
    * @param trimChars the characters to trim
    * @return the right trimmed word
    */
   public static String rightTrim(String word, char[] trimChars) {
      return reverse(leftTrim(reverse(word), trimChars));
   }

   /**
    * This method searches for item in the array and returns the index
    * in which it finds it, or -1 if item is not in the array.
    *
    * @param array the array to be searched
    * @param item the item to search for
    * @return the lowest index in which appears in the array or -1 if item is 
    * not in the array
    */
   public static int getArrayIndexForItem(String[] array, String item) {
      for (int i = 0; i < array.length; i++) {
         if (item.equals(array[i])) {
            return i;
         }
      }
      
      return -1;
   }
   
   
   /**
    * This method searches for item in the array and returns the index
    * in which it finds it, or -1 if item is not in the array.
    *
    * @param array the array to be searched
    * @param item the item to search for
    * @return the lowest index in which appears in the array or -1 if item is 
    * not in the array
    */
   public static int getArrayIndexForCharItem(char[] array, char item) {
      for (int i = 0; i < array.length; i++) {
         if (item == array[i]) {
            return i;
         }
      }
      
      return -1;
   }


   /*public static int getArrayIndexForIntItem(int[] array) {
      for (int i = 0; i < array.length; i++) {
         if (array[i] == 0) {
            return i;
         }
      }
      
      return -1;
   }*/



   /**
    * This method sanitizes the words array. It goes word by word and left
    * trims the following characters {'.', '(', '"', '\'' }, and right trims
    * the following characters {',', '.', '?', '!', ':', ')', '"', '\'' } from
    * each word. If after trimming the word has length greater than 0 and is
    * not present in the array stopWords then it needs to be included in the
    * return array, otherwise the word is dropped from further consideration.

    * @param words is the input unsanitized array of words
    * @param stopWords is the array with stop words
    * @return the sanitized array constructed as described above
    */
   public static String[] getSanitizedWordArray(String[] words, 
                                                 String[] stopWords) {
      
      //int idk = words.length;
      String[] santizedArray = new String[words.length];
      int j = 0;
      int count = 0;
      
      for (int i = 0; i < words.length; i++) {
         char[] trimLChars = {'.', '(', '"', '\'' };
         char[] trimRChars = {',', '.', '?', '!', ':', ')', '"', '\'' };
         String word = words[i];
         word = leftTrim(word, trimLChars);
         word = rightTrim(word, trimRChars);
         
         
         if (word.length() > 0 && !(isStopWord(word, stopWords))) {
            count++;
            santizedArray[j] = word.toLowerCase();
            j++;
         }
         
      }
      
      santizedArray = trimArray(santizedArray, count);
   
      return santizedArray;
   
   }


   /**
    * This method checks if a word within an array.
    * is a stop word.
    *
    * @param word the word
    * @param stopWords the words to stop
    * @return boolean stating if word should be stopped
    */

   public static boolean isStopWord(String word, String[] stopWords) {
      int indexFound = getArrayIndexForItem(stopWords, word);
      
      if (indexFound >= 0 && indexFound < stopWords.length) {
         return true;
      }         
      
      return false;
      
     
            
   }





   /**
    * This method determines if a given word is a number. For the project 
    * we define number as any word that only contains the symbols: '0', '1'
    * '2', '3', '4', '5', '6', '7', '8, '9', '.', '-'
    * Examples:
    * "75": number
    * "75.54": number
    * "$99.95": not number
    * "-33.2": number
    * "45F": not number
    *
    * @param word the word
    * @return true if the input word is an amount of money, false if not
    */
   public static boolean isNumber(String word) {
      char[] allowed = {'0', '1', '2', '3', '4', '5', 
                        '6', '7', '8', '9', '.', '-'};
                        
      //boolean valid = true;
      
      for (int i = 0; i < word.length(); i++) {
         int validNum = getArrayIndexForCharItem(allowed, word.charAt(i)); 
         
         if (validNum < 0) {
            return false;
         }
         
         
      }
      
      return true;
      
      
      /*for (int i = 0; i < word.length(); i++) {
         for (int j = 0; j < allowed.length; j++) {
            if (!(word.charAt(i) == (allowed[j]))) {
               valid = false;
               return valid;
            }
         }
      }
                        
      return valid; //FINISH / ASK FOR HELP*/
   
   }
   
   /**
    * This method determines if a given word is an amount of money. For the 
    * project we define money as any word that contains the symbols: '£' or '$'.
    * Please use '\u00A3' instead of '£' for the autograder to work, this is 
    * limitation of Gradescope.
    * 
    * Examples:
    * "75": not money
    * "75bucks": not money
    * "$99.95": money
    * "$$$": money
    *
    * @param word the word
    * @return true if the input word is an amount of money, false if not
    */
   public static boolean isMoney(String word) {
      char[] wordChars = wordCharsToArray(word);
      int dollarIndex = getArrayIndexForCharItem(wordChars, '$');
      int euroIndex = getArrayIndexForCharItem(wordChars, '£');
      
      if (dollarIndex >= 0 || euroIndex >= 0) {
         return true;
      }         
      
      return false;
      
   
      //return (word.contains("$") || word.contains("£"));
   }

   /**
    * This method determines if a given word is an URL. For the project we 
    * define URL as a word that has at least one dot and no pair of 
    * consecutive dots. Examples:
    * "my...friend": not a URL
    * "my...F.r.i.e.n.d": not a URL
    * "cs.jhu.edu": a URL
    * "GET.OUT.OF.HERE": a URL
    *
    * @param word the word
    * @return true if the input word is a URL, false if not
    */
   public static boolean isURL(String word) {
      
      return (word.contains(".") && !(word.contains("..")));
   }

   /**
    * This method puts the individual characters.
    * of a word into a spot in a new array.
    *
    * @param word the word
    * @return the character array
    */

   public static char[] wordCharsToArray(String word) {
   
      char[] returnArray = new char[word.length()];
      
      for (int i = 0; i < returnArray.length; i++) {
         returnArray[i] = word.charAt(i);
      }
      
      return returnArray;
   
   }


   /**
    * This method receives a word and computes its non-negative hash code. 
    * Observe that the hash code returned by this method can be from 0 to the 
    * maximum integer in Java, but for a fixed word the hash code is always 
    * the same.
    *
    * @param word the word
    * @return the hashcode
    */
   public static int getHashCode(String word) {
      int hashCode = word.hashCode() & 0xfffffff;
      return hashCode;
   }

   /**
    * This method receives an array of words (strings) and builds a histogram.
    * The histogram is 1000-dimensional and will be structured as follows:
    *
    * - index 0 contains the count of words in the array for which isNumber is
    * true
    * - index 1 contains the count of words in the array for which isMoney is
    * true
    * - index 2 contains the count of words in the array for which isURL is
    * true
    * - from index 3 to index 999 if the word is not a number, money or URL, 
    * then a hash code of the word (from 0 to 996 will) be computed and will 
    * be counted in index 3 to 999 of the histogram
    *
    * @param words is the array of words
    * @return will return a 1000-dimensional histogram as described above
    */
   public static int[] buildHistogram(String[] words) {
      int[] histogram = new int[1000];
      
      int numCount = 0;
      int moneyCount = 0;
      int urlCount = 0;
      
      for (int i = 0; i < words.length; i++) {
         if (isNumber(words[i])) {
            numCount++;
         }
         
         if (isMoney(words[i])) {
            moneyCount++;
         }
         
         if (isURL(words[i])) {
            urlCount++;
         }
      
      
      }
      
            //int j = 3;
      
      histogram[0] = numCount;
      histogram[1] = moneyCount;
      histogram[2] = urlCount;
     
      
      //for (int j = 3; j < histogram.length; j++) {
      for (int i = 0; i < words.length; i++) {
         if (!(isNumber(words[i])) && !(isMoney(words[i])) 
                                       && !(isURL(words[i]))) {
            int c = getHashCode(words[i]) % 997;
            histogram[c + 3]++;
            //j++;
         }
      }
      //}
      
      for (int i = 0; i < histogram.length; i++) {     
         //int hasZero = getArrayIndexForIntItem(histogram);
         if (histogram[i] == 0) {
            histogram[i] = 1;
         }
      }
      
      return histogram;
   }
}