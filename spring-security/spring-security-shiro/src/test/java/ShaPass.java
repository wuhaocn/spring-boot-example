import org.apache.shiro.crypto.hash.Sha1Hash;

/**
 * 生成用户名密码
 *
 */
public class ShaPass {
    public static void main(String[] args) {
       printUserAndPass("test");
    }

    public static void  printUserAndPass(String user){
        String hash = new Sha1Hash(user, user).toHex();
        System.out.println("user: " + user);
        System.out.println("pass: " + hash);
    }
}
