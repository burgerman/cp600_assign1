import java.io.*;

public class HuffmanDecoding {
    private static final String CLASS_FOLDER = HuffmanDecoding.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final String DECODE_PATH = "decoded.txt";


    private static char readWithSpecialCharacters(String line) {
        if (line.startsWith("0")) {
            if(line.endsWith("\\")) {
                return '\\';
            } else if(line.endsWith("\\n")) {
                return '\n';
            } else {
                return line.charAt(2);
            }
        } else {
            return '\0';
        }
    }

    private static HuffmanEncoding.CharNode readTreeFile(BufferedReader reader) {
        try {
            String line = reader.readLine();
            if(line != null) {
                if (line.startsWith("0")) {
                   // get the character
                    char ch = readWithSpecialCharacters(line);
                    return new HuffmanEncoding.CharNode(ch, 0);
                } else {
                    HuffmanEncoding.CharNode node = new HuffmanEncoding.CharNode('\0', 0);
                    node.setLeft(readTreeFile(reader));
                    node.setRight(readTreeFile(reader));
                    return node;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read Tree file content");
        }
        return null;
    }

    private static String readEncodedData(){
        String binaryFilePath = CLASS_FOLDER+ HuffmanEncoding.BINARY_PATH;
        try (DataInputStream dataInput = new DataInputStream(new BufferedInputStream(new FileInputStream(binaryFilePath)))) {
            StringBuilder encodedData = new StringBuilder();
            int byteValue;
            while ((byteValue = dataInput.read()) != -1 && dataInput.available()>1) {
                for (int i = 7; i >= 0; i--) {
                    if(((byteValue >> i) & 1)==1) {
                        encodedData.append('1');
                    } else {
                        encodedData.append('0');
                    }

//                    encodedData.append((byteValue >> i) & 1);
                }
            }
            // check if there are left bits to read
            if(byteValue != -1) {
                int lastByte = byteValue;
                int validBitsNum = dataInput.read();
                for(int i =7; i>= (validBitsNum%8); i--) {
                    if (((lastByte >> i) & 1) == 1) {
                        encodedData.append('1');
                    } else {
                        encodedData.append('0');
                    }
//                    encodedData.append((lastByte >> i) & 1);
                }
            }
            return encodedData.toString();

        } catch (IOException e) {

        }
        return null;
    }

    private static String decodeData(HuffmanEncoding.CharNode rootNode, String encodedData) {
        StringBuilder decodedContent = new StringBuilder();
        HuffmanEncoding.CharNode currentNode = rootNode;
        for (char bit : encodedData.toCharArray()) {

            currentNode = bit=='0'? currentNode.getLeft() : currentNode.getRight();

            // If a leaf node is reached, append the character
            if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                if(Character.isLetter(currentNode.getCh())) {
                    decodedContent.append(Character.toLowerCase(currentNode.getCh()));
                } else {
                    decodedContent.append(currentNode.getCh());
                }
                currentNode = rootNode;
            }
        }
        return decodedContent.toString();
    }

    private static void writeDecodedContent(String content) {
        String filePath = CLASS_FOLDER+ DECODE_PATH;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            bw.write(content);
        } catch (IOException e) {
            System.out.println("Failed to write file: "+DECODE_PATH);
        }
    }

    public static void main(String[] args) {
        String treeFilePath = CLASS_FOLDER + HuffmanEncoding.TREE_PATH;
        try (BufferedReader reader = new BufferedReader(new FileReader(treeFilePath))) {
            HuffmanEncoding.CharNode root = readTreeFile(reader);
            String loadEncodedData = readEncodedData();
            String content = decodeData(root, loadEncodedData);
            writeDecodedContent(content);
        } catch (IOException e) {
            System.out.println("Error reading tree file: "+HuffmanEncoding.TREE_PATH);
        }
    }
}
