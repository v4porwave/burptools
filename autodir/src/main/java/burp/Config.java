package burp;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Getter
@Setter
public class Config {

    private String configPath = new File(System.getProperty("java.class.path")).getParentFile().getAbsolutePath() + File.separator + "conf" + File.separator;

    private String configFileName = "config.properties";

    private String mode ;

    private String localFile ;

    private String remoteUrl ;

    private String remoteKey ;

    public Config() throws Exception {
        File dic = new File(configPath);
        if (!dic.exists()) {
            dic.mkdir();
        }
        File configFile = new File(configPath + configFileName);
        Properties properties = new Properties();;
        if (configFile.exists()) {
            properties.load(Files.newInputStream(configFile.toPath()));
        } else {
            InputStream resource = BurpExtender.class.getClassLoader().getResourceAsStream(configFileName);
            properties.load(resource);
            properties.store(Files.newOutputStream(Paths.get(configFile.getAbsolutePath()),
                            StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE),
                    "Default config");
        }
        parseConfig(properties);
    }


    private void parseConfig(Properties properties) {
        mode = properties.getProperty("dictionary.mode", "local");
        localFile = properties.getProperty("dictionary.localFile",configPath + "dictionary.txt");
        remoteUrl = properties.getProperty("dictionary.remoteUrl","");
        remoteKey = properties.getProperty("dictionary.remoteKey","");
    }
}
