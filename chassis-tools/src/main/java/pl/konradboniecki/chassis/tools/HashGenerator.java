package pl.konradboniecki.chassis.tools;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

import static org.springframework.util.StringUtils.isEmpty;

@Data
@Component
public class HashGenerator {
    private String algorithm;
    private String charsetName;

    public HashGenerator() {
        setAlgorithm("SHA-256");
        setCharsetName("UTF-8");
    }

    public String hashPassword(String password) {
        if (isEmpty(password))
            throw new IllegalArgumentException("Password should not be empty");
        try{
            return hashString(password);
        } catch (Exception e){
            throw new HashGenerationException("Hashing password failed. Probably invalid charset or Algorithm is set", e);
        }
    }

    private String hashString(String string) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(getAlgorithm());
        byte[] hashArray = digest.digest(string.getBytes(getCharsetName()));
        return DatatypeConverter.printHexBinary(hashArray);
    }
}
