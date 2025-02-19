package burp;

import burp.config.Settings;
import burp.config.Website;
import burp.ui.SmartPanel;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BurpExtender implements IBurpExtender,IProxyListener {

    private IBurpExtenderCallbacks callbacks;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    //==================================IBurpExtender=======================================
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        callbacks.setExtensionName("Smart Proxy v1.0");
        callbacks.registerProxyListener(this);

        callbacks.addSuiteTab(new ITab() {
            @Override
            public String getTabCaption() {
                return "Smart Proxy";
            }

            @Override
            public Component getUiComponent() {
                return new SmartPanel();
            }
        });
        callbacks.printOutput("Success to extension load!");
        callbacks.printOutput("You will get more smart proxy when test or collect asset.");
    }

    //================================IProxyListener===================================
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (!messageIsRequest) {
            byte[] requestByte = message.getMessageInfo().getRequest();
            byte[] responseByte = message.getMessageInfo().getResponse();
            IRequestInfo iRequestInfo = callbacks.getHelpers().analyzeRequest(requestByte);
            IResponseInfo iResponseInfo = callbacks.getHelpers().analyzeResponse(responseByte);

            String body = new String(Arrays.copyOfRange(requestByte, iRequestInfo.getBodyOffset(), requestByte.length),
                    StandardCharsets.UTF_8);
            if (iResponseInfo.getStatusCode() == 200) {
                Website website = matchRequest(iRequestInfo.getHeaders());
                if (website != null) {
                    switch (website) {
                        case AQC: parserAQCBody(new String(responseByte).substring(iResponseInfo.getBodyOffset())); break;
                        case ICP: parserICPRequest(iRequestInfo.getHeaders(), body); break;
                        default: break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ProcessBuilder().command("/bin/sh", "-c", "echo hello").start().getOutputStream();
        new ProcessBuilder().command();
    }

    private Website matchRequest(List<String> headers) {
        for (Website website : Website.values()) {
            if (website.getHostPattern().matcher(headers.get(1)).find()) {
                if (website.getUriPattern().matcher(headers.get(0)).find()) {
                    return website;
                }
            }
        }
        return null;
    }

    private void parserAQCBody(String body) {
        JSONObject o = JSONObject.parse(body).getJSONObject("data");
        JSONObject investRecordData = o.getJSONObject("investRecordData");
        String unitName = o.getString("entName");
        int total = investRecordData.getInteger("total");
        callbacks.printOutput(">>>>>>>>>> icp domain parser [" + unitName + " : " + total + "] >>>>>>>>>>");
        executorService.execute(() -> {
            if (Settings.AQC_BUTTON) {
                Map<String, String> subs = new HashMap<>();
                for (Object list : investRecordData.getJSONArray("list")) {
                    JSONObject sub = (JSONObject) list;
                    callbacks.printOutput(sub.getString("entName") + " : " + sub.getString("regRate"));
                }
            }
        });
    }

    private void parserICPRequest(List<String> headers, String body) {
        JSONObject parse = JSONObject.parse(body);
        parse.replace("pageNum", 1);
        parse.replace("pageSize", 300);
        String unitName = parse.getString("unitName");

        executorService.execute(() -> {
            for (String header : headers) {
                if (header.startsWith("Token")) {
                    Settings.ICP_COOKIE = header.substring(7);
                }
            }

            if (Settings.ICP_BUTTON) {
                callbacks.printOutput(">>>>>>>>>> icp domain parser [" + unitName + "] >>>>>>>>>>");
                byte[] bytes = callbacks.getHelpers().buildHttpMessage(headers, parse.toJSONString().getBytes(StandardCharsets.UTF_8));
                IHttpService service = callbacks.getHelpers().buildHttpService(Website.ICP.host().replace("\\", ""), 443, "https");
                IHttpRequestResponse iHttpRequestResponse = callbacks.makeHttpRequest(service, bytes);
                byte[] responseByte = iHttpRequestResponse.getResponse();
                IResponseInfo iResponseInfo = callbacks.getHelpers().analyzeResponse(responseByte);
                parseResponse(JSONObject.parse(new String(Arrays.copyOfRange(responseByte, iResponseInfo.getBodyOffset(), responseByte.length), StandardCharsets.UTF_8)),
                        unitName
                );
            }
        });
    }

    private void parseResponse(JSONObject resp, String target) {
        StringBuilder sb = new StringBuilder();
        if (resp.containsKey("code") && resp.getInteger("code") == 200 && resp.containsKey("params")) {
            JSONObject params = resp.getJSONObject("params");
            if (params.containsKey("list")) {
                JSONArray list = params.getJSONArray("list");
                for (Object obj : list) {
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject.containsKey("domain")) {
                        String domain = jsonObject.getString("domain");
                        sb.append(domain);
                        sb.append("\n");
                        callbacks.printOutput(new String(domain.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                    }
                }
            }
        }
        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(null, "解析结果已复制到粘贴板，并同步输出到Extensions -> Output.",target + " 解析结果",
                    JOptionPane.INFORMATION_MESSAGE);
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferable = new StringSelection(sb.toString());
            systemClipboard.setContents(transferable, null);
        }
    }
}
