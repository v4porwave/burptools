package burp.core;

import burp.IBurpExtenderCallbacks;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 配置和工具的集合类
 * TODO1: 调整为可通过gui修改配置
 * TODO2: 如果后期需求大，将配置和工具相关的内容分开
 */
@Getter
@Setter
public class ConfigCore {

    public static final ConfigCore instance = new ConfigCore();

    private ConfigCore() {
        uriPattern = Pattern.compile("^(POST|GET|PUT|DELETE|OPTION|HEAD|PATCH|CONNECT|TRACE)\\s+.*HTTP/[12.]{1,3}$");
        hostPattern = Pattern.compile("^Host:\\s+.*");
        enter = "\r\n";
        mode = Mode.PASSIVE;
        title = "AutoDir";
        useWhiteList = false;
        hostWhiteList = new ArrayList<>();
    }


    private IBurpExtenderCallbacks callbacks;

    private Mode mode;

    private Pattern uriPattern ;

    private String enter ;

    private Pattern hostPattern ;

    private String title;

    private Boolean useWhiteList ;

    private List<String> hostWhiteList ;
}
