import java.io.*;
import java.util.*;

public class ScriptioContinua {
    private static final String CLASS_FOLDER = ScriptioContinua.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    private static Set<String> dict = new HashSet<>(10000, 1.0f);
    private static String FILE_PATH = "wordlist.txt";
    private static int longestWord = 0;

    private static void loadDict() {
        String path = CLASS_FOLDER+FILE_PATH;
        File dictFile = new File(path);
        String word;
        if(dictFile.exists()) {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(dictFile))) {
                while ((word = bufferedReader.readLine()) != null) {
                    dict.add(word);
                    longestWord = Math.max(longestWord, word.length());
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    private static boolean isWord(String word) {
        return dict.contains(word);
    }

    private static void dynamicSolution(String s) {
        int len = s.length();
        String word;
        boolean[] dp = new boolean[len+1];
        dp[0] = true;
        String[] words = new String[len+1];
        for(int i = 1; i <= len; i++) {
            if(i<=longestWord) {
                for(int j = i-1; j >=0; j--) {
                    word = s.substring(j, i);
                    if(isWord(word) && dp[j]==true) {
                        dp[i] = true;
                        words[i] = word;
                        break;
                    }
                }
            } else {
                for(int j = i-1; j >=i-longestWord; j--) {
                    word = s.substring(j, i);
                    if(isWord(word) && dp[j]==true) {
                        dp[i] = true;
                        words[i] = word;
                        break;
                    }
                }
            }
        }
        System.out.println(dp[len]);
        if(dp[len]==true) {
            List<String> wordList = new ArrayList<>();
            System.out.print("s = ");
            int i = len;
            while (i>0) {
                word = words[i];
                wordList.add(word);
                i = i-word.length();
            }
            for (i=wordList.size()-1; i>=0; i--) {
                System.out.print(wordList.get(i)+" ");
            }
        }
    }

    public static void main(String[] args) {
        loadDict();
        Scanner scanner = new Scanner(System.in);
        String s;
        s = scanner.nextLine();
        dynamicSolution(s);
    }

}