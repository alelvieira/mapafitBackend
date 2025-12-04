package com.mapadavida.mdvBackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (args == null || args.length == 0) {
            System.out.println("Usage: mvn exec:java -Dexec.mainClass=com.mapadavida.mdvBackend.util.HashGenerator -Dexec.args=\"password1 password2\"");
            return;
        }
        for (String s : args) {
            String hash = encoder.encode(s);
            System.out.println(s + " ==> " + hash);
        }
    }
}

