package service;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> linkedMap;
    private Node head;
    private Node tail;
    //На самом деле, модификатор не указал по неопытности...Имеет смысл их сделать private

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

        //Насколько это правильно, учитывая то, что все модели с данными у нас храняться в диреткории model?
        //И это нормальная практика, использовать вложенные классы?
        //Если сделать класс private то в тестовой директории не получится получить к нему доступ через импорты
        //Допустимо ли сделать модификатор protected
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public Map<Integer,Node> getMap() {
        return new HashMap<>(linkedMap);
    } //Я так понял, что нужно возвращать копию мапы, тем самым не давая доступ к основным её данным

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
        linkedMap.put(task.getId(), createNode);
    }

    public InMemoryHistoryManager() {
        this.linkedMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    private void removeNode(Node node) {
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
        linkedMap.remove(node.getTask().getId());
    }

    private void addNode(Task task) {
        if (linkedMap.containsKey(task.getId())) {
            remove(linkedMap.get(task.getId()));
        }
        linkLast(task);
    }

    private ArrayList<Task> getHistoryNode() {
        ArrayList<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.getTask());
            current = current.getNext();
        }
        return history;
    }


    @Override
    public void add(Task task) {
        addNode(task);
    } //Кажется я понял, это инкапсуляция чистой воды....наверное)

    @Override
    public void remove(Node node) {
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getHistoryNode();
    }


}