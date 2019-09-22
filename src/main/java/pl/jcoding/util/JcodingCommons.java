package pl.jcoding.util;

public class JcodingCommons {

    private static final String SPACE = " ";

    public static final String fillSpaces(String text, int length) {

        if (text == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(SPACE);
            }
            return sb.toString();
        } else {

            if (text.length() > length) {
                return text.substring(0, length);
            } else {

                StringBuilder sb = new StringBuilder();
                sb.append(text);
                for (int i = 0; i < (length - text.length()); i++) {
                    sb.append(SPACE);
                }
                return sb.toString();
            }

        }
    }

}
