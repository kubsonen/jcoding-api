package pl.jcoding.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MotoFotoPhotoUtil {

    public enum PhotoKind {
        PROFILE,POST
    }

    private static final String IMAGE_PATH = "/srv/jcoding-api/moto-foto-image";
    private static final String IMAGE_PREFIX_PATH_POST = "MOTO_FOTO_POST_IMAGE_";
    private static final String IMAGE_PREFIX_PATH_PROFILE = "MOTO_FOTO_PROFILE_IMAGE_";

    public static String getMotoFotoImageFolder() {
        File folder = new File(IMAGE_PATH);
        if (!folder.exists()) folder.mkdirs();
        return IMAGE_PATH;
    }

    public static String getMotoFotoImagePath(Long id, PhotoKind kind) {
        if (id == null || id == 0L) throw new IllegalArgumentException("Id not found");
        String name = IMAGE_PREFIX_PATH_POST + id;
        if (PhotoKind.PROFILE.equals(kind)) {
            name = IMAGE_PREFIX_PATH_PROFILE + id;
        }
        File folder = new File(getMotoFotoImageFolder());
        final String finalName = name;
        boolean fileExists = Arrays.stream(folder.listFiles()).anyMatch(file -> file.getName().equals(finalName));
        File file = new File(getMotoFotoImageFolder(), name);
        if (fileExists) {
            return file.getAbsolutePath();
        }
        throw new IllegalArgumentException("Not found photo");
    }

    public static String getMotoFotoImageNewFile(Long id, PhotoKind kind) throws Exception {
        if (id == null || id == 0L) throw new IllegalArgumentException("Id not found");
        String name = "";
        switch (kind) {
            case PROFILE:
                name = IMAGE_PREFIX_PATH_PROFILE + id;
                break;
            case POST:
                name = IMAGE_PREFIX_PATH_POST + id;
                break;
        }
        File folder = new File(getMotoFotoImageFolder());
        File file = new File(getMotoFotoImageFolder(), name);
        final String finalName = name;
        if (Arrays.stream(folder.listFiles()).anyMatch(f -> f.getName().equals(finalName))) {
            return file.getAbsolutePath();
        } else {
            try {
                if (file.createNewFile())
                    return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        throw new Exception("Undefined fail");
    }

}
