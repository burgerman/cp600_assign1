import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * The overall time complexity is O(nlogn)
 */
public class JumbleWord {


    // the longest English word has 45 letters
    private static int LONGEST_ENGLISH_WORD = 45;

    private static String preSortString(String str) {
        char[] charArray = str.toCharArray();
        // In Java8, to sort all English words, it will use insertion sort
        // the INSERTION_SORT_THRESHOLD is 47
        // the time complexity will be a constant because the num of letters won't exceed 45
        Arrays.sort(charArray);
        return new String(charArray);
    }

    /**
     *
     * @param filePath
     * @return a map of sorted words in alphabetical order
     * key: sorted word in alphabetical order
     * value: a list of original words
     * Time complexity: O(1)
     * Space Complexity: O(n^2)
     */
    private static Map<String, List<String>> loadDictionary(String filePath) {
        File dictFile = new File(filePath);
        if(dictFile.exists()) {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(dictFile))) {
                // get the total num of lines of the file
                // Time complexity O(n)
                int lineNum = (int)Files.lines(Paths.get(dictFile.getAbsolutePath())).count();
                // preallocate the space and fix the size of hash map to avoid resizing later
                Map<String, List<String>> wordMap = new HashMap<>(lineNum, 1.0f);
                String word;
                String sortedWord;
                List<String> wordList;
                // iterate the list of dictionary and presort letters of the word in alphabetical order
                while((word=bufferedReader.readLine())!=null) {
                    sortedWord = preSortString(word);
                    // the average time complexity of lookup remains O(1)
                    if(!wordMap.containsKey(sortedWord)) {
                        wordList = new ArrayList<>();
                    } else {
                        wordList = wordMap.get(sortedWord);
                    }
                    wordList.add(word);
                    wordMap.put(sortedWord, wordList);
                }
                return wordMap;
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return null;
    }

    private static void mergeWordsAndIndices(List<String> words, List<List<Integer>> indices, int l, int m, int r) {
        int leftSize = m-l+1;
        int rightSize = r-m;

        List<String> leftWords = new ArrayList<>(leftSize);
        List<String> rightWords = new ArrayList<>(rightSize);

        List<List<Integer>> leftIndices = new ArrayList<>(leftSize);
        List<List<Integer>> rightIndices = new ArrayList<>(rightSize);
        int index;
        for(int i=0; i<leftSize; i++) {
            index = l+i;
            leftWords.add(words.get(index));
            leftIndices.add(indices.get(index));
        }

        for(int j=0; j<rightSize; j++) {
            index = m+1+j;
            rightWords.add(words.get(index));
            rightIndices.add(indices.get(index));
        }

        int i=0, j=0;
        int k=l;

        while(i<leftSize && j<rightSize) {
            if(leftWords.get(i).compareTo(rightWords.get(j))<=0) {
                words.set(k, leftWords.get(i));
                indices.set(k, leftIndices.get(i));
                i++;
            } else {
                words.set(k, rightWords.get(j));
                indices.set(k, rightIndices.get(j));
                j++;
            }
            k++;
        }

        while(i<leftSize) {
            words.set(k, leftWords.get(i));
            indices.set(k, leftIndices.get(i));
            i++;
            k++;
        }

        while(j<rightSize) {
            words.set(k, rightWords.get(j));
            indices.set(k, rightIndices.get(j));
            j++;
            k++;
        }
    }

    private static void mergeSortWordsAndIndices(List<String> words, List<List<Integer>> indices, int l, int r) {
        if(l < r) {
            int m = l+(r-l)/2;
            mergeSortWordsAndIndices(words, indices, l, m);
            mergeSortWordsAndIndices(words, indices, m + 1, r);
            mergeWordsAndIndices(words, indices, l, m, r);
        }
    }


