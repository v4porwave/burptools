package burp;

import java.util.regex.Pattern;

public enum Website {

    ICP("/icpproject_query/api/icpAbbreviateInfo/queryByCondition", "hlwicpfwc\\.miit\\.gov\\.cn"),

    XLB("/api\\.xiaolanben\\.com/xlb-gateway/blue-book/group/groupData","sou\\.xiaolanben\\.com");

    private String uri;
    private String host;

    private Pattern uriPattern;

    private Pattern hostPattern;

    Website(String uri, String host) {
        this.uri = uri;
        this.host = host;
        this.hostPattern = Pattern.compile("^Host:\\s(" + host + ")$");
        this.uriPattern = Pattern.compile("^(GET|POST)\\s(" + uri + ")");
    }

    public String getUri() {
        return uri;
    }

    public String host() {
        return host;
    }

    public Pattern getUriPattern() {
        return uriPattern;
    }

    public Pattern getHostPattern() {
        return hostPattern;
    }
}
