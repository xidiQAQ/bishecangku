import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("密码: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("验证: " + encoder.matches(password, hash));
    }
}
