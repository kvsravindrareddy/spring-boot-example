package com.github.toastshaman.springboot;

import org.apache.commons.cli.*;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class Encryptor {

    // run with: -s password foobar
    public static void main(String[] args) {
        final Options options = new Options();
        options.addOption(Option.builder("s").hasArg().required().desc("the master secret").build());

        final CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine line = parser.parse(options, args);

            final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            final SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(line.getOptionValue('s'));
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setStringOutputType("base64");
            encryptor.setConfig(config);

            System.out.println(encryptor.encrypt(line.getArgList().get(0)));
        } catch (ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        }
    }
}
