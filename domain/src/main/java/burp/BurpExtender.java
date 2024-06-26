package burp;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurpExtender implements IBurpExtender,IContextMenuFactory {

    private IBurpExtenderCallbacks callbacks;

    //==================================IBurpExtender=======================================
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;

        callbacks.setExtensionName("Domain Parser");
        callbacks.registerContextMenuFactory(this);

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
                    for (Object obj : list) {
                        JSONObject jsonObject = (JSONObject) obj;
                        if (jsonObject.containsKey("domain")) {
                            sb.append(jsonObject.getString("domain"));
                            sb.append("\n");
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
}