    private static void sortWordsAndIndices(List<String> words, List<List<Integer>> indices) {
        int n = words.size();
        String tempWord;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (words.get(j).compareTo(words.get(j + 1)) > 0) {
                    // Swap words
                    tempWord = words.get(j);
                    words.set(j, words.get(j + 1));
                    words.set(j + 1, tempWord);

                    // Swap frequencies to match word swap
                    List<Integer> tempIndices = indices.get(j);
                    indices.set(j, indices.get(j + 1));
                    indices.set(j + 1, tempIndices);
                }
            }
        }
    }

    private static int binarySearchWords(List<String> arr, int l, int r, String word)
    {
        int low = l;
        int high = r;
        int mid;
        while (low <= high) {
            mid = low +(high-low) / 2;
            if (arr.get(mid).compareTo(word)<0) {
                low = mid + 1;
            } else if (arr.get(mid).compareTo(word)>0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        // not found.
        return -1;
    }

    private static void binaryInsertionSort(List<String> arr) {
        for (int i = 1; i < arr.size(); i++) {
            int j = i -1;
            String current = arr.get(i);
            int loc = binarySearchWords(arr, 0, i, current);
            while( j >= loc) {
                arr.set(j+1, arr.get(j));
                j--;
            }
            arr.set(j+1, current);
        }
    }


    private static void lookUpJumbledWord(String filePath, String jumbledWord) {
        String sortedJumbledWord = preSortString(jumbledWord);
        List<String> words, sortedWordList;
        List<List<Integer>> wordsIndices;
        File dictFile = new File(filePath);
        if(dictFile.exists()) {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(dictFile))) {
                // get the total num of lines of the file
                int lineNum = (int)Files.lines(Paths.get(dictFile.getAbsolutePath())).count();
                // preallocate the space and fix the size of hash map to avoid resizing later
                words = new ArrayList<>(lineNum);
                String word;
                String sortedWord;
                int index;
                wordsIndices = new ArrayList<>(lineNum);
                sortedWordList = new ArrayList<>(lineNum);
                // iterate the list of dictionary and presort letters of the word in alphabetical order
                // the time complexity is O(n^2)
                List<Integer> tmpList;
                for (int i = 0; i < lineNum; i++) {
                    word=bufferedReader.readLine();
                    words.add(i, word);
                    sortedWord = preSortString(word);
                    index = sortedWordList.indexOf(sortedWord);
                    if(index!=-1) {
                        tmpList = wordsIndices.get(index);
                        tmpList.add(i);
                        wordsIndices.set(index, tmpList);
                    } else {
                        List<Integer> indicesOfWords = new ArrayList<>();
                        indicesOfWords.add(i);
                        sortedWordList.add(sortedWord);
                        index = sortedWordList.lastIndexOf(sortedWord);
                        wordsIndices.add(index, indicesOfWords);
                    }
                }
                mergeSortWordsAndIndices(sortedWordList, wordsIndices, 0, sortedWordList.size()-1);
                int targetIndex = binarySearchWords(sortedWordList, 0, sortedWordList.size()-1, sortedJumbledWord);
                if(targetIndex!=-1) {
                    for (Integer id : wordsIndices.get(targetIndex)) {
                        System.out.println(words.get(id));
                    }
                } else {
                    System.out.println("UNDEFINED");
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
//        Map<String, List<String>> wordMap = loadDictionary("list.txt");
//        if(wordMap != null) {
//            String unscrambledWord;
//            List<String> words;
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNext()) {
//                if(scanner.hasNext("quit")) {
//                    System.out.println("Bye");
//                    break;
//                }
//                unscrambledWord = scanner.nextLine();
//                if(unscrambledWord.length()>LONGEST_ENGLISH_WORD) {
//                    System.out.println("INVALID INPUT of ENGLISH WORD");
//                } else {
//                    words = wordMap.get(preSortString(unscrambledWord));
//                    if(words!=null){
//                        words.forEach(w->System.out.println(w));
//                    } else {
//                        System.out.println("UNDEFINED");
//                    }
//                }
//            }
//        }

        String classPath = JumbleWord.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String filePath = classPath+"/list.txt";
        String unscrambledWord;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if(scanner.hasNext("quit")) {
                System.out.println("Bye");
                break;
            }
            unscrambledWord = scanner.nextLine();
            if(unscrambledWord.length()>LONGEST_ENGLISH_WORD) {
                System.out.println("INVALID INPUT of ENGLISH WORD");
            } else {
                lookUpJumbledWord(filePath, unscrambledWord);
            }
        }
    }
}
