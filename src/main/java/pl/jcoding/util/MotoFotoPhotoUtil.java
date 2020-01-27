package pl.jcoding.util;

import pl.jcoding.entity.MotoFotoPhotoPiece;
import pl.jcoding.entity.MotoFotoPost;
import sun.net.www.URLConnection;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MotoFotoPhotoUtil {

    public enum PhotoKind {
        PROFILE,POST
    }

    private static final String MIME_PREFIX = "image/";
    private static final String[] HANDLING_MIME_TYPES = new String[]{"jpg", "jpeg"};
    private static final String IMAGE_PATH = "/srv/jcoding-api/moto-foto-image";
    private static final String IMAGE_PREFIX_PATH_POST = "MOTO_FOTO_POST_IMAGE_";
    private static final String IMAGE_PREFIX_PATH_PROFILE = "MOTO_FOTO_PROFILE_IMAGE_";

    public static MotoFotoPost preparePhoto(String photoName, String base64) throws Throwable {
        if (!Commons.isValid(base64)) return null;

        if (base64.contains(",")) {
            int io = base64.indexOf(",");
            base64 = base64.substring(io + 1);
        }

        byte[] fileBytes = Base64.getDecoder().decode(base64);
        String mime;
        try (InputStream is = new BufferedInputStream(new ByteArrayInputStream(fileBytes))) {
            mime = URLConnection.guessContentTypeFromStream(is);
        } catch (Throwable t) {
            throw ApiException.of(t, ApiExceptionCode.CANNOT_GET_MIME_TYPE);
        }

        if (mime == null || mime.trim().isEmpty()) throw ApiException.of(ApiExceptionCode.NULL_OR_EMPTY_MIME_TYPE);

        mime = mime.trim();
        if (!mime.startsWith(MIME_PREFIX)) throw ApiException.of(ApiExceptionCode.MIME_IS_NOT_AN_IMAGE);

        mime = mime.substring(MIME_PREFIX.length());
        final String finalMime = mime;

        boolean correctMime = Arrays.stream(HANDLING_MIME_TYPES).anyMatch(s -> s.equals(finalMime));
        if (!correctMime) throw ApiException.of(ApiExceptionCode.UNHANDLED_MIME_TYPE);

        MotoFotoPost photo = new MotoFotoPost();
        photo.setPhotoOriginalName(photoName);
        photo.setPieces(getPhotoPieces(base64, MotoFotoPhotoPiece.MAX_PIECE_BLOB_LENGTH).stream().collect(Collectors.toSet()));
        return photo;
    }

    public static List<MotoFotoPhotoPiece> getPhotoPieces(String base64, Integer maxPieceLen) {

        if (!Commons.isValid(base64)) return Collections.emptyList();
        List<String> pieces = new ArrayList<>();
        base64 = base64.trim();

        do {

            int len = base64.length();
            if (len > maxPieceLen) {
                pieces.add(base64.substring(0, maxPieceLen));
                base64 = base64.substring(maxPieceLen);
            } else {
                pieces.add(base64);
                base64 = "";
            }

        } while (base64.length() > 0);

        return IntStream
                .range(0, pieces.size())
                .mapToObj(i -> MotoFotoPhotoPiece.of(i, pieces.get(i).getBytes()))
                .collect(Collectors.toList());

    }

    public static String makeImage(Collection<MotoFotoPhotoPiece> pieces) throws Throwable {

        if (pieces == null || pieces.isEmpty()) throw ApiException.of(ApiExceptionCode.NOT_FOUND_PIECES_IN_PHOTO);

        String base64 = pieces.stream()
                .sorted(Comparator.comparing(MotoFotoPhotoPiece::getOrdinal))
                .map(piece -> new String(piece.getPieceData()))
                .collect(Collectors.joining());

        return base64;
    }

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
