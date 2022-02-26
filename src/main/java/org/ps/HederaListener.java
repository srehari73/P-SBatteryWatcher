package org.ps;

import com.hedera.hashgraph.sdk.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class HederaListener {
    public static final String MY_ACCOUNT_ID = System.getenv("MY_ACCOUNT_ID");
    public static final String MY_PRIVATE_KEY = System.getenv("MY_PRIVATE_KEY");


    public static void main(String[] args) throws PrecheckStatusException, TimeoutException, ReceiptStatusException, InterruptedException {
        AccountId myAccountId = AccountId.fromString(MY_ACCOUNT_ID);
        PrivateKey myPrivateKey = PrivateKey.fromString(MY_PRIVATE_KEY);
        //Create your Hedera testnet client
        Client client = Client.forTestnet();
        client.setOperator(myAccountId, myPrivateKey);

        // Generate a new key pair
        PrivateKey newAccountPrivateKey = PrivateKey.generate();
        PublicKey newAccountPublicKey = newAccountPrivateKey.getPublicKey();

        //Create new account and assign the public key
        TransactionResponse newAccount = new AccountCreateTransaction()
                .setKey(newAccountPublicKey)
                .setInitialBalance(Hbar.fromTinybars(1000))
                .execute(client);

        // Get the new account ID
        AccountId newAccountId = newAccount.getReceipt(client).accountId;

        //Log the account ID
        System.out.println("The new account ID is: " +newAccountId);

        //Check the new account's balance
        AccountBalance accountBalance = new AccountBalanceQuery()
                .setAccountId(newAccountId)
                .execute(client);

        System.out.println("The new account balance is: " +accountBalance.hbars);

        TopicId tid = new TopicId(0,0,30808279);
        new TopicMessageQuery()
                .setTopicId(tid)
                .subscribe(client, resp -> {
                    String msg = new String(resp.contents, StandardCharsets.UTF_8);
                    System.out.println(resp.consensusTimestamp + " recieved topic message: "+msg);
                });

        boolean flag = false;
        Scanner sc = new Scanner(System.in);
        String input;
        while(!flag) {
            input = sc.next();
            if(Objects.equals(input, "q") || Objects.equals(input, "Q")){
                flag = true;
            }
        }

    }
}