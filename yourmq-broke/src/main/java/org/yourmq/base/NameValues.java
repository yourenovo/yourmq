package org.yourmq.base;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NameValues {
    private final List<Map.Entry<String, String>> items = new ArrayList();

    public NameValues() {
    }

    public int size() {
        return this.items.size();
    }

    public void sort() {
        this.items.sort(Map.Entry.comparingByKey());
    }

    public void add(String name, String value) {
        this.items.add(new AbstractMap.SimpleEntry(name, value));
    }

    public Map.Entry<String, String> get(int index) {
        return (Map.Entry) this.items.get(index);
    }

    public List<Map.Entry<String, String>> getItems() {
        return this.items;
    }
}
