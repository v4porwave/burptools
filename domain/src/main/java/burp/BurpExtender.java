package burp;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO1: 增加被动式扫描
 * TODO2: 优化结果显示方式(可以放在logger中)
 * TODO3: 可以增加颜色标记(考虑再logger中)
 */
public class BurpExtender implements IBurpExtender,IContextMenuFactory,IProxyListener {

    private IBurpExtenderCallbacks callbacks;

    private String[] websites = new String[]{
            "sou\\.xiaolanben\\.com",
            "hlwicpfwc\\.miit\\.gov\\.cn"
    };

    private String[] uris = new String[]{
            "/api\\.xiaolanben\\.com/xlb-gateway/blue-book/group/groupData",
            ""
    };

    //==================================IBurpExtender=======================================
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;

        callbacks.setExtensionName("Domain Parser");
        callbacks.registerContextMenuFactory(this);
        callbacks.registerProxyListener(this);

        callbacks.printOutput("Success to extension load!");
    }

    //================================IContextMenuFactory===================================
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        List<JMenuItem> menuItems = new ArrayList<>();
        if (invocation.getToolFlag() == callbacks.TOOL_REPEATER) {
            IHttpRequestResponse selectedMessage = invocation.getSelectedMessages()[0];
            byte[] request = selectedMessage.getRequest();
            byte[] response = selectedMessage.getResponse();
            String res ;
            String resp = "";
            try {
                res = new String(request, StandardCharsets.UTF_8);
                if (response != null && response.length > 0) {
                    resp = new String(response, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                res = new String(request);
                if (response != null && response.length > 0) {
                    resp = new String(response);
                }
            }
            String[] resLines = res.split("\r\n");
            for (String resLine : resLines) {
                if (resLine.startsWith("Host") && resLine.contains("hlwicpfwc.miit.gov.cn")) {
                    if (!resp.isEmpty()) {
                        menuItems.add(createParserMenu(resp.split("\r\n"), selectedMessage));
                    }
                    menuItems.add(createDataMenu(res.split("\r\n"), selectedMessage));

                }
            }
        }
        return menuItems;
    }

    private JMenuItem createDataMenu(String[] resLines, IHttpRequestResponse selectedMessage) {
        JMenuItem item = new JMenuItem("fill data");
        item.setEnabled(true);
        item.addActionListener(event -> {
            int length = resLines.length;
            String resLine = resLines[length - 1];
            JSONObject dataObject = JSONObject.parse(resLine);
            if (dataObject.containsKey("pageNum") && dataObject.containsKey("pageSize")) {
                dataObject.replace("pageNum", 1);
                dataObject.replace("pageSize", 300);
                List<String> headers = Arrays.asList(Arrays.copyOf(resLines, length - 2));
                IExtensionHelpers helpers = callbacks.getHelpers();
                byte[] request = helpers.buildHttpMessage(headers, dataObject.toJSONString().getBytes(StandardCharsets.UTF_8));
                selectedMessage.setRequest(request);
            }
        });
        return item;
    }

    private JMenuItem createParserMenu(String[] respLines, IHttpRequestResponse selectedMessage) {
        int length = respLines.length;
        String jsonStr = respLines[length - 1];
        JSONObject resp = JSONObject.parse(jsonStr);
        //解析json工具
        JMenuItem item = new JMenuItem("domain parser");
        item.setEnabled(true);
        item.addActionListener(event -> {
            StringBuilder sb = new StringBuilder();
            if (resp.containsKey("code") && resp.getInteger("code") == 200 && resp.containsKey("params")) {
                JSONObject params = resp.getJSONObject("params");
                if (params.containsKey("list")) {
                    JSONArray list = params.getJSONArray("list");
                    callbacks.printOutput("icp domain parser >>>");
                    for (Object obj : list) {
                        JSONObject jsonObject = (JSONObject) obj;
                        if (jsonObject.containsKey("domain")) {
                            String domain = jsonObject.getString("domain");
                            sb.append(domain);
                            sb.append("\n");
                            callbacks.printOutput(domain);
                        }
                    }
                }
            }
            if (sb.length() > 0) {
                JOptionPane.showMessageDialog(null, "已复制到粘贴板","域名解析结果",
                        JOptionPane.INFORMATION_MESSAGE);
                Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = new StringSelection(sb.toString());
                systemClipboard.setContents(transferable, null);
            }
        });
        return item;
    }

    //================================IProxyListener===================================
    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (!messageIsRequest) {
            byte[] requestByte = message.getMessageInfo().getRequest();
            byte[] responseByte = message.getMessageInfo().getResponse();
            IRequestInfo iRequestInfo = callbacks.getHelpers().analyzeRequest(requestByte);
            IResponseInfo iResponseInfo = callbacks.getHelpers().analyzeResponse(responseByte);
            List<String> headers = iRequestInfo.getHeaders();
            if (iResponseInfo.getStatusCode() == 200 ) {
                for (int i = 0;i < websites.length; i++) {
                    Pattern hostPattern = Pattern.compile("^Host:\\s(" + websites[i] + ")$");
                    if (hostPattern.matcher(headers.get(1)).find()) {
                        Pattern uriPattern = Pattern.compile("^GET\\s(" + uris[i] + ")");
                        if (uriPattern.matcher(headers.get(0)).find()) {
                            parserBody(new String(responseByte).substring(iResponseInfo.getBodyOffset()), i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void parserBody(String body, int offset) {
        switch (offset) {
            case 0: parserXLBBody(body); break;
            case 1: parserICPBody(body); break;
            default: callbacks.printError("Unknown website.");
        }
    }

    private void parserXLBBody(String body) {
        JSONArray array = JSONArray.parse(body);
        callbacks.printOutput("xlb domain parser >>>");
        for (int i = 0;i < array.size();i ++) {
            JSONObject o = (JSONObject) array.get(i);
            callbacks.printOutput(o.getString("domain"));
        }
    }

    private void parserICPBody(String body) {
    }
}
