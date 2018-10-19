package com.alphawizard.hdwallet.alphahdwallet.service;


import com.alphawizard.hdwallet.alphahdwallet.data.entiry.ServiceException;
import com.alphawizard.hdwallet.alphahdwallet.data.entiry.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import org.ethereum.geth.Accounts;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Transaction;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.admin.JsonRpc2_0Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.schedulers.Schedulers;

import static org.web3j.crypto.Wallet.create;


public class GethKeystoreAccountService implements AccountKeystoreService {
    private static final int PRIVATE_KEY_RADIX = 16;
    /**
     * CPU/Memory cost parameter. Must be larger than 1, a power of 2 and less than 2^(128 * r / 8).
     */
    private static final int N = 1 << 9;
    /**
     * Parallelization parameter. Must be a positive integer less than or equal to Integer.MAX_VALUE / (128 * r * 8).
     */
    private static final int P = 1;

    private final KeyStore keyStore;

    public GethKeystoreAccountService(File keyStoreFile) {
        keyStore = new KeyStore(keyStoreFile.getAbsolutePath(), Geth.LightScryptN, Geth.LightScryptP);
    }

    public GethKeystoreAccountService(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public Single<Wallet> createAccount(String password) {
        return Single.fromCallable(() -> {
            return new Wallet(
                    keyStore.newAccount(password).getAddress().getHex().toLowerCase());
//            Wallet wallet = new Wallet(createNewAccount());
//            return  wallet;
        })
                .subscribeOn(Schedulers.io());
    }


//    http://54.219.176.229:8545/   一哥  提供的节点地址
    private static Admin admin = AdminFactory.build(new HttpService("http://54.219.176.229:8545/")) ;
//    private static Admin admin =new JsonRpc2_0Admin(new HttpService("http://54.219.176.229:8545"));
//            Admin.build(new HttpService("https://mainnet.infura.io/llyrtzQ3YhkdESt2Fzrk"));


    /**
     * web3j 创建账号
     */
    private static String createNewAccount() {
        String password = "123456789";
        String address = "";
        try {
            NewAccountIdentifier newAccountIdentifier = admin.personalNewAccount(password).send();
            address = newAccountIdentifier.getAccountId();
            System.out.println("new account address " + address);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return address;

    }

    /**
     * 使用web3  来获取账号列表
     */
    private static void getAccountList() {
        try {
            PersonalListAccounts personalListAccounts = admin.personalListAccounts().send();
            List<String> addressList;
            addressList = personalListAccounts.getAccountIds();
            System.out.println("account size " + addressList.size());
            for (String address : addressList) {
                System.out.println("account   : " +address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getAddressFromKeystore(String store){
        String addressStr ="\"address\"";
        int addressIndex = store.indexOf(addressStr);
        int crypToIndex = store.indexOf("\"crypto\"");
        String subStore = store.substring(addressIndex+addressStr.length(),crypToIndex);
        int firstIndex = subStore.indexOf("\"");
        int lastIndex = subStore.lastIndexOf("\"");
        String address = subStore.substring(firstIndex+1,lastIndex);

        return address;
    }

    /**
     * 判断 import的地址 在geth中是否已经存在了
     * @param address    eth地址
     * @param accounts      geth 管理的账户集合
     * @return
     */
    private  boolean isEnableImportKeyStore(String address, Accounts accounts){

        address = "0x"+address;

        int len = (int) accounts.size();
        for (int i = 0; i < len; i++) {
            org.ethereum.geth.Account gethAccount = null;
            try {
                gethAccount = accounts.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
//                result[i] = new Wallet(gethAccount.getAddress().getHex().toLowerCase());
            if(gethAccount.getAddress().getHex().equalsIgnoreCase(address)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Single<Wallet> importKeystore(String store, String password, String newPassword) {
        return Single.fromCallable(() -> {

            if(!isEnableImportKeyStore(getAddressFromKeystore(store),keyStore.getAccounts())){
                return  null;
            }

            org.ethereum.geth.Account account = keyStore
                    .importKey(store.getBytes(Charset.forName("UTF-8")), password, newPassword);

            return new Wallet(account.getAddress().getHex().toLowerCase());
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Wallet> importPrivateKey(String privateKey, String newPassword) {
        return Single.fromCallable(() -> {
            BigInteger key = new BigInteger(privateKey, PRIVATE_KEY_RADIX);
            ECKeyPair keypair = ECKeyPair.create(key);
            WalletFile walletFile = create(newPassword, keypair, N, P);
            if(!isEnableImportKeyStore(walletFile.getAddress(),keyStore.getAccounts())){
                return null;
            }
            return new ObjectMapper().writeValueAsString(walletFile);
        }).compose(new SingleTransformer<String, Wallet>() {
            @Override
            public SingleSource<Wallet> apply(Single<String> upstream) {
                return GethKeystoreAccountService.this.importKeystore(upstream.blockingGet(), newPassword, newPassword);
            }
        });
    }

    @Override
    public Single<String> exportAccount(Wallet wallet, String password, String newPassword) {
        return Single
                .fromCallable(() -> findAccount(wallet.address))
                .flatMap(account1 -> Single.fromCallable(()
                        -> new String(keyStore.exportKey(account1, password, newPassword))))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable deleteAccount(String address, String password) {
        return Single.fromCallable(() -> findAccount(address))
                .flatMapCompletable(account -> Completable.fromAction(
                        () -> keyStore.deleteAccount(account, password)))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<byte[]> signTransaction(Wallet signer, String signerPassword, String toAddress, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, long nonce, byte[] data, long chainId) {
        return Single.fromCallable(() -> {
            BigInt value = new BigInt(0);
            value.setString(amount.toString(), 10);

            BigInt gasPriceBI = new BigInt(0);
            gasPriceBI.setString(gasPrice.toString(), 10);

            BigInt gasLimitBI = new BigInt(0);
            gasLimitBI.setString(gasLimit.toString(), 10);

            Transaction tx = new Transaction(
                    nonce,
                    new Address(toAddress),
                    value,
                    gasLimitBI,
                    gasPriceBI,
                    data);

            BigInt chain = new BigInt(chainId); // Chain identifier of the main net
            org.ethereum.geth.Account gethAccount = findAccount(signer.address);
            keyStore.unlock(gethAccount, signerPassword);
            Transaction signed = keyStore.signTx(gethAccount, tx, chain);
            keyStore.lock(gethAccount.getAddress());

            return signed.encodeRLP();
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public boolean hasAccount(String address) {
        return keyStore.hasAddress(new Address(address));
    }

    @Override
    public Single<Wallet[]> fetchAccounts() {
        return Single.fromCallable(() -> {
//            注意这里用geth的 getAccounts  api 返回的是当前钱包里所有的账户，
//            是不是说明geth 就已经做了 账户的管理了？
            Accounts accounts = keyStore.getAccounts();
            int len = (int) accounts.size();
            Wallet[] result = new Wallet[len];

            for (int i = 0; i < len; i++) {
                org.ethereum.geth.Account gethAccount = accounts.get(i);
                result[i] = new Wallet(gethAccount.getAddress().getHex().toLowerCase());
            }
            return result;
        })
                .subscribeOn(Schedulers.io());
    }

    private org.ethereum.geth.Account findAccount(String address) throws ServiceException {
        Accounts accounts = keyStore.getAccounts();
        int len = (int) accounts.size();
        for (int i = 0; i < len; i++) {
            try {
                android.util.Log.d("ACCOUNT_FIND", "Address: " + accounts.get(i).getAddress().getHex());
                if (accounts.get(i).getAddress().getHex().equalsIgnoreCase(address)) {
                    return accounts.get(i);
                }
            } catch (Exception ex) {
                /* Quietly: interest only result, maybe next is ok. */
            }
        }
        throw new ServiceException("Wallet with address: " + address + " not found");
    }
}