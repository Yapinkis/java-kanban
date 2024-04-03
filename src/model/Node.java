package model;

public class Node {
    //Верная ли дириктория для размещения Node?
    private Task task;
    private Node prev;
    private Node next;//Нужно ли их оставлять private?

    public Node(Task task) {
        this.task = task;
        this.prev = null;
        this.next = null;
    }

    public void prevNode(Node prev) {
        this.prev = prev;
    }

    public void nextNode(Node next) {
        this.next = next;
    }

    public Task getTask() {
        return task;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }
}
