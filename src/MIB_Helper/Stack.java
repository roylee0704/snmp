package MIB_Helper;
/**
 *
 * @author roylee
 */
public interface Stack<T>{
    public T pop();
    public void push(T nodeID);
    public boolean isEmpty();
    public boolean clear();
    public boolean COMMIT();
}
