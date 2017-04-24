package app.algorithm;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import org.apache.commons.lang.RandomStringUtils;
import com.auth0.jwt.algorithms.Algorithm;

import app.helpers.ConsoleOut;

public class AlgorithmLinker {

	public static final app.algorithm.AlgorithmWrapper none = new app.algorithm.AlgorithmWrapper("none",
			AlgorithmType.none);
	public static final app.algorithm.AlgorithmWrapper HS256 = new app.algorithm.AlgorithmWrapper("HS256",
			AlgorithmType.symmetric);
	public static final app.algorithm.AlgorithmWrapper HS384 = new app.algorithm.AlgorithmWrapper("HS384",
			AlgorithmType.symmetric);
	public static final app.algorithm.AlgorithmWrapper HS512 = new app.algorithm.AlgorithmWrapper("HS512",
			AlgorithmType.symmetric);
	public static final app.algorithm.AlgorithmWrapper RS256 = new app.algorithm.AlgorithmWrapper("RS256",
			AlgorithmType.asymmetric);
	public static final app.algorithm.AlgorithmWrapper RS384 = new app.algorithm.AlgorithmWrapper("RS384",
			AlgorithmType.asymmetric);
	public static final app.algorithm.AlgorithmWrapper RS512 = new app.algorithm.AlgorithmWrapper("RS512",
			AlgorithmType.asymmetric);
	public static final app.algorithm.AlgorithmWrapper ES256 = new app.algorithm.AlgorithmWrapper("ES256",
			AlgorithmType.asymmetric);
	public static final app.algorithm.AlgorithmWrapper ES384 = new app.algorithm.AlgorithmWrapper("ES384",
			AlgorithmType.asymmetric);
	public static final app.algorithm.AlgorithmWrapper ES512 = new app.algorithm.AlgorithmWrapper("ES512",
			AlgorithmType.asymmetric);

	private static final app.algorithm.AlgorithmWrapper[] supportedAlgorithms = { 
			none, HS256, HS384, HS512, RS256, RS384, RS512, ES256, ES384, ES512 };


	private static final AlgorithmWrapper[] supportedPSKAlgorithms = {
		 HS256, HS384, HS512
	};

	private static final AlgorithmWrapper[] supportedRSAAlgorithms = {
		 RS256, RS384, RS512
	};

	private static final AlgorithmWrapper[] supportedECAlgorithms = {
		 ES256, ES384, ES512
	};

	private static PublicKey generatePublicKeyFromString(String key, String algorithm) {
		PublicKey publicKey = null;
		if(key.length()>1){
			key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
					.replaceAll("\\s+", "").replaceAll("\\r+", "").replaceAll("\\n+", "");
			byte[] keyByteArray = Base64.getDecoder().decode(key);
			try {
				KeyFactory kf = KeyFactory.getInstance(algorithm);
				EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByteArray);
				publicKey = kf.generatePublic(keySpec);
			} catch (Exception e) {
				ConsoleOut.output(e.getMessage());
			}
		}
		return publicKey;
	}

	/**
	 * @param algo
	 * @param key - either the secret or the private key
	 * @return the algorithm element from the library, if nothing matches the
	 *         none algorithm element is returned
	 * @throws IllegalArgumentException
	 * @throws UnsupportedEncodingException
	 */
	public static Algorithm getAlgorithm(String algo, String key)
			throws IllegalArgumentException, UnsupportedEncodingException {
		if (algo.equals(HS256.getAlgorithm())) {
			return Algorithm.HMAC256(key);
		}
		if (algo.equals(HS384.getAlgorithm())) {
			return Algorithm.HMAC384(key);
		}
		if (algo.equals(HS512.getAlgorithm())) {
			return Algorithm.HMAC512(key);
		}
		if (algo.equals(ES256.getAlgorithm())) {
			return Algorithm.ECDSA256((ECKey) generatePublicKeyFromString(key, "EC"));
		}
		if (algo.equals(ES384.getAlgorithm())) {
			return Algorithm.ECDSA384((ECKey) generatePublicKeyFromString(key, "EC"));
		}
		if (algo.equals(ES512.getAlgorithm())) {
			return Algorithm.ECDSA512((ECKey) generatePublicKeyFromString(key, "EC"));
		}
		if (algo.equals(RS256.getAlgorithm())) {
			return Algorithm.RSA256((RSAKey) generatePublicKeyFromString(key, "RSA"));
		}
		if (algo.equals(RS384.getAlgorithm())) {
			return Algorithm.RSA384((RSAKey) generatePublicKeyFromString(key, "RSA"));
		}
		if (algo.equals(RS512.getAlgorithm())) {
			return Algorithm.RSA512((RSAKey) generatePublicKeyFromString(key, "RSA"));
		}

		return Algorithm.none();
	}

	public static boolean isPresharedKeyAlgorithm(String algorithm) {
		return Arrays.asList(supportedPSKAlgorithms).contains(algorithm);
	}

	public static boolean isRSAKeyAlgorithm(String algorithm) {
		return Arrays.asList(supportedRSAAlgorithms).contains(algorithm);
	}

	public static boolean isECKeyAlgorithm(String algorithm) {
		return Arrays.asList(supportedECAlgorithms).contains(algorithm);
	}

	public static String getRandomKey(String algorithm){
		if(isPresharedKeyAlgorithm(algorithm) ) {
			return RandomStringUtils.randomAlphanumeric(6);
		}

		if(isRSAKeyAlgorithm(algorithm)) {
			try {
				KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
				return Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()).toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();

			}
		}

		if (isECKeyAlgorithm(algorithm)) {
			try {
				KeyPair keyPair = KeyPairGenerator.getInstance("EC").generateKeyPair();
				return Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()).toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * @return gets the type (asymmetric, symmetric, none) of the
	 *         provided @param algo
	 */
	public static String getTypeOf(String algorithm) {
		for (app.algorithm.AlgorithmWrapper supportedAlgorithm : supportedAlgorithms) {
			if (algorithm.equals(supportedAlgorithm.getAlgorithm())) {
				return supportedAlgorithm.getType();
			}
		}
		return AlgorithmType.none;
	}

	public static app.algorithm.AlgorithmWrapper[] getSupportedAlgorithms() {
		return supportedAlgorithms;
	}
}
