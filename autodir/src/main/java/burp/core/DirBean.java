package burp.core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DirBean {
    
    private String uri;

    private String host;

    private List<String> dirs;
}
