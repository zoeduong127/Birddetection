package security;


import io.jsonwebtoken.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import static java.lang.String.valueOf;

public class TokenManager {

    private static SecretKey tokenKey;

    static {
        try {
            tokenKey = loadKeyFromFile(new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\security\\tokenKey.key");
        } catch (IOException ignored) {
            try {
//            tokenKey = loadKeyFromFile(new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2\\security\\tokenKey.key");
                tokenKey = loadKeyFromFile(ServletContextHolder.getServletContext().getRealPath("/security/tokenKey.key"));
//            System.out.println(new File(context.getRealPath("/security/tokenKey.key")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Private constructor to prevent instantiation
    private TokenManager() {
    }

    private static SecretKey loadKeyFromFile(String filePath) throws IOException {


        String realPath =  filePath;
        byte[] keyBytes = Files.readAllBytes(Paths.get(realPath));
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public static Claims decodeTokens(String token) {
        try {
            // Create a JwtParser instance with the desired settings
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenKey).build();
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Authentication error (token expired): " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return null;
        }

    }

    public static String generateToken(String username, int accountID, Timestamp expiration) {
        // Set the token expiration time
        Date expirationDate = new Date(expiration.getTime());
        String token = Jwts.builder()
                .setSubject(username)
                .claim("account_id", accountID)
                .setExpiration(expirationDate)
                .signWith(tokenKey)
                .compact();

        return token;
    }
}

