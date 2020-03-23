package pl.wanderers.sandbox.dropwizard;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.client.oauth1.AccessToken;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Reads and writes access tokens and secrets to the repository, encrypting and encoding them on the fly.
 */
public class AccessTokenService {

    private final AccessTokenRepository repo;
    private final byte[] dbEncryptionKey;

    public AccessTokenService(AccessTokenRepository repo, byte[] dbEncryptionKey) {
        this.repo = repo;
        this.dbEncryptionKey = dbEncryptionKey;
    }

    Optional<AccessToken> getByTwitterId(long id) {
        return repo.getById(id)
                .map(token -> new AccessToken(token.getToken(), decrypt(token.getAccessTokenSecret())));
    }

    private byte[] decrypt(String encryptedText) {
        try {
            Key aesKey = new SecretKeySpec(dbEncryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(dbEncryptionKey));
            return cipher.doFinal(Hex.decodeHex(encryptedText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    void save(long twitterId, AccessToken token) {
        repo.insert(twitterId, new AccessToken(token.getToken(), encrypt(token.getAccessTokenSecretAsByteArray())));
    }

    private String encrypt(byte[] plainText) {
        try {
            Key aesKey = new SecretKeySpec(dbEncryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(dbEncryptionKey));
            byte[] encrypted = cipher.doFinal(plainText);
            return Hex.encodeHexString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
