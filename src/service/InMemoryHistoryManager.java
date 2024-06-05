package service;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> nodeMap;
    private Node head;
    private Node tail;

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public Map<Integer,Node> getMap() {
        return new HashMap<>(nodeMap);
    }

    public Node getNode(Task task) {
        return getMap().get(task.getId());
    }

    public InMemoryHistoryManager() {
        this.nodeMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    public void linkLast(Task task) {
        Node createNode = new Node(task);
        if (head == null) {
            head = createNode;
            tail = createNode;
        } else {
            tail.nextNode(createNode);
            createNode.prevNode(tail);
            tail = createNode;
        }
        if (nodeMap.values().stream().map(Node::getTask).map(Task::getId).anyMatch(Id -> Id.equals(task.getId()))) {
            nodeMap.put(task.getId(), getNode(task));
        } else {
            nodeMap.put(task.getId(), createNode);
        }
    }

    private void removeNode(Task task) {
        Node node = getNode(task);
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = head.getNext();
            head.prevNode(null);
        } else if (node == tail) {
            tail = tail.getPrev();
            tail.nextNode(null);
        } else {
            node.getPrev().nextNode(node.getNext());
            node.getNext().prevNode(node.getPrev());
        }
        nodeMap.remove(node.getTask().getId());
    }

    private void addNode(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task);
        }
        linkLast(task);
    }

    @Override
    public void add(Task task) {
        addNode(task);
    }

    @Override
    public void remove(Task task) {
        removeNode(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.getTask());
            current = current.getNext();
        }
        return history;
    }

    protected static class Node {
        private Task task;
        private Node prev;
        private Node next;

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
}