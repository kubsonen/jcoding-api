package pl.jcoding.util;

import com.gargoylesoftware.htmlunit.html.DomElement;
import org.springframework.stereotype.Component;

@Component
public class DomUtil {

    @SuppressWarnings("unchecked")
    public <T extends DomElement> T getByIndex(DomElement parent, int index) {
        int i = 0;
        for (DomElement domElement : parent.getChildElements()) {
            if (i == index) return (T) domElement;
            ++i;
        }
        throw new IllegalStateException("Index " + index + " not found. The last index was " + (i - 1));
    }

    public <T extends DomElement> T getByIndexes(DomElement parent, int... indexes) {
        T de = null;
        for (int i = 0; i < indexes.length; i++) {
            if (de == null && i == 0) {
                de = getByIndex(parent, indexes[i]);
            } else if (de != null) {
                de = getByIndex(de, indexes[i]);
            } else {
                throw new IllegalStateException("Dom element is null for index " + indexes[i]);
            }
        }
        return de;
    }

    public String getContentByIndexes(DomElement parent, int... indexes) {
        String content = getByIndexes(parent, indexes).getTextContent();
        if (content != null) {
            content = content.trim().replace("\n", "").replace("\t", "");
            while (content.contains("  "))
                content = content.replace("  ", " ");
        }
        return content;
    }

}
