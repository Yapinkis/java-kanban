package service;

import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    LinkedHashMap<Integer, Node> linkedMap;
    Node head;
    Node tail;//Я же правильно понял, что linkedMap, head и tail мы реализуем в классе InMemoryHistoryManager
    //А в классе Node оставляем непосредственно узлы связи Node?

    public InMemoryHistoryManager() {
        this.linkedMap = new LinkedHashMap<>();
        this.head = null;
        this.tail = null;
    }

    public LinkedHashMap<Integer,Node> getLinkedMap(InMemoryHistoryManager map) {
        return map.linkedMap;
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
        linkedMap.put(task.getId(), createNode);
    }
    //Правильно ли я понял из ТЗ, что теперь переопределённые методы интерфейса HistoryManager, теперь выглядят так.
    //Просто получается, что вся история просмотров теперь у нас хранится в мапе и имеет устройство двусвязного списка
    //С инкапсулированными методами. Зачем наличие других методов add, remove, getHistory просто не понятно...

    @Override
    public void remove(Node node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head.nextNode(head);
            head.prevNode(null);
        } else if (node == tail) {
            tail.prevNode(tail);
            tail.nextNode(null);
        } else {
            node.getPrev().nextNode(node.getNext());
            node.getNext().prevNode(node.getPrev());
        }
        linkedMap.remove(node.getTask().getId());
    }

    @Override
    public void add(Task task) {
        if (linkedMap.containsKey(task.getId())) {
            remove(linkedMap.get(task.getId()));
        }
        linkLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        for (Map.Entry<Integer,Node> entry : linkedMap.entrySet()) {
            history.add(entry.getValue().getTask());
        }
        return history;
    }
}