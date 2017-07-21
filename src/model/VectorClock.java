package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import view.Logger;

public class VectorClock implements Serializable, Cloneable {

  private static final long serialVersionUID = -8761765948522375160L;
  private HashMap<String, Integer> vectorClock;

  public VectorClock() { this.vectorClock = new HashMap<String, Integer>(); }

  public HashMap<String, Integer> getVector() { return vectorClock; }

  public int addOneTo(String processId) {
    if (!vectorClock.containsKey(processId)) {
      vectorClock.put(processId, 0);
    }
    int value = vectorClock.get(processId) + 1;
    vectorClock.put(processId, value);
    return value;
  }

  public void set(String processId, int value) {
    vectorClock.put(processId, value);
  }

  public int get(String processId) {
    if (!vectorClock.containsKey(processId)) {
      vectorClock.put(processId, 0);
    }
    return vectorClock.get(processId);
  }

  public Set<String> getProcessIds() { return vectorClock.keySet(); }

  public static VectorClock mergeClocks(VectorClock vc1, VectorClock vc2) {
    VectorClock merge = new VectorClock();
    Iterator<String> i1 = vc1.getProcessIds().iterator();
    Iterator<String> i2;
    String id1, id2;
    int val1, val2;

    while (i1.hasNext()) {
      i2 = vc2.getProcessIds().iterator();
      id1 = i1.next();
      while (i2.hasNext()) {
        id2 = i2.next();
        if (id1.equals(id2)) {
          val1 = vc1.get(id1);
          val2 = vc2.get(id2);
          merge.set(id1, Math.max(val1, val2));
        }
      }
    }

    return merge;
  }

  public String toString() {
    String result = "[";
    Iterator<String> it = vectorClock.keySet().iterator();
    while (it.hasNext()) {
      result += vectorClock.get(it.next());
      if (it.hasNext()) {
        result += ", ";
      }
    }
    return result + "]";
  }

  public Object Clone() {
    VectorClock vectorClockClone = null;
    try {
      vectorClockClone = (VectorClock)super.clone();
      vectorClockClone.vectorClock = new HashMap<String, Integer>(vectorClock);
    } catch (CloneNotSupportedException e) {
      Logger.println("CloneNotSupportedException in VectorClock: " +
                     e.getMessage());
    }
    return vectorClockClone;
  }
}
