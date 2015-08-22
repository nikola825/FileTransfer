package com.nidzo.filetransfer.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {

    private static final BigInteger one = new BigInteger("1");
    private static final BigInteger default_DH_P = new BigInteger
            ("1044388881413152506679602719846529545831269060992135009022588756444338172022322690710444046669809783930111585737890362691860127079270495454517218673016928427459146001866885779762982229321192368303346235204368051010309155674155697460347176946394076535157284994895284821633700921811716738972451834979455897010306333468590751358365138782250372269117968985194322444535687415522007151638638141456178420621277822674995027990278673458629544391736919766299005511505446177668154446234882665961680796576903199116089347634947187778906528008004756692571666922964122566174582776707332452371001272163776841229318324903125740713574141005124561965913888899753461735347970011693256316751660678950830027510255804846105583465055446615090444309583050775808509297040039680057435342253926566240898195863631588888936364129920059308455669454034010391478238784189888594672336242763795138176353222845524644040094258962433613354036104643881925238489224010194193088911666165584229424668165441688927790460608264864204237717002054744337988941974661214699689706521543006262604535890998125752275942608772174376107314217749233048217904944409836238235772306749874396760463376480215133461333478395682746608242585133953883882226786118030184028136755970045385534758453247");
    private static final BigInteger default_DH_G = new BigInteger("2");

    public static String generatePrivate() {
        BigInteger key = new BigInteger("0");
        while(calculatePublic(key).equals(one))
        {
            byte[] numberContents = new byte[64];
            SecureRandom randomGenerator = new SecureRandom();
            randomGenerator.nextBytes(numberContents);
            key =  NumberConversion.bytesToBigInteger(numberContents);
        }
        return NumberConversion.bigIntegerToString(key);
    }

    private static BigInteger calculatePublic(BigInteger privateKey)
    {
        return default_DH_G.modPow(privateKey, default_DH_P);
    }
    public static String calculatePublic(String privateKey) {
        return NumberConversion.bigIntegerToString(calculatePublic(NumberConversion.stringToBigInteger(privateKey)));
    }

    public static String calculateExchangeResult(String privateKey, String publicKey) {
        BigInteger privateKeyNumber = NumberConversion.stringToBigInteger(privateKey);
        BigInteger publicKeyNumber = NumberConversion.stringToBigInteger(publicKey);
        BigInteger calculated = publicKeyNumber.modPow(privateKeyNumber, default_DH_P);
        return NumberConversion.bigIntegerToString(calculated);
    }

    public static String calculateAESKey(String privateKey, String publicKey) {
        String result = calculateExchangeResult(privateKey, publicKey);
        byte[] resultBytes = Base64.Decode(result);
        int[] resultNumbers = new int[resultBytes.length];
        for (int i = 0; i < resultBytes.length; i++) {
            if (resultBytes[i] < 0) {
                resultNumbers[i] = resultBytes[i] + 256;
            }
            else {
                resultNumbers[i] = resultBytes[i] + 256;
            }
        }
        int[] calculatedKey = new int[16];
        for (int i = 0; i < resultNumbers.length; i++) {
            calculatedKey[i % 16] ^= resultNumbers[i];
        }
        byte[] calculatedKeyBytes = new byte[calculatedKey.length];
        for (int i = 0; i < calculatedKey.length; i++) {
            if (calculatedKey[i] >= 128) {
                calculatedKeyBytes[i] = (byte) (calculatedKey[i] - 256);
            }
            else {
                calculatedKeyBytes[i] = (byte) (calculatedKey[i]);
            }
        }
        return Base64.Encode(calculatedKeyBytes);
    }


}