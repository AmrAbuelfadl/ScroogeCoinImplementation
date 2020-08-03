import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Coin {
	public int id;
	static int i = 0;
	
	public Coin() {
		this.id = i + 1;
		i = this.id;
	}
    
//	public static void main(String[] args) {
//		ArrayList<Integer> coinsIDs = new ArrayList<Integer>();
//		coinsIDs.add(1);
//		coinsIDs.add(2);
//		coinsIDs.add(3);
//		Set<Integer> set = new HashSet<Integer>(coinsIDs);
//
//		if(set.size() == coinsIDs.size()){
//		    System.out.println("No");
//		}
//		else {
//			System.out.println("Yes");
//		}
//	} 
}
