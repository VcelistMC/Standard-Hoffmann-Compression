import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class StandardHuffman {
    private ArrayList<Node> huffmanTree = new ArrayList<>();
    private HashMap<String, String> binaryTable = new HashMap<>(), letterTable = new HashMap<>();
    private String compressedText, decompressedText;
    private String dicPath =  System.getProperty("user.home") + "/Desktop/Dictionary.txt";
    private String decompressedPath = System.getProperty("user.home") + "/Desktop/Decompressed.txt";
    private String compressedPath = System.getProperty("user.home") + "/Desktop/Compressed.txt";

    private void writeCompressed() throws Exception{
        File dicFile = new File(dicPath);
        FileWriter dicWriter = new FileWriter(dicFile);

        File compressedFile = new File(compressedPath);
        FileWriter compressedWriter = new FileWriter(compressedFile);
        for(Node node: huffmanTree){
            dicWriter.write(node.value + ":" + node.binaryCode + "\n");
        }

        compressedWriter.write(compressedText);
        compressedWriter.close();
        dicWriter.close();
    }
    
    private void readCompressed() throws Exception{
        File inputString = new File(compressedPath);
        File inputDic = new File(dicPath);
        if(inputString.exists() && inputDic.exists()){
            Scanner inputReader = new Scanner(inputString);
            Scanner dicReader = new Scanner(inputDic);
            
            while(inputReader.hasNext())
                compressedText += inputReader.nextLine();
            while(dicReader.hasNext()){
                String[] codeLetterPair = dicReader.nextLine().split(":");
                letterTable.put(codeLetterPair[1], codeLetterPair[0]);

            }
        }
        else{
            System.err.println("Either the compressed text or the dictionary doesn't exist, make sure they're in the same folder and try again");
        }
    }

    private void writeDecompressed() throws Exception{
        File outputFile = new File(decompressedPath);
        FileWriter fileWriter = new FileWriter(outputFile);
        fileWriter.write(decompressedText);
        fileWriter.close();
    }

    private void readDecompressed(String path) throws Exception{
        File inputString = new File(path);
        if(inputString.exists()){
            Scanner inputReader = new Scanner(inputString);
            while(inputReader.hasNext())
                decompressedText += inputReader.nextLine();
        }
        else{
            System.err.println("File doesn't exist, try again");
        }
    }
    
    private void clearVariables(){
        binaryTable.clear();
        letterTable.clear();
        compressedText = "";
        decompressedText = "";
    }


    private void genCodes(String input){
        calcProb(input);
        int ogSize = huffmanTree.size();

        while(huffmanTree.size() > 2){
            Collections.sort(huffmanTree);

            Node node1 = huffmanTree.get(0);
            huffmanTree.remove(0);
            
            Node node2 = huffmanTree.get(0);
            huffmanTree.remove(0);

            Node compositeNode = Node.combine(node1, node2);
            huffmanTree.add(compositeNode);
        }
        
        Collections.sort(huffmanTree);
        huffmanTree.get(0).binaryCode = "1";
        huffmanTree.get(1).binaryCode = "0";

        int currNodeIndex = 0;
        while(huffmanTree.size() < ogSize){
            Node currNode = huffmanTree.get(currNodeIndex);
            if(currNode.isComposite()){
                ArrayList<Node> splitNodes = currNode.split();
                huffmanTree.remove(currNodeIndex);
                huffmanTree.add(splitNodes.get(0));
                huffmanTree.add(splitNodes.get(1));
                currNodeIndex = -1;   
            }
            currNodeIndex++;
        }

        for(Node node: huffmanTree){
            binaryTable.put(node.value, node.binaryCode);
        }
    }

    private void printStats(){
        int compressedSize = compressedText.length();
        int originalSize = decompressedText.length() * 8;
        double compressionRatio = ((double) compressedSize) / originalSize;
        System.out.println("===========================================================");
        System.out.println("Compressed Text: " + compressedText + "\n");
        System.out.println("Original size: " + originalSize);
        System.out.println("Compressed Size " + compressedSize);
        System.out.println("Compression ratio: " + compressionRatio);
    }

    private void calcProb(String input){
        HashMap<String, Integer> letterProb = new HashMap<String, Integer>();
        for(int i = 0; i < input.length(); i++){
            String letter = String.valueOf(input.charAt(i));
            int oldValue = letterProb.get(letter) == null? 0 : letterProb.get(letter);

            letterProb.put(letter, oldValue + 1);
        }

        getCount(letterProb);

        int stringLength = input.length();
        for(var entry : letterProb.entrySet()){
            Double prob = Double.valueOf(((double) entry.getValue()) / stringLength);
            String letter = entry.getKey();

            huffmanTree.add(new Node(letter, prob));
        }
    }

    private void getCount(HashMap<String, Integer> letterProb){
        for (var set : letterProb.entrySet()){
            System.out.println(set.getKey() + ": " + set.getValue());
        }
    }

    public void compress(String path) throws Exception{
        clearVariables();
        readDecompressed(path);
        genCodes(decompressedText);
        for(int i = 0; i < decompressedText.length(); i++){
            String letter = String.valueOf(decompressedText.charAt(i));
            compressedText += binaryTable.get(letter);
        }
        writeCompressed();
        printStats();
    }

    public void decompress() throws Exception {
        clearVariables();
        readCompressed();
        String tempKey = "";
        for(int i = 0; i < compressedText.length(); i++){
            tempKey += String.valueOf(compressedText.charAt(i));
            String letter = letterTable.get(tempKey);
            if(letter != null){
                decompressedText += letter;
                tempKey = "";
            }
        }
        writeDecompressed();
    }

    public void run()throws Exception{
        Scanner scanner = new Scanner(System.in);
        int operation = 0;

        while(operation != 3){
            System.out.println("1.Compress\n2.Decompress\n3.exit");
            operation = scanner.nextInt();
            scanner.nextLine();
            switch (operation) {
                case 1:
                    System.out.print("enter file path: ");
                    String inputPath = scanner.nextLine();
                    compress(inputPath);
                    break;
                case 2:
                    decompress();
                    break;
                default:
                    continue;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        StandardHuffman hoof = new StandardHuffman();
        try{
            hoof.run();
        }
        catch(Exception e){e.printStackTrace();}
    }
}