import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.*;

import static java.lang.Character.getType;

public class HuffmanEncoding {
    private static final String CLASS_FOLDER = HuffmanEncoding.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final String FREQUENCY_PATH = "frequency.txt";
    private static final String CODES_PATH = "codes.txt";
    public static final String TREE_PATH = "tree.txt";
    public static final String BINARY_PATH = "compressed.bin";

    private static final char[] CHARS_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.:+-*/\\\n ".toCharArray();


    private static int findChar(char c) {
        if((Character.isSpace(c) || Character.isWhitespace(c) || Character.isSpaceChar(c)) && c!='\n') {
            return CHARS_SET.length-1;
        } else {
            for (int i = 0; i < CHARS_SET.length; i++) {
                if (CHARS_SET[i] == c) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static int[] getFrequency(String text) {
        int[] freqArr = new int[CHARS_SET.length];
        int i;
        for (char c : text.toCharArray()) {
            i = findChar(c);
            if(i!=-1) {
                freqArr[i]+=1;
            }
        }
        return freqArr;
    }

    private static String getSpecialCharacters(char special) {
        if(special == '\n') {
            return "\\n";
        } else if(special == '\\') {
            return "\\\\";
        } else {
            return Character.toString(special);
        }
    }

    private static void writeFrequencyTable(Map<Character, Integer> frequencyTable) {
        if(frequencyTable!=null && frequencyTable.size()>0) {
            String filePath = CLASS_FOLDER+FREQUENCY_PATH;
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
                for (Map.Entry<Character, Integer> entry : frequencyTable.entrySet()) {
                    String c = entry.getKey()!=null? String.valueOf(entry.getKey()):"";
                    String n = String.valueOf(entry.getValue());
                    String cn = c+":"+n;
                    bw.write(cn);
                    bw.newLine();
                }
                bw.flush();
                System.out.println("File written to: "+filePath);
            } catch (IOException e) {
            }
        }
    }

    private static void writeHuffmanCodes(String[] huffCodes) {
        if(huffCodes.length>0) {
            String filePath = CLASS_FOLDER+CODES_PATH;
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
                for (int i = 0; i < huffCodes.length; i++) {
                    if(huffCodes[i]!=null && !huffCodes[i].isEmpty()) {
                        String c;
                        if(Character.isLetterOrDigit(CHARS_SET[i])) {
                            c = String.valueOf(CHARS_SET[i]);
                        } else {
                            c = getSpecialCharacters(CHARS_SET[i]);
                        }
                        String h = huffCodes[i];
                        String ch = c+":"+h;
                        bw.write(ch);
                        bw.newLine();
                    }
                }
                bw.flush();
                System.out.println("File written to: "+filePath);
            } catch (IOException e) {
            }
        }
    }

    private static CharNode buildHuffman(int[] freqArr) {
        CharNode root = null;
        MyPriorityQueue queue = new MyPriorityQueue(CHARS_SET.length);
        for (int i = 0; i < CHARS_SET.length; i++) {
            if(freqArr[i]>0) {
                CharNode node = new CharNode(CHARS_SET[i], freqArr[i]);
                node.left = null;
                node.right = null;
                queue.offer(node);
            }
        }

        for(;;) {
            if(queue.size()<=1) {
                break;
            }
            CharNode minNode = queue.poll();
            CharNode secondMinNode = queue.poll();

            CharNode internalNode = new CharNode(minNode.getFrequency()+secondMinNode.getFrequency());
            internalNode.setLeft(minNode);
            internalNode.setRight(secondMinNode);
            root = internalNode;
            queue.offer(internalNode);
        }
        return queue.poll();
    }

    private static String[] getHuffmanCodes(CharNode root, String hCode, String[]huffCodes) {
        if(root==null) {
            return huffCodes;
        }

        if(root.getLeft()==null && root.getRight()==null) {
            char c = root.getCh();
            int i = findChar(c);
            if(i!=-1) {
                huffCodes[i] = hCode;
                return huffCodes;
            }
        }
        String leftCode = hCode+"0";
        String rightCode = hCode+"1";
        getHuffmanCodes(root.getLeft(), leftCode, huffCodes);
        getHuffmanCodes(root.getRight(), rightCode, huffCodes);

        return huffCodes;
    }

    /**
     * check if there's a possible prefix word in Huffman codes
     * @param huffCodes
     * @return
     */
    private static boolean isPrefix(String[] huffCodes) {
        Map<String, Character> prefixNodes = new HashMap<>(huffCodes.length, 1.0f);
        boolean res = false;
        for (int i = 0; i < huffCodes.length; i++) {
            if(huffCodes[i]!=null) {
                prefixNodes.put(huffCodes[i], CHARS_SET[i]);
            }
        }
        StringBuilder s;
        for (Map.Entry<String, Character> entry : prefixNodes.entrySet()) {
            for (Map.Entry<String, Character> entry2 : prefixNodes.entrySet()) {
                if(entry.getValue()!=entry2.getValue()) {
                    s = new StringBuilder();
                    String code1 = entry.getKey();
                    String code2 = entry2.getKey();
                    for (int i = 0; i < entry2.getKey().length(); i++) {
                           s.append(code2.charAt(i));
                           if(code1.equals(s.toString())) {
                               System.out.println("Char "+code1+" is a prefix of Char "+code2);
                               res = true;
                               break;
                           }
                    }
                }
            }
        }
        return res;
    }

    private static void writeHuffTree(CharNode node, BufferedWriter writer) {
        if (node != null) {
            try {
                if (node.getLeft() == null && node.getRight() == null) {
                    // Leaf node with character
                    if(Character.isLetterOrDigit(node.getCh())) {
                        writer.write("0 " + node.getCh() + "\n");
                    } else {
                        writer.write("0 " + getSpecialCharacters(node.getCh()) + "\n");
                    }
                } else {
                    writer.write("1\n");  // Internal node
                    writeHuffTree(node.getLeft(), writer);
                    writeHuffTree(node.getRight(), writer);
                }
            } catch (IOException e) {
                System.out.println("Failed to write: "+TREE_PATH);
            }
        }
    }


    private static void writeBinaryFile(String[] huffCodes, String Content) {
        StringBuilder s = new StringBuilder();
        int i;
        for (char c : Content.toCharArray()) {
            i = findChar(c);
            if(i!=-1) {
                s.append(huffCodes[i]);
            }
        }
        String encodedData = s.toString();
        String filename = CLASS_FOLDER+BINARY_PATH;
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename, false)))) {
            int byteAccumulator = 0;
            int count = 0;

            // bit by bit
            for (char bit : encodedData.toCharArray()) {
                byteAccumulator = byteAccumulator << 1;
                if (bit == '1') {
                    byteAccumulator |= 1;
                } else {
                    byteAccumulator |= 0;
                }
                count++;

                if (count%8 == 0 && count>0) {
                    out.write(byteAccumulator);
                    byteAccumulator = 0;
                }
            }
            System.out.println("bits left: "+(count % 8));
            // Process the left bits (last incomplete byte)
            if (count % 8 != 0) {
                int redundantBits = 8 - (count % 8);
                // fill missing bits with 0
                byteAccumulator = byteAccumulator << redundantBits;
                out.write(byteAccumulator);
                out.write(count % 8);
            } else {
                out.write(8);
            }
        } catch (IOException e) {

        }

    }

    /**
     * A full set of characters we want to include in our frequency list
     * @return
     */
    private static Set<Character> getFrequencyCharSet() {
        Set<Character> charSet = new HashSet<>(39, 1.0f);
        charSet.add(',');
        charSet.add('.');
        charSet.add(null);
        for (char ch = 'a'; ch <= 'z'; ch++) {
            charSet.add(ch);
        }
        for (char digit = '0'; digit <= '9'; digit++) {
            charSet.add(digit);
        }
        return charSet;
    }

    private static Map<Character, Integer> countCharFrequency(String content) {
        Map<Character, Integer> frequencyTable = new HashMap<>(39, 1.0f);
        for (char c : content.toCharArray()) {
            if(Character.isSpaceChar(c) || Character.isWhitespace(c) || Character.isSpace(c)) {
                frequencyTable.compute(null, (key, oldValue) -> oldValue == null ? 0 : oldValue + 1);
            } else if (c=='.' || c==',' || ((((1 << Character.UPPERCASE_LETTER) |
                    (1 << Character.LOWERCASE_LETTER) |
                    (1 << Character.DECIMAL_DIGIT_NUMBER)) >> getType((int)c)) & 1) != 0) {
                if(Character.isUpperCase(c)) c = Character.toLowerCase(c);
                frequencyTable.compute(c, (key, oldValue) -> oldValue == null ? 0 : oldValue + 1);
            }
        }
        return frequencyTable;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = CLASS_FOLDER+"/"+scanner.nextLine();
        File txtFile = new File(filePath);
        String line, content;
        StringBuilder contentBuilder = new StringBuilder();
        if(txtFile.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile))) {
                while ((line = bufferedReader.readLine())!=null) {
                    contentBuilder.append(line).append("\n");
                }
                if(contentBuilder.length()>0) {
                    content = contentBuilder.toString();
                    Set<Character> charSet = getFrequencyCharSet();
                    Map<Character, Integer> frequencyTable = countCharFrequency(content);
                    if(charSet.removeAll(frequencyTable.keySet())) {
                        for (char c : charSet) {
                            frequencyTable.put(c, 0);
                        }
                    }
                    writeFrequencyTable(frequencyTable);
                    int[] freqArr = getFrequency(content);
                    CharNode root = buildHuffman(freqArr);
                    String[] huffCodes = new String[freqArr.length];
                    huffCodes = getHuffmanCodes(root, "", huffCodes);
                    isPrefix(huffCodes);
                    writeHuffmanCodes(huffCodes);
                    String treeFilePath = CLASS_FOLDER+TREE_PATH;
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(treeFilePath, false))) {
                        writeHuffTree(root, bw);
                    }
                    writeBinaryFile(huffCodes, content);
                }
            } catch (IOException e) {
            }
        } else {
            System.out.println("File not found");
        }
    }


    public static class MyPriorityQueue {

        CharNode[] queue;
        private int size = 0;

        public MyPriorityQueue(int capacity) {
            this.queue = new CharNode[capacity];
        }

        public boolean offer(CharNode charNode) {
            if (charNode != null) {
                int i = size;
                if (i >= queue.length) {
                    int newCapacity = queue.length >> 1;
                    queue = Arrays.copyOf(queue, newCapacity);
                }
                size = i + 1;
                if (i == 0) {
                    queue[0] = charNode;
                } else {
                    int k = i;
                    while (k > 0) {
                        int parent = (k - 1) >>> 1;
                        CharNode parentNode = queue[parent];
                        if (charNode.getFrequency().compareTo(parentNode.getFrequency()) > 0) break;

                        if(charNode.getFrequency().compareTo(parentNode.getFrequency()) == 0 &&
                                charNode.getCh().compareTo(parentNode.getCh()) >= 0) break;


                        queue[k] = parentNode;
                        k = parent;
                    }
                    queue[k] = charNode;
                }
                return true;
            } else {
                throw new NullPointerException();
            }
        }

        public CharNode poll() {
            if (size == 0) {
                return null;
            } else {
                size = size-1;
                int s = size;
                CharNode result = queue[0];
                CharNode x = queue[s];
                queue[s] = null;
                if (s != 0) {
                    int half = size >>> 1;
                    int k = 0;
                    // loop while a non-leaf
                    while (k < half) {
                        int child = (k << 1) + 1;
                        // assume left child is least
                        CharNode c = queue[child];
                        int right = child + 1;
                        if (right < size && (c.getFrequency().compareTo(queue[right].getFrequency()) > 0)) {
                            child = right;
                            c = queue[child];
                        }
                        if (x.getFrequency().compareTo(c.getFrequency()) == 0) {
                            if(x.getCh().compareTo(c.getCh()) <= 0)
                                break;
                        }
                        if (x.getFrequency().compareTo(c.getFrequency()) < 0)
                            break;
                        queue[k] = c;
                        k = child;
                    }
                    queue[k] = x;
                }
                return result;
            }
        }

        public CharNode peek() {
            if (size == 0) {
                return null;
            } else {
                return queue[0];
            }
        }

        public int size() {
            return size;
        }
    }


    public static class CharNode implements Serializable  {
        private static final long serialVersionUID = 1L;
        private Character ch;
        private Integer frequency;
        private CharNode left, right;

        public CharNode(Integer frequency) {
            this.ch = '\0';
            this.frequency = frequency;
        }

        public CharNode(Character ch, Integer frequency) {
            this.ch = ch;
            this.frequency = frequency;
        }

        public Character getCh() {
            return ch;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public CharNode getLeft() {
            return left;
        }

        public void setLeft(CharNode left) {
            this.left = left;
        }

        public CharNode getRight() {
            return right;
        }

        public void setRight(CharNode right) {
            this.right = right;
        }

        @Override
        public String toString() {
            return ch + ":" + frequency;
        }
    }
}
