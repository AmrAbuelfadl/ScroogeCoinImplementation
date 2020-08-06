# ScroogeCoinImplementation
Designed a cryptocurrency similar to ScroogeCoin using Java.

# Main Idea
In this project, I designed a cryptocurrency similar to ScroogeCoin. A network of 100 users will simulate the transaction processes. Initially each user will have 10 ScroogCoins. As long as the system is running, a random transaction with random amount (within the range of amount the user has) will be created from User A to User B. The transaction is signed by the private-key of the sender. Scrooge get notified by every transaction. Scrooge verifies the signature before accumulating the transaction. Once Scrooge accumulates 10 transaction, he can form a block and attach it to the blockchain.

# How to use
Network class is responsible for running the simulation and to terminate the code press the space bar.
