import java.math.BigInteger;
import java.util.Stack;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


public class Crypt {
    private static final String UTF8 = "UTF-8";
    private static final String AES = "AES";
    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final String TARGET_STR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] chs = TARGET_STR.toCharArray();
    private static final BigInteger INTEGER0 = new BigInteger("0");
    private static int NLF[][][][][] = new int[2][2][2][2][2];


    static {
        NLF[0][0][0][0][0] = 0;
        NLF[0][0][0][0][1] = 1;
        NLF[0][0][0][1][0] = 1;
        NLF[0][0][0][1][1] = 1;
        NLF[0][0][1][0][0] = 0;
        NLF[0][0][1][0][1] = 1;
        NLF[0][0][1][1][0] = 0;
        NLF[0][0][1][1][1] = 0;

        NLF[0][1][0][0][0] = 0;
        NLF[0][1][0][0][1] = 0;
        NLF[0][1][0][1][0] = 1;
        NLF[0][1][0][1][1] = 0;
        NLF[0][1][1][0][0] = 1;
        NLF[0][1][1][0][1] = 1;
        NLF[0][1][1][1][0] = 1;
        NLF[0][1][1][1][1] = 0;

        NLF[1][0][0][0][0] = 0;
        NLF[1][0][0][0][1] = 0;
        NLF[1][0][0][1][0] = 1;
        NLF[1][0][0][1][1] = 1;
        NLF[1][0][1][0][0] = 1;
        NLF[1][0][1][0][1] = 0;
        NLF[1][0][1][1][0] = 1;
        NLF[1][0][1][1][1] = 0;

        NLF[1][1][0][0][0] = 0;
        NLF[1][1][0][0][1] = 1;
        NLF[1][1][0][1][0] = 0;
        NLF[1][1][0][1][1] = 1;
        NLF[1][1][1][0][0] = 1;
        NLF[1][1][1][0][1] = 1;
        NLF[1][1][1][1][0] = 0;
        NLF[1][1][1][1][1] = 0;

    }

    private static int getBit(BigInteger source, int n) {
        long temp0 = ((long) 1 << n);
        long a = source.and(new BigInteger(Long.valueOf(temp0).toString())).longValue();
        // long temp1 = source & temp0;
        if (a != 0) {
            return 1;
        }
        return 0;
    }

    private static BigInteger RRC(BigInteger source, int c) {
        if (c != 0) {
            source = source.shiftRight(1).or(new BigInteger("80000000", 16));
            //System.out.println(source);
            // soucre = (soucre >> 1) | 0x80000000;
        } else {
            source = source.shiftRight(1).and(new BigInteger("7fffffff", 16));
            // System.out.println(source);
            // soucre = (soucre >> 1) & 0x7fffffff;
        }
        return source;
    }

    private static int base64_decode(char c) {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
    }

    private static void base64_decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();
        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;
            if (i == len)
                break;
            int tri = (base64_decode(s.charAt(i)) << 18)
                    + (base64_decode(s.charAt(i + 1)) << 12)
                    + (base64_decode(s.charAt(i + 2)) << 6)
                    + (base64_decode(s.charAt(i + 3)));
            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);
            i += 4;
        }
    }

    private static BigInteger RLC(BigInteger source, int c) {
        if (c != 0) {
            source = source.shiftLeft(1).or(new BigInteger("1"));
            // source = (source << 1) | 1;
        } else {
            source = source.shiftLeft(1).and(new BigInteger("FFFFFFFE", 16));
            //source = (source << 1) & 0xFFFFFFFE;
        }
        return source;
    }

    private static BigInteger CRYPT(BigInteger source, BigInteger key) {
        int c;
        for (int i = 0; i < 528; i++) {
            int nlf = NLF[getBit(source, 31)][getBit(source, 26)][getBit(source, 20)][getBit(source, 9)][getBit(source,
                    1)];
            int y16 = getBit(source, 16);
            int y0 = getBit(source, 0);
            int k = getBit(key, i % 64);
            int result = nlf ^ y16 ^ y0 ^ k;

            if (result != 0) {
                c = 1;
            } else {
                c = 0;
            }
            source = RRC(source, c);
        }
        return source;
    }

    private static BigInteger DECRYPT(BigInteger source, BigInteger key) {
        int c;
        for (int i = 528; i > 0; i--) {
            int nlf = NLF[getBit(source, 30)][getBit(source, 25)][getBit(source, 19)][getBit(source, 8)][getBit(source, 0)];
            int y15 = getBit(source, 15);
            int y31 = getBit(source, 31);
            int k = getBit(key, (i - 1) % 64);
            int result = nlf ^ y15 ^ y31 ^ k;
            if (result != 0) {
                c = 1;
            } else {
                c = 0;
            }
            source = RLC(source, c);
        }
        return source;
    }

    private static String changeData(int byteSize, String data) {
        String content = addZeroForString(data, byteSize * 2, 1);
        StringBuffer result = new StringBuffer();
        for (int i = content.length(); i >= 2; i = i - 2) {
            result.append(content.substring(i - 2, i));
        }
        return result.toString();
    }

    private static String addZeroForString(String str, int strLength, int type) {
        int strLen = str.length();
        StringBuffer sb = null;
        if (type == 1) {
            while (strLen < strLength) {
                sb = new StringBuffer();
                sb.append("0").append(str);
                str = sb.toString();
                strLen = str.length();
            }
        } else {
            while (strLen < strLength) {
                sb = new StringBuffer();
                sb.append(str).append("0");
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public static String base64_encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;
        while (i <= end) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append("");
            }
        }
        if (i == start + len - 2) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;
            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }
        return buf.toString();
    }

    public static String numToRadix(String number, int radix) {
        if (radix < 0 || radix > TARGET_STR.length()) {
            radix = TARGET_STR.length();
        }

        BigInteger bigNumber = new BigInteger(number);
        BigInteger bigRadix = new BigInteger(radix + "");

        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder(0);
        while (!bigNumber.equals(INTEGER0)) {
            stack.add(chs[bigNumber.remainder(bigRadix).intValue()]);
            bigNumber = bigNumber.divide(bigRadix);
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        return result.length() == 0 ? "0" : result.toString();
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    private static byte uniteBytes(byte src0, byte src1) {
        char _b0 = (char) Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (char) (_b0 << 4);
        char _b1 = (char) Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private static SecretKeySpec create128BitsKey(String key) {
        if (key == null) {
            key = "";
        }
        byte[] data = null;
        StringBuffer buffer = new StringBuffer(16);
        buffer.append(key);
        while (buffer.length() < 16) {
            buffer.append("0");
        }
        if (buffer.length() > 16) {
            buffer.setLength(16);
        }
        try {
            data = buffer.toString().getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, AES);
    }

    public static byte[] HexString2Bytes(String src) {
        int size = src.length();
        byte[] ret = new byte[size / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < size / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static String decimalToHex(int decimalSource) {
        BigInteger bi = new BigInteger(String.valueOf(decimalSource));
        return bi.toString(16);
    }

    public static String stringToAsciiHex(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int v = (int) chars[i];
            String hex = decimalToHex(v);
            if (hex.length() < 2) {
                sbu.append("0" + hex);
            } else {
                sbu.append(hex);
            }
        }
        return sbu.toString();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }



    public static String sign(String count, String addressCode, String key) {
        while (count.length() < 8) {
            count = "0" + count;
        }
        byte[] data = HexString2Bytes(count + addressCode + stringToAsciiHex(key));
        MessageDigest messageDigest;
        String encdeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(data);
            encdeStr = bytesToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encdeStr;
    }

    public static String sha256_encrypt(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }


    public static String keelop_decrypt(String dataHex, String key) {
        StringBuffer result = new StringBuffer();
        int size = dataHex.length() % 8 == 0 ? dataHex.length() / 8 : (dataHex.length() / 8) + 1;
        String data = addZeroForString(dataHex, size * 8, 2);
        String hex = null;
        for (int i = 0; i < size * 8; i = i + 8) {
            hex = changeData(4, data.substring(i, i + 8));
            BigInteger encryptData = Crypt.DECRYPT(new BigInteger(hex, 16), new BigInteger(changeData(4, key), 16));
            result.append(changeData(4, numToRadix(encryptData.toString(), 16)));
        }
        return result.toString();
    }


    public static String keelop_encrypt(String dataHex, String key) {
        StringBuffer result = new StringBuffer();
        int size = dataHex.length() % 8 == 0 ? dataHex.length() / 8 : (dataHex.length() / 8) + 1;
        String data = addZeroForString(dataHex, size * 8, 2);
        String hex = null;
        for (int i = 0; i < size * 8; i = i + 8) {
            hex = changeData(4, data.substring(i, i + 8));
            BigInteger encryptData = Crypt.CRYPT(new BigInteger(hex, 16), new BigInteger(changeData(4, key), 16));
            result.append(changeData(4, numToRadix(encryptData.toString(), 16)));
        }
        return result.toString();
    }

    public static byte[] base64_decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            base64_decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    public static String aes_encrypt(String stype, String aesKey, String ivsHex, String content) {
        byte[] sSrc = HexString2Bytes(content);
        int len = sSrc.length;
        while (len % 16 != 0) len++;
        byte[] result = new byte[len];
        for (int i = 0; i < len; ++i) {
            if (i < sSrc.length) {
                result[i] = sSrc[i];
            } else {
                result[i] = 0;
            }
        }
        SecretKeySpec skeySpec = create128BitsKey(aesKey);
        byte[] data = HexString2Bytes(ivsHex);
        IvParameterSpec iv = new IvParameterSpec(data);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(stype);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] encrypted = null;
        try {
            encrypted = cipher.doFinal(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHexString(encrypted);
    }

    private static String getArgs(String[] args, Integer idx) {
        byte[] data = HexString2Bytes(args[idx]);
        String str = "";
        try {
            str = new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


	public static String webEncrypt(String keyS, String ivSS, String content) {
		String str = "";
		try {
			byte[] key = keyS.getBytes("utf8");
			byte[] ivS = ivSS.getBytes("utf8");
			byte[] raw = key;
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
			IvParameterSpec iv = new IvParameterSpec(ivS);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(content.getBytes("utf8"));
			byte[] newB = new byte[ivS.length+encrypted.length];
			System.arraycopy(ivS,0,newB,0,ivS.length);
			System.arraycopy(encrypted,0,newB,ivS.length,encrypted.length);
			str = base64_encode(newB);
		} catch (Exception e) {
            e.printStackTrace();
        }
		return str;
    }

    public static void main(String[] args) {
        String cmd = args[0];
        String key = getArgs(args, 1);
        String result = "";
        if (cmd.equals("keelop_encrypt") == true) {
            String data = getArgs(args, 2);
            result = keelop_encrypt(data, key);
        } else if (cmd.equals("keelop_decrypt") == true) {
            String data = getArgs(args, 2);
            result = keelop_decrypt(data, key);
        } else if (cmd.equals("sha256_encrypt") == true) {
            result = sha256_encrypt(key);
        } else if (cmd.equals("aes_encrypt") == true) {
			String aesKey = getArgs(args, 2);
            String ivs = getArgs(args, 3);
            String content = getArgs(args, 4);
            result = aes_encrypt(key, aesKey, ivs, content);
        } else if (cmd.equals("sign") == true) {
            String apid = getArgs(args, 2);
            String ki = getArgs(args, 3);
            result = sign(key, apid, ki);
        } else if (cmd.equals("webEncrypt") == true) {
            String ivSS = getArgs(args, 2);
            String content = getArgs(args, 3);
            result = webEncrypt(key, ivSS, content);
        }
        System.out.println(result);
    }
}
