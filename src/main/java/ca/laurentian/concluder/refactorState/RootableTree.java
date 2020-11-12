//This code in parts was adopted and modified to suit.
//Taken from http://prefuse.org/.
//Date 2016-08-02

package ca.laurentian.concluder.refactorState;

import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;

import java.io.Serializable;

public class RootableTree extends Tree implements Cloneable, Serializable {
    Tree toClone;

    public RootableTree() {
        super();
    }

    public RootableTree(Table nodes, Table edges, String node_key, String source_key, String target_key) {
        super(nodes, edges, node_key, source_key, target_key);
    }

    public void setNewRoot(Node root) {
        super.m_root = root.getRow();
    }

    public void setNodeKey(String node_key) {
        super.m_nkey = node_key;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            return null;
        }
    }

    public void setTreeToClone(Tree t) {
        toClone = t;
    }

    public Tree getClone() {
        return toClone;
    }
    
	/*
	public Tree readBack()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try 
		{
			oos = new ObjectOutputStream(bos);
			oos.writeObject(toClone);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			Tree copy=null;
			try 
			{
				copy = (Tree) new ObjectInputStream(bais).readObject();
			} 
			catch (ClassNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return copy;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	*/
}
