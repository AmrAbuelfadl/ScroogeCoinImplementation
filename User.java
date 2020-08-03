import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;

public class User{
	public KeyPair keyPair;
	public ArrayList<Coin> coinsBalance;
		
	public User() throws Exception {
		this.keyPair = generateKeyPair();
		this.coinsBalance = new ArrayList<Coin>();
	}
	
	public static KeyPair generateKeyPair() throws Exception { // RSA Signing, verifying and generating keypair in Java.security got it from https://niels.nu/blog/2016/java-rsa.html
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    return pair;
	}
	
	public static String sign(PrivateKey privateKey) throws Exception { // RSA Signing, verifying and generating keypair in Java.security got it from https://niels.nu/blog/2016/java-rsa.html
	    Signature privateSignature = Signature.getInstance("SHA256withRSA");
	    privateSignature.initSign(privateKey);
	    byte[] signature = privateSignature.sign();
	    return Base64.getEncoder().encodeToString(signature);
	}
	
	public Object [] requestTransactionsToScrooge(int coinsRequestedToBeDelivered, User receiver) throws Exception {
		Object [] list = new Object[5];
		ArrayList<Coin> coins = new ArrayList<Coin>();
		if(coinsRequestedToBeDelivered <= this.coinsBalance.size()) {
			for(int i = 0; i < coinsRequestedToBeDelivered; i++) {
				int random = (int)(Math.random()*(this.coinsBalance.size()));
				coins.add(this.coinsBalance.get(random));
			}
		}
		list[0] = coinsRequestedToBeDelivered;
		list[1] = this;
		list[2] = sign(this.keyPair.getPrivate());
		list[3] = receiver;
		list[4] = coins;
		return list;	
	}
}
