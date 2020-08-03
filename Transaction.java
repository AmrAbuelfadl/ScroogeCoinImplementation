import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class Transaction {
	public int id;
	static int i = 0;
	public String hashedPreviousTransaction;
//	public Integer hashedPreviousTransaction;
	public String userSign;
	public int transferredAmount;
	public Coin coin;
	public PublicKey sender;
	public PublicKey receiver;
	public User sen;
	public User rec;
	
	public static String computeHash(String data) {  // This hash function got it from a source online "freeformatter.com/sha256-generator"
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
	
	public Transaction(String userSign, int transferredAmount, Coin coin, PublicKey sender) { //createCoin
		this.id = i + 1;
		i = this.id;
		this.hashedPreviousTransaction = null;
		this.userSign = userSign;
		this.transferredAmount = transferredAmount;
		this.coin = coin;
		this.sender = sender;
		this.receiver = null;
		
	}
	
	public Transaction(Transaction PreviousTransaction, String userSign, int transferredAmount, Coin coin, PublicKey sender, PublicKey receiver) { //payTo
		String data = PreviousTransaction.id + PreviousTransaction.hashedPreviousTransaction + PreviousTransaction.transferredAmount + PreviousTransaction.coin + "";
		this.id = i + 1;
		i = this.id;
		this.hashedPreviousTransaction = computeHash(data);
//		this.hashedPreviousTransaction = PreviousTransaction.hashCode();

		this.userSign = userSign;
		this.transferredAmount = transferredAmount;
		this.coin = coin;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public Transaction(Transaction PreviousTransaction, String userSign, int transferredAmount, Coin coin, User sender, User receiver) { //payTo
		String data = PreviousTransaction.id + PreviousTransaction.hashedPreviousTransaction + PreviousTransaction.transferredAmount + PreviousTransaction.coin + "";
		this.id = i + 1;
		i = this.id;
		this.hashedPreviousTransaction = computeHash(data);
//		this.hashedPreviousTransaction = PreviousTransaction.hashCode();

		this.userSign = userSign;
		this.transferredAmount = transferredAmount;
		this.coin = coin;
		this.sender = sender.keyPair.getPublic();
		this.receiver = receiver.keyPair.getPublic();
		this.sen = sender;
		this.rec = receiver;
	}


}
