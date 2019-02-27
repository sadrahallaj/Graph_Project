package sample;

public class Node {
    private int index;
    private boolean isVisited;

    public Node(int index){
        this.isVisited = false;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

}
