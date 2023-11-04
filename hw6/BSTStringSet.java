import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author
 */
public class BSTStringSet implements StringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        // FIXME: PART A
        Node curr = _root;
        Node prev;
        if (curr == null) {
            _root = new Node(s);
            return;
        }
        while (true) {
            if (s.compareTo(curr.s) > 0) {
                prev = curr;
                curr = curr.right;
                if (curr == null) {
                    if (s.compareTo(prev.s) > 0) {
                        prev.right = new Node(s);
                    } else {
                        prev.left = new Node(s);
                    }
                    return;
                }
            } else if (s.compareTo(curr.s) < 0) {
                prev = curr;
                curr = curr.left;
                if (curr == null) {
                    if (s.compareTo(prev.s) > 0) {
                        prev.right = new Node(s);
                    } else {
                        prev.left = new Node(s);
                    }
                    return;
                }
            } else {
                return;
            }
        }
    }

    @Override
    public boolean contains(String s) {
        // FIXME: PART A
        Node root = _root;
        if (root == null) {
            return false;
        }
        while (true) {
            if (s.compareTo(root.s) > 0) {
                root = root.right;
                if (root == null) {
                    return false;
                }
            } else if (s.compareTo(root.s) < 0) {
                root = root.left;
                if (root == null) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    @Override
    public List<String> asList() {
        // FIXME: PART A. MUST BE IN SORTED ORDER, ASCENDING
        List<String> bstAsList = new ArrayList<>();
        BSTIterator iterator = new BSTIterator(this._root);
        while (iterator.hasNext()) {
            bstAsList.add(iterator.next());
        }
        return bstAsList;
    }


    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    // FIXME: UNCOMMENT THE NEXT LINE FOR PART B
    // @Override
    public Iterator<String> iterator(String low, String high) {
        return null;  // FIXME: PART B (OPTIONAL)
    }


    /** Root node of the tree. */
    private Node _root;
}
