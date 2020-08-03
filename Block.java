import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class Block {
	public int id;
	static int i = 0;
	public ArrayList<Transaction> transactionsList;
	public String hashedPreviousBlock;
	public String hashedThisBlock;
//	public Integer hashedPreviousBlock;
//	public Integer hashedThisBlock;
	public String scroogeSign;
	
	public static String computeHash(String data) { // This hash function got it from a source online "freeformatter.com/sha256-generator"
		String dataToHash = data;
		MessageDigest digest;
		String encoded = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encoded;
	}
	
	public Block(Block previousBlock, ArrayList<Transaction> transactionsList) {
		this.id = i + 1;
		i = this.id;
		this.transactionsList = transactionsList;
		this.hashedPreviousBlock = previousBlock.hashedThisBlock;
		String data = "";
		for(int i = 0; i < this.transactionsList.size(); i++) {
			data += this.transactionsList.get(i);
		}
		data += previousBlock.hashedThisBlock;
		this.hashedThisBlock = computeHash(data);
//		this.hashedThisBlock = this.hashCode();

	}
	
	public Block(ArrayList<Transaction> transactionsList) {
		this.id = i + 1;
		i = this.id;
		this.transactionsList = transactionsList;
		this.hashedPreviousBlock = null;
		String data = "";
		for(int i = 0; i < this.transactionsList.size(); i++) {
			data += this.transactionsList.get(i);
		}
		data += this.hashedPreviousBlock;
		this.hashedThisBlock = computeHash(data);
//		this.hashedThisBlock = this.hashCode();
	}
	
	public Block(Block previousBlock, String scroogeSign) {
		this.hashedPreviousBlock = previousBlock.hashedThisBlock;
		this.scroogeSign = scroogeSign;
	}
	


}
