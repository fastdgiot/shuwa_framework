import java.math.BigInteger;
import java.util.Stack;

/**
 *@Description: keelop算法加解密操作
 *@author: wyc
 *@date： 2020年6月18日
 */
public class KeelopCrypt {
    private static final String TARGET_STR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] chs = TARGET_STR.toCharArray();
    private static final BigInteger INTEGER0 = new BigInteger("0");
    private static int NLF[][][][][] = new int[2][2][2][2][2];
    static {
        NLF[0][0][0][0][0]=0;
        NLF[0][0][0][0][1]=1;
        NLF[0][0][0][1][0]=1;
        NLF[0][0][0][1][1]=1;
        NLF[0][0][1][0][0]=0;
        NLF[0][0][1][0][1]=1;
        NLF[0][0][1][1][0]=0;
        NLF[0][0][1][1][1]=0;
        
        NLF[0][1][0][0][0]=0;
        NLF[0][1][0][0][1]=0;
        NLF[0][1][0][1][0]=1;
        NLF[0][1][0][1][1]=0;
        NLF[0][1][1][0][0]=1;
        NLF[0][1][1][0][1]=1;
        NLF[0][1][1][1][0]=1;
        NLF[0][1][1][1][1]=0;
        
        NLF[1][0][0][0][0]=0;
        NLF[1][0][0][0][1]=0;
        NLF[1][0][0][1][0]=1;
        NLF[1][0][0][1][1]=1;
        NLF[1][0][1][0][0]=1;
        NLF[1][0][1][0][1]=0;
        NLF[1][0][1][1][0]=1;
        NLF[1][0][1][1][1]=0;
        
        NLF[1][1][0][0][0]=0;
        NLF[1][1][0][0][1]=1;
        NLF[1][1][0][1][0]=0;
        NLF[1][1][0][1][1]=1;
        NLF[1][1][1][0][0]=1;
        NLF[1][1][1][0][1]=1;
        NLF[1][1][1][1][0]=0;
        NLF[1][1][1][1][1]=0;
        
    }

    /*
     * 获取source第n个位数
     */
    private static int getBit(BigInteger source, int n) {
        long temp0 = ((long) 1 << n);
        long a = source.and(new BigInteger(Long.valueOf(temp0).toString())).longValue();
       // long temp1 = source & temp0;
        if (a != 0) {
            return 1;
        }
        return 0;
    }

    /*
     * source带进位右移
     */
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

    /*
     * source带进位左移
     */
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

    
    /**
     * 加密
     */
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

    /**
     * 解密
     */
    private static BigInteger DECRYPT(BigInteger source, BigInteger key) {
        int c;
        for (int i = 528; i > 0; i--) {
            int nlf = NLF[getBit(source, 30)][getBit(source, 25)][getBit(source, 19)][getBit(source, 8)][getBit(source,0)];
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
    
    /**
     * @Description:加密
     * @param dataHex
     * @param key
     * @return
     */
    public static String encrypt(String dataHex ,String key) {
    	StringBuffer result = new StringBuffer();
    	int size = dataHex.length()%8==0?dataHex.length()/8 : (dataHex.length()/8)+1;
    	String data = addZeroForString(dataHex, size*8, 2);
    	String hex = null;
    	for (int i = 0; i < size*8; i= i+8) {
    		hex = changeData(4, data.substring(i, i+8));
    		BigInteger encryptData = KeelopCrypt.CRYPT(new BigInteger(hex, 16), new BigInteger(changeData(4, key), 16));
    		result.append(changeData(4,numToRadix(encryptData.toString(), 16)));
		}
    	return result.toString();
    }
    
   /**
    * @Description:每个字节首尾对调
    * @param byteSize
    * @param data
    * @return
    */
    private static String changeData(int byteSize , String data) {
	   String content = addZeroForString(data, byteSize*2, 1);
	   StringBuffer result = new StringBuffer();
	   for (int i = content.length(); i >= 2; i = i - 2) {
		   result.append(content.substring(i-2, i));
	   }
	   return result.toString();
   }
    
    public static String addZeroForString(String str, int strLength, int type) {
	    int strLen = str.length();
	    StringBuffer sb = null;
	    if(type == 1){
	    	while (strLen < strLength) {
		    	sb = new StringBuffer();
		        sb.append("0").append(str);// 左补0
		        //sb.append(str).append("0");//右补0
		        str = sb.toString();
		        strLen = str.length();
		    }
	    }else{
	    	while (strLen < strLength) {
		    	sb = new StringBuffer();
//		        sb.append("0").append(str);// 左补0
		        sb.append(str).append("0");//右补0
		        str = sb.toString();
		        strLen = str.length();
		    }
	    }
	    return str;
	}
	
    /**
     * 10进制转任意进制
     */
    public static String numToRadix(String number, int radix) {
        if(radix < 0 || radix > TARGET_STR.length()){
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

    /**
     * @Description:解密
     * @param dataHex
     * @param key
     * @return
     */
   public static String decrypt(String dataHex ,String key) {
	    StringBuffer result = new StringBuffer();
	    int size = dataHex.length()%8==0?dataHex.length()/8 : (dataHex.length()/8)+1;
	   	String data = addZeroForString(dataHex, size*8, 2);
	   	String hex = null;
	   	for (int i = 0; i < size*8; i= i+8) {
	   		hex = changeData(4, data.substring(i, i+8));
	   		BigInteger encryptData = KeelopCrypt.DECRYPT(new BigInteger(hex, 16), new BigInteger(changeData(4, key), 16));
	   		result.append(changeData(4,numToRadix(encryptData.toString(), 16)));
		}
	   	return result.toString();
    }
    
    public static void main(String[] args) {
        String cmd = args[0];
		String key = args[1];
		String data = args[2]; 
		//java -classpath  D:\shuwa_pro\shuwa_zeta\priv  KeelopCrypt encrypt 8EA09EEF30F05DBF 5A9BA3F0
    	//java -classpath  D:\shuwa_pro\shuwa_zeta\priv  KeelopCrypt decrypt 8EA09EEF30F05DBF 30bccdb5
        if(cmd.equals("encrypt") == true) {
		    System.out.println(encrypt(data, key));
		} else if(cmd.equals("decrypt") == true){
			System.out.println(decrypt(data, key));
		}
    }
}