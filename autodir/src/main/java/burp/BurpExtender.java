package burp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class BurpExtender implements IBurpExtender,IProxyListener {

    private IBurpExtenderCallbacks callbacks;

    private Config config;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;

        try {
            config = new Config();
        } catch (Exception e) {
            throw new RuntimeException("Initial error : " + e.getMessage());
        }

        callbacks.setExtensionName("Auto Dir");
        callbacks.registerProxyListener(this);

        callbacks.printOutput("Success to extension load!");
        callbacks.printOutput("Config file path is " + config.getConfigPath());
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (messageIsRequest) {
            IRequestInfo iRequestInfo = callbacks.getHelpers().analyzeRequest(message.getMessageInfo().getHttpService(), message.getMessageInfo().getRequest());
            String path = iRequestInfo.getUrl().getPath();
            String[] split = path.substring(0, path.lastIndexOf("/")).split("/");

            if (config.getMode().equals("local")) {
                executor.submit(() -> {
                    for (String str : split) {
                        try {
                            if (str != null && !"".equals(str)) {
                                Files.write(Paths.get(config.getLocalFile()),
                                        (str.trim() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                                        StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } else {

            }
        }
    }
}
