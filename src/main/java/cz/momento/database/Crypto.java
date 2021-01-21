package cz.momento.database;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    /**
     * Method that takes parameters and using the key encodes clear text into bytes
     * @param strClearText String of clear text
     * @param strKey key in which is the text encoded
     * @return String of encrypted bytes
     * @throws Exception
     */
    public String encrypt(String strClearText, String strKey) throws Exception {
        String strData = "";
        byte[] encrypted;

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            encrypted = cipher.doFinal(strClearText.getBytes());


        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        for (byte b : encrypted) {
            strData += (String.valueOf(b) + ",");
        }
        return strData;
    }

    /**
     * Method takes encrypted data and key to decode into plain text
     * @param bytesEncrypted String of encrytped data
     * @param strKey String that is used as cryping key
     * @return plain text String of decrypted text
     * @throws Exception
     */
    public String decrypt(String bytesEncrypted, String strKey) throws Exception {
        String strData = "";
        byte[] bytes;
        bytes = getBytesFromString(bytesEncrypted);
        try {
            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(bytes);
            strData = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }

    /**
     * Method that returns hashed type of word
     * @param word plain text of word what is going to be hashed
     * @return hashed word
     */
    public String hash(String word) {
        try {
            return toHexString(getSHA(word));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    /**
     * method that splits crypted String into separate bytes spliting by regex
     * @param bytesEncrypted encrypted String
     * @return array of separate bytes of encrypted String
     */
    private static byte[] getBytesFromString(String bytesEncrypted) {
        String strBytes[] = bytesEncrypted.split("\\,");
        byte[] returnByte = new byte[strBytes.length];
        for (int i = 0; i < returnByte.length; i++) {
            returnByte[i] = Byte.parseByte(strBytes[i]);
        }
        return returnByte;
    }

    /**
     * Mehtod creates standard instance of hashing SHA-256 and calculates the hash that is returned in bytes
     * @param input word to be hashed
     * @return array of byte hahsed
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Method takes array if hashed bytes and puts them into hexadecimal representation in String
     * @param hash array of hashed bytes
     * @return kinda readable representation of hashed text
     */
    private static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}