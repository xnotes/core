/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

/**
 *
 * @author sopheap
 */
public final class DatatypeConverter {

	public static final byte[] parseBase64Binary(String base64EncodedString) throws IllegalArgumentException {
		return javax.xml.bind.DatatypeConverter.parseBase64Binary(base64EncodedString);
	}

	public static final byte[] parseBase64URLBinary(String base64URLEncodedString) throws IllegalArgumentException {
		return parseBase64Binary(base64URLEncodedString.replace("-", "+").replace("_", "/"));
	}

	public static final byte[] parseBase64MIMETypeBinary(String base64EncodedString) throws IllegalArgumentException {
		base64EncodedString = base64EncodedString.trim();
		if (base64EncodedString.startsWith("data:")) {
			int i = base64EncodedString.indexOf(";");
			if (i > -1) {
				String mimeType = base64EncodedString.substring(5, i).trim();
				base64EncodedString = base64EncodedString.substring(i + 1).trim();
				if (base64EncodedString.startsWith("base64,")) {
					byte[] bytes = parseBase64Binary(base64EncodedString.substring(7));
					if (mimeType.equals(FileUtil.detectMIMEType(bytes))) {
						return bytes;
					}
				}
			}
		}
		throw new IllegalArgumentException("Invalid Base64MimeType String.");
	}

	public static final String printBase64Binary(byte[] bytes) throws IllegalArgumentException {
		return javax.xml.bind.DatatypeConverter.printBase64Binary(bytes);
	}

	public static final String printBase64URLBinary(byte[] bytes) throws IllegalArgumentException {
		return printBase64Binary(bytes).replace("+", "-").replace("/", "_");
	}

	public static final String printBase64MIMETypeBinary(String filePath) throws IllegalArgumentException, IOException {
		return printBase64MIMETypeBinary(Files.readAllBytes((new File(filePath)).toPath()));
	}

	public static final String printBase64MIMETypeBinary(byte[] bytes) throws IllegalArgumentException, IOException {
		return printBase64MIMETypeBinary(FileUtil.detectMIMEType(bytes), bytes);
	}

	public static final String printBase64MIMETypeBinary(String mimeType, byte[] bytes) throws IllegalArgumentException {
		if (mimeType != null) {
			return "data:" + mimeType + ";base64," + printBase64Binary(bytes);
		} else {
			return printBase64Binary(bytes);
		}
	}

	public static final String printHexBinary(byte[] bytes) {
		return javax.xml.bind.DatatypeConverter.printHexBinary(bytes).toLowerCase();
	}

	public static final String printBase58ToHexBinary(String base58String) {
		return printHexBinary(parseBase58Binary(base58String));
	}

	public static final byte[] parseHexBinary(String hexString) {
		hexString = hexString.trim();
		if (hexString.length() % 2 != 0) {
			hexString = "0" + hexString;
		}
		return javax.xml.bind.DatatypeConverter.parseHexBinary(hexString);
	}

	private static final String B58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
	private static final BigInteger B58_BASE = BigInteger.valueOf(58);

	public static String printBase58Binary(byte[] bytes) throws IllegalArgumentException {
		// TODO: This could be a lot more efficient.
		BigInteger bi = new BigInteger(1, bytes);
		StringBuilder s = new StringBuilder();
		while (bi.compareTo(B58_BASE) >= 0) {
			BigInteger mod = bi.mod(B58_BASE);
			s.insert(0, B58_ALPHABET.charAt(mod.intValue()));
			bi = bi.subtract(mod).divide(B58_BASE);
		}
		s.insert(0, B58_ALPHABET.charAt(bi.intValue()));
		// Convert leading zeros too.
		for (byte anInput : bytes) {
			if (anInput == 0) {
				s.insert(0, B58_ALPHABET.charAt(0));
			} else {
				break;
			}
		}
		return s.toString();
	}

	public static byte[] parseBase58Binary(String base58EncodedString) throws IllegalArgumentException {
		BigInteger bi = BigInteger.valueOf(0);
		// Work backwards through the string.
		for (int i = base58EncodedString.length() - 1; i >= 0; i--) {
			int alphaIndex = B58_ALPHABET.indexOf(base58EncodedString.charAt(i));
			if (alphaIndex == -1) {
				throw new IllegalArgumentException("Illegal character " + base58EncodedString.charAt(i) + " at " + i);
			}
			bi = bi.add(BigInteger.valueOf(alphaIndex).multiply(B58_BASE.pow(base58EncodedString.length() - 1 - i)));
		}
		byte[] bytes = bi.toByteArray();
		// We may have got one more byte than we wanted, if the high bit of the next-to-last byte was not zero. This
		// is because BigIntegers are represented with twos-compliment notation, thus if the high bit of the last
		// byte happens to be 1 another 8 zero bits will be added to ensure the number parses as positive. Detect
		// that case here and chop it off.
		boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
		// Count the leading zeros, if any.
		int leadingZeros = 0;
		for (int i = 0; base58EncodedString.charAt(i) == B58_ALPHABET.charAt(0); i++) {
			leadingZeros++;
		}
		// Now cut/pad correctly. Java 6 has a convenience for this, but Android can't use it.
		byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
		System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
		return tmp;
	}

}
