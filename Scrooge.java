import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.awt.List;
import java.io.FileWriter;  
import java.io.IOException;  

public class Scrooge {
	public ArrayList<Block> BlockChain;
	public ArrayList<Transaction> waitingTransactions;
	public KeyPair keyPair;
	public FileWriter myWriter;
	public ArrayList<User> users;
	public boolean transactionConfirmed;
	public ArrayList<Object> requestTransactionsToScrooge;
	public Block lastHashPointer;
//	public ArrayList<Transaction> coinsPreviousTransactions;
	
	public Scrooge() throws Exception {
		this.BlockChain = new ArrayList<Block>();
		this.waitingTransactions = new ArrayList<Transaction>();
		this.keyPair = generateKeyPair();
		this.users = new ArrayList<User>();
		this.transactionConfirmed = false;
		this.myWriter = new FileWriter("result.txt");
		this.requestTransactionsToScrooge = new ArrayList<Object>();
		generateCoinsForUsers();
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
	
	public static boolean verify(String signature, PublicKey publicKey) throws Exception { // RSA Signing, verifying and generating keypair in Java.security got it from https://niels.nu/blog/2016/java-rsa.html
	    Signature publicSignature = Signature.getInstance("SHA256withRSA");
	    publicSignature.initVerify(publicKey);
	    byte[] signatureBytes = Base64.getDecoder().decode(signature);
	    return publicSignature.verify(signatureBytes);
	}
	
	public Transaction payToByScrooge(Transaction transaction, User receivedUser) throws Exception {
		Transaction trans = new Transaction(transaction, sign(this.keyPair.getPrivate()), 1, transaction.coin, this.keyPair.getPublic(), receivedUser.keyPair.getPublic());
		return trans;
	}
	
	
	public boolean checkBlockCahinNotChanged() throws Exception {
		Block block = this.BlockChain.get(this.BlockChain.size() -1 );
		String data = "";
		for(int i = 0; i < block.transactionsList.size(); i++) {
			data += block.transactionsList.get(i);
		}
		data += block.hashedPreviousBlock;
		String finalBlockHash = Block.computeHash(data);
		if(! this.lastHashPointer.hashedPreviousBlock.equals(finalBlockHash)) {
			String b= "last block in the BlockChain has been changed by someone";
//			System.out.println(b);
			this.myWriter.write(b+"\n");
			return false;
		}
//		
		if(! verify(this.lastHashPointer.scroogeSign,this.keyPair.getPublic())) {
			String b = "last hashpointer not signed by  Scrooge";
//			System.out.println(b);
			this.myWriter.write(b+"\n");
			return false;
		}
		
		for(int i = this.BlockChain.size() - 1; i > 0; i--) {
			Block blk = this.BlockChain.get(i);
			String values = "";
			for(int b = 0; b < blk.transactionsList.size(); b++) {
				values += blk.transactionsList.get(b);
			}
			values += blk.hashedPreviousBlock;
			String finalBlkHash = Block.computeHash(values);
			if(! blk.hashedThisBlock.equals(finalBlkHash)){
				String b= " last block in the BlockChain has been changed by someone";
//				System.out.println(b);
				this.myWriter.write(b+"\n");
				return false;
			}
			
			Block blk2 = this.BlockChain.get(i-1);
			String values2 = "";
			for(int b = 0; b < blk2.transactionsList.size(); b++) {
				values2 += blk2.transactionsList.get(b);
			}
			values2 += blk2.hashedPreviousBlock;
			String finalBlkHash2 = Block.computeHash(values2);
			
			if(! blk.hashedPreviousBlock.equals(finalBlkHash2)) {
				String b= " Previous block in the BlockChain has been changed by someone";
//				System.out.println(b);
				this.myWriter.write(b+"\n");
				return false;
			}
		}
		
		return true;
	}
	
	public boolean checkCoinsOwner(Coin c, User sender) {
		for(int j = this.BlockChain.size() - 1; j > -1 ; j--) {
			Block block = this.BlockChain.get(j);
			for(int t = block.transactionsList.size() - 1; t > -1; t--) {
				Transaction trans = block.transactionsList.get(t);
				if(trans.coin.id == c.id & sender.keyPair.getPublic().equals(trans.receiver)) {
//					this.waitingTransactions.add(new Transaction(trans, senderSign, 1, c, receiver.keyPair.getPublic()));
					return true;
				}
			}
		}
		return false;
	}
	
    public ArrayList<PublicKey> removeDups(PublicKey[] arr, int n)  
    {  
        HashMap<PublicKey,Boolean> mp = new HashMap<>(); 
        ArrayList<PublicKey> senders= new ArrayList<PublicKey>();
        for (int i = 0; i < n; ++i) 
        { 
 
            if (mp.get(arr[i]) == null) {
//              System.out.print(arr[i] + " "); 
            		senders.add(arr[i]);
            	
            }
  
            mp.put(arr[i], true); 
        } 
        return senders;
        
    }
    
    public boolean checkDoubleSpending(int transId, Coin coin) {
    		for(int i = 0; i < this.waitingTransactions.size(); i++) {
    			Transaction t2 = this.waitingTransactions.get(i);
    			if(transId != t2.id & coin.id == t2.coin.id) {
    				return true;
    			}
    		}
    		return false;
    }
	
	public void checkingUserRequests() throws Exception {
		Object[] list = (Object[]) this.requestTransactionsToScrooge.get(0);
		int amountNeededToBeTransfered = (int) list[0];
		User sender = (User) list[1];
		String senderSign = (String) list[2];
		User receiver = (User) list[3];
		ArrayList<Coin> coinsToBeTransferred = (ArrayList<Coin>) list[4];
		this.requestTransactionsToScrooge = new ArrayList<Object>();
		if(amountNeededToBeTransfered <= sender.coinsBalance.size()) {
			if(verify(senderSign, sender.keyPair.getPublic())){
				ArrayList<Boolean> verifications = new ArrayList<Boolean>();
				ArrayList<Integer> coinsIDs = new ArrayList<Integer>();
				for(int i = 0; i < amountNeededToBeTransfered; i++) {
					boolean verifiedCoinsOwner = checkCoinsOwner(sender.coinsBalance.get(i), sender);
					coinsIDs.add(sender.coinsBalance.get(i).id);
					verifications.add(verifiedCoinsOwner);
				}
				boolean verified = true;
				for(int i = 0; i < verifications.size(); i++) {
					verified &= verifications.get(i);
				}
				Set<Integer> set = new HashSet<Integer>(coinsIDs);
				if(verified) { // Checking the owner of coins
					if(set.size() == coinsIDs.size()){ // Checking No duplicate coins
						for(int i = 0; i < amountNeededToBeTransfered ; i++) {
							boolean found = false;
							for(int j = this.BlockChain.size() - 1; j > -1 ; j--) {
								Block block = this.BlockChain.get(j);
								for(int t = block.transactionsList.size() - 1; t > -1; t--) {
									Transaction trans = block.transactionsList.get(t);
									if(trans.coin.id == coinsToBeTransferred.get(i).id & sender.keyPair.getPublic().equals(trans.receiver)) {
										if(sender.coinsBalance.indexOf(coinsToBeTransferred.get(i)) > -1) {
											Transaction transaction = new Transaction(trans, senderSign, 1, coinsToBeTransferred.get(i), sender, receiver);
											this.waitingTransactions.add(transaction);
											String trans1 = "Transaction with id " + transaction.id +" which is payTo by a user" + "\n" +", his public key is " + transaction.sender + "\n" +", public key of received user is " + transaction.receiver+ "\n" +", the hash of previous transaction is " + transaction.hashedPreviousTransaction + "\n" +", transferred amount is " + transaction.transferredAmount + "\n" +", the id of transferred coin is " + transaction.coin.id +"\n";
//											System.out.println(trans1+"\n");
											this.myWriter.write(trans1+"\n");
											printTransactions();
											found = true;
											break;
										}
										else {
//											System.out.println("You have spent this coin in the previous block");
										}

										
									}
								}
								if(found) {
									break;
								}
							}
							if(this.waitingTransactions.size() == 10) {
								//COMPLETE
								for(int t = 0; t < this.waitingTransactions.size(); t++ ) {
									Transaction tr = this.waitingTransactions.get(t);
									boolean doubleSpendingFound = checkDoubleSpending(tr.id, tr.coin);
									if(doubleSpendingFound) {
										String trans1 = "Transaction with id " + tr.id +" is a double spending transaction and it is removed" + "\n" +", his public key is " + tr.sender + "\n" +", public key of received user is " + tr.receiver+ "\n" +", the hash of previous transaction is " + tr.hashedPreviousTransaction + "\n" +", transferred amount is " + tr.transferredAmount + "\n" +", the id of transferred coin is " + tr.coin.id +"\n" ;
//										System.out.println(trans1+"\n");
										this.myWriter.write(trans1+"\n");
										this.waitingTransactions.remove(tr);
										t--;
									}
								}
								if(this.waitingTransactions.size() == 10) {
									if(checkBlockCahinNotChanged()) {
										for(int t = 0; t < 10; t++ ) {
											Transaction tr = this.waitingTransactions.get(t);
											tr.sen.coinsBalance.remove(tr.coin);
											tr.rec.coinsBalance.add(tr.coin);
										}
										Block blk = new Block(this.BlockChain.get(this.BlockChain.size() - 1), waitingTransactions);
										this.BlockChain.add(blk);
										String blockChainNew = "BlockChain after adding block "+blk.id ;
//										System.out.println(blockChainNew+"\n");
										this.myWriter.write(blockChainNew+"\n");
										for(int j = 0; j < this.BlockChain.size() ; j++) {
												Block block = this.BlockChain.get(j);
												String blockString = "Appended Block with id "+block.id + ", hash value of the block is " + block.hashedThisBlock + ", hash value of the previous block is " + block.hashedPreviousBlock ;  
//												System.out.println(blockString+"\n");
												this.myWriter.write(blockString+"\n");
									}
										this.lastHashPointer = new Block(this.BlockChain.get(this.BlockChain.size() - 1), sign(this.keyPair.getPrivate()));
										String lastHashP = "Last hash pointer signed by scrooge" + ", hash of previous block is " + this.lastHashPointer.hashedPreviousBlock;
//										System.out.println(lastHashP+"\n");
										this.myWriter.write(lastHashP+"\n");	
//										System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+"\n");
										this.myWriter.write("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+"\n");
										
									}
								else {
									this.myWriter.write("\n");
									String bb = "BlockChain is not valid";
//									System.out.println(bb+"\n");
									this.myWriter.write(bb+"\n");
									this.myWriter.write("\n");
									System.exit(0);
								}
								this.waitingTransactions = new ArrayList<Transaction>();
							  }

							}
							else {
								// not 10 yet
							}	
						}
					}
					else {
//						this.myWriter.write("\n");
//						String c = "User with public key " + sender.keyPair.getPublic() + "has duplicate Coins ";
//						System.out.println(c);
//						this.myWriter.write(c+"\n");
//						this.myWriter.write("\n");
					}
				}
				else {
//					this.myWriter.write("\n");
//					String c = "User with public key " + sender.keyPair.getPublic() + "not the owner of all the coins ";
////					System.out.println(c);
//					this.myWriter.write(c+"\n");
//					this.myWriter.write("\n");

				}
			}
			else {
//				this.myWriter.write("\n");
//				String c = "User with public key " + sender.keyPair.getPublic() + "not verified by scrooge ";
////				System.out.println(c);
//				this.myWriter.write(c+"\n");
//				this.myWriter.write("\n");
			}
		}
		else {
//			this.myWriter.write("\n");
//			String u = "User with public key " + sender.keyPair.getPublic() + "does not have enough coin balance to transfer coins";
////			System.out.println(u);
//			this.myWriter.write(u+"\n");
//			this.myWriter.write("\n");
		}
	}
	
	public void printTransactions() throws IOException {
		this.myWriter.write("\n");
		String b = "The Block under construction contains the following transactions: ";
//		System.out.println(b+"\n");
		this.myWriter.write(b+"\n");
		for(int tr = 0; tr < this.waitingTransactions.size(); tr++) {
			String trans1 = "Transaction with id " + this.waitingTransactions.get(tr).id;
//			System.out.println(trans1+"\n");
			this.myWriter.write(trans1+"\n");
		}
		this.myWriter.write("\n");
	}
	public void generateCoinsForUsers() throws Exception{
		int blockChainIndex = 0;
		for(int i = 1; i < 101; i++) {
			User user = new User();
			this.users.add(user);
			String UserPK = "A newly created User " + i + ", public key: " + user.keyPair.getPublic() + "\n" + "Coin Balance: " + user.coinsBalance.size() +"\n";
//			System.out.println(UserPK+"\n");
			this.myWriter.write(UserPK+"\n");
			ArrayList<Coin> userCoinsBalance = new ArrayList<Coin>();
			for(int k = 0; k < 2; k ++) {
				for(int j = 0; j < 5; j++) {
					Coin coin = new Coin();
					userCoinsBalance.add(coin);
					Transaction createCoin = new Transaction(sign(this.keyPair.getPrivate()), 1, coin, this.keyPair.getPublic());
					Transaction payTo = payToByScrooge(createCoin, user);
					this.waitingTransactions.add(createCoin);
					String trans1 = "Transaction with id " + createCoin.id +" which is createCoin by scrooge" + "\n" +", his public key is " + createCoin.sender + "\n" +", public key of received user is " + createCoin.receiver+ "\n" +", the hash of previous transaction is " + createCoin.hashedPreviousTransaction + "\n" +", transferred amount is " + createCoin.transferredAmount + "\n" +", the id of transferred coin is " + createCoin.coin.id + "\n";
//					System.out.println(trans1+"\n");
					this.myWriter.write(trans1+"\n");
					this.waitingTransactions.add(payTo);
					String trans2 = "Transaction with id " + payTo.id +" which is payTo by scrooge" + "\n" +", his public key is " + payTo.sender + "\n" +", public key of received user is " + payTo.receiver+ "\n" +", the hash of previous transaction is " + payTo.hashedPreviousTransaction + "\n" +", transferred amount is " + payTo.transferredAmount + "\n" +", the id of transferred coin is " + payTo.coin.id + "\n";
//					System.out.println(trans2+"\n");
					this.myWriter.write(trans2+"\n");
					printTransactions();
				}	
				Block currentBlock;
				if(blockChainIndex == 0) {
					currentBlock = new Block(waitingTransactions);
					this.BlockChain.add(currentBlock);
				}
				else {
					currentBlock = new Block(BlockChain.get(blockChainIndex - 1), waitingTransactions);
					this.BlockChain.add(currentBlock);
				}
				for(int j = 0; j < 5; j++) {
					user.coinsBalance.add(userCoinsBalance.get(j));
				}
				String UserCB1 = "User " + i + ", Coin Balance: " + user.coinsBalance.size();
//				System.out.println(UserCB1);	
				this.myWriter.write(UserCB1+"\n");
				userCoinsBalance = new ArrayList<Coin>();
				this.waitingTransactions = new ArrayList<Transaction>();
				String blockChainNew = "BlockChain after adding block "+currentBlock.id ;
//				System.out.println(blockChainNew);
				this.myWriter.write(blockChainNew+"\n");
				for(int j = 0; j < this.BlockChain.size() ; j++) {
						Block block = this.BlockChain.get(j);
						String blockk = "Appended Block with id "+block.id + ", hash value of the block is " + block.hashedThisBlock + ", hash value of the previous block is " + block.hashedPreviousBlock ;  
//						System.out.println(blockk);
						this.myWriter.write(blockk+"\n");
			}
				this.lastHashPointer = new Block(this.BlockChain.get(this.BlockChain.size() - 1), sign(this.keyPair.getPrivate()));
				String lastHashP = "Last hash pointer signed by scrooge" + ", hash of previous block is " + this.lastHashPointer.hashedPreviousBlock;
//				System.out.println(lastHashP);
				this.myWriter.write(lastHashP+"\n");
//				System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
				this.myWriter.write("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+"\n");
				blockChainIndex ++;
			}
		} 
		this.waitingTransactions = new ArrayList<Transaction>();
	}

}
