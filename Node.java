import java.util.ArrayList;

class Node implements Comparable<Node>{
    public String value;
    public Double probability;
    public Node left;
    public Node right;
    public String binaryCode;

    boolean isComposite(){ return value.length() > 1; }

    Node(String val, Double prob){
        this.value = val;
        this.probability = prob;
        this.left = null;
        this.right = null;
        this.binaryCode = "";
    }

    Node(String val, Double prob, Node l, Node r){
        this.value = val;
        this.probability = prob;
        this.left = l;
        this.right = r;
        this.binaryCode = "";
    }

    static public Node combine(Node l, Node r){
        String newString = l.value + r.value;
        Double newProb = l.probability + r.probability;
        
        return new Node(newString, newProb, l, r);
    }

    public ArrayList<Node> split(){
        ArrayList<Node> splitNodes = new ArrayList<>();

        if(left.compareTo(right) < 0){
            left.binaryCode = this.binaryCode + "1";
            right.binaryCode = this.binaryCode + "0";
        }
        else{
            left.binaryCode = this.binaryCode + "0";
            right.binaryCode = this.binaryCode + "1";
        }

        splitNodes.add(left);
        splitNodes.add(right);

        return splitNodes;
    }

    @Override
    public int compareTo(Node o) {
        return this.probability.compareTo(o.probability);
    }

    @Override
    public String toString() {
        return "Node [binaryCode=" + binaryCode + ", probability=" + probability + ", value=" + value + "]";
    }
}