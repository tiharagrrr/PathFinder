public class Edge {

    private Vertex start;
    private Vertex end;
    private Integer weight; // Changed from Integer to int

    public Edge(Vertex startV, Vertex endV, Integer inputWeight){ // Changed input type from Integer to int
        this.start = startV;
        this.end = endV;
        this.weight = inputWeight != null ? inputWeight : 1; // Set default weight to 1 if inputWeight is null
    }

    public Vertex getStart() {
        return this.start;
    }

    public Vertex getEnd() {
        return this.end;
    }

    public Integer getWeight() { // Changed return type from Integer to int
        return this.weight;
    }
}
