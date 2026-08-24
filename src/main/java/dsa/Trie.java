package dsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A prefix tree over strings.
 *
 * <p>Children hang off a {@link HashMap} per node rather than a fixed 26-slot
 * array, so the structure is not limited to lowercase ASCII and a sparse node
 * costs one small map instead of 26 null references.
 *
 * <p>Not thread-safe. Concurrent {@code insert} calls will corrupt the tree.
 */
public class Trie {

    /** One node; {@code terminal} marks the end of an inserted word. */
    private static final class Node {
        final Map<Character, Node> children = new HashMap<>(4);
        boolean terminal;
    }

    private final Node root = new Node();
    private int size;

    /**
     * Adds a word.
     *
     * @return true if the word was not already present
     * @throws IllegalArgumentException if {@code word} is null or empty
     */
    public boolean insert(String word) {
        Node node = descendCreating(requireWord(word));
        if (node.terminal) {
            return false;
        }
        node.terminal = true;
        size++;
        return true;
    }

    /** True if the exact word was inserted. A bare prefix does not count. */
    public boolean contains(String word) {
        Node node = descend(requireWord(word));
        return node != null && node.terminal;
    }

    /** True if any inserted word begins with this prefix. */
    public boolean startsWith(String prefix) {
        return descend(requireWord(prefix)) != null;
    }

    /** Every inserted word beginning with {@code prefix}, in no defined order. */
    public List<String> keysWithPrefix(String prefix) {
        List<String> found = new ArrayList<>();
        Node start = descend(requireWord(prefix));
        if (start != null) {
            collect(start, new StringBuilder(prefix), found);
        }
        return found;
    }

    /** Number of distinct words held. */
    public int size() {
        return size;
    }

    /** Walks to the node for {@code word}, or null if the path does not exist. */
    private Node descend(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            node = node.children.get(word.charAt(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /** Walks to the node for {@code word}, creating any missing links. */
    private Node descendCreating(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            node = node.children.computeIfAbsent(word.charAt(i), unused -> new Node());
        }
        return node;
    }

    /** Depth-first walk collecting every terminal beneath {@code node}. */
    private void collect(Node node, StringBuilder path, List<String> out) {
        if (node.terminal) {
            out.add(path.toString());
        }
        node.children.forEach((ch, child) -> {
            path.append(ch);
            collect(child, path, out);
            path.deleteCharAt(path.length() - 1);
        });
    }

    private static String requireWord(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("word must be non-null and non-empty");
        }
        return word;
    }
}
