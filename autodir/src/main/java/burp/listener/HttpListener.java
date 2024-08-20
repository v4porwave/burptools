package burp.listener;

import burp.IExtensionHelpers;
import burp.IInterceptedProxyMessage;
import burp.IProxyListener;
import burp.IRequestInfo;
import burp.core.ConfigCore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HttpListener implements IProxyListener {
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        System.out.println(messageIsRequest);
        if (messageIsRequest) {
            byte[] requestByte = message.getMessageInfo().getRequest();
            IExtensionHelpers helpers = ConfigCore.instance.getCallbacks().getHelpers();
            IRequestInfo iRequestInfo = helpers.analyzeRequest(requestByte);
            String host ;
            String path ;
            try {
                URI uri = iRequestInfo.getUrl().toURI();
                host = uri.getHost();
                path = uri.getPath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            if (!ConfigCore.instance.getUseWhiteList() ||
                    (ConfigCore.instance.getUseWhiteList()
                            && ConfigCore.instance.getHostWhiteList().contains(host.toLowerCase(Locale.ROOT)))) {
                List<String> strings = spitPath(path);
            }
        }
    }

    private List<String> spitPath(String path) {
        String[] split = path.split("/");
        List<String> dic = new ArrayList<>();
        dic.addAll(Arrays.asList(split));
        return dic;
    }
}
