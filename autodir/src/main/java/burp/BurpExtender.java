package burp;

import burp.core.ConfigCore;
import burp.listener.HttpListener;
import burp.ui.AutoDirTab;

public class BurpExtender implements IBurpExtender{

    //==================================IBurpExtender=======================================
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {

        callbacks.addSuiteTab(new AutoDirTab());
        callbacks.registerProxyListener(new HttpListener());
        callbacks.printOutput("Success to load auto-dir!");
    }
}
