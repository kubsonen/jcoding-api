package pl.jcoding.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GalleryUtil {

    private static final Logger logger = LoggerFactory.getLogger(GalleryUtil.class);

    private static final Integer IDENTITY_LENGTH = 40;
    private static final Integer FILE_NAME_LENGTH = 1000;

    @Value("${gallery.files}")
    private String galleryFolder;

    @Value("${gallery.files.temp}")
    private String galleryFolderTemp;

    @PostConstruct
    public void init() {
        //Create folders
        File folder = new File(galleryFolder);
        File folderTemp = new File(galleryFolderTemp);
        if (!folder.exists()) folder.mkdirs();
        if (!folderTemp.exists()) folderTemp.mkdirs();
    }

    public File getGalleryFolder() {
        return new File(galleryFolder);
    }

    public File getGalleryFolderTemp() {
        return new File(galleryFolderTemp);
    }

    private boolean filesContains(UUID fileIdentity, File[] files) {
        return Arrays.stream(files)
                .map(file -> UUID.fromString(file.getName()))
                .collect(Collectors.toList())
                .contains(fileIdentity);
    }

    public boolean fileExistsInGalleryFolder(UUID fileIdentity) {
        return filesContains(fileIdentity, getGalleryFolder().listFiles());
    }

    public boolean fileExistsInGalleryFolderTemp(UUID fileIdentity) {
        return filesContains(fileIdentity, getGalleryFolderTemp().listFiles());
    }

    public File getFileFromGalleryFolder(UUID fileIdentity) throws Exception {
        if (!fileExistsInGalleryFolder(fileIdentity)) throw new Exception("File not found!");
        return new File(getGalleryFolder().getCanonicalPath() + File.separator + fileIdentity.toString());
    }

    public File getFileFromGalleryFolderTemp(UUID fileIdentity) throws Exception {
        if (!fileExistsInGalleryFolderTemp(fileIdentity)) throw new Exception("File not found!");
        return new File(getGalleryFolderTemp().getCanonicalPath() + File.separator + fileIdentity.toString());
    }

    public UUID getTempFile() throws Exception {
        File tempFolder = getGalleryFolderTemp();
        List<String> fileNames =
                Arrays.stream(tempFolder.listFiles()).map(file -> file.getName()).collect(Collectors.toList());
        for (int i = 0; i < 100; i++) {
            final UUID uuid = UUID.randomUUID();
            final String uuidText = uuid.toString();
            if (!fileNames.contains(uuidText)) {
                final String tempFilePath = tempFolder.getCanonicalPath() + File.separator + uuidText;
                if (new File(tempFilePath).createNewFile()) {
                    return uuid;
                }
            }

        }
        throw new Exception("Cannot create file");
    }

    public UUID saveMultipartToTemp(MultipartFile file) throws Exception {
        File tempFile = getFileFromGalleryFolderTemp(getTempFile());
        file.transferTo(tempFile);
        return UUID.fromString(tempFile.getName());
    }

    public void moveTempFile(UUID tempFileUuid) throws Exception {
        File galleryFolder = getGalleryFolder();
        File galleryFolderTemp = getGalleryFolderTemp();
        List<String> tempFilesName =
                Arrays.stream(galleryFolderTemp.listFiles()).map(file -> file.getName()).collect(Collectors.toList());

        String fileUuidText = tempFileUuid.toString();
        if (tempFilesName.contains(fileUuidText)) {
            File tempFile = new File(galleryFolderTemp.getCanonicalPath() + File.separator + fileUuidText);
            File mainFile = new File(galleryFolder.getCanonicalPath() + File.separator + fileUuidText);
            FileUtils.moveFile(tempFile, mainFile);
            return;
        }

        throw new Exception("Cannot move file to main folder");

    }

    public UUID saveFileToGallery(UUID galleryFileIdentity, String fileOriginalName, UUID tempFileIdentity) throws Exception {
        //Check the files exists
        if (!(fileExistsInGalleryFolder(galleryFileIdentity) && fileExistsInGalleryFolderTemp(tempFileIdentity)))
            throw new Exception("Gallery or temp file not found");

        File galleryFile = getFileFromGalleryFolder(galleryFileIdentity);
        return performSavePhotoToGalleryFile(tempFileIdentity, fileOriginalName, galleryFile);
    }

    public UUID saveFileToGalleryTemp(UUID galleryFileTempIdentity, String fileOriginalName, UUID tempFileIdentity) throws Exception {

        //Check the files exists
        if (!(fileExistsInGalleryFolderTemp(galleryFileTempIdentity) && fileExistsInGalleryFolderTemp(tempFileIdentity)))
            throw new Exception("Gallery or temp file not found");

        File galleryFile = getFileFromGalleryFolderTemp(galleryFileTempIdentity);
        return performSavePhotoToGalleryFile(tempFileIdentity, fileOriginalName, galleryFile);

    }

    private UUID performSavePhotoToGalleryFile(UUID tempFileIdentity, String fileOriginalName, File galleryFile) throws Exception {
        File tempFile = getFileFromGalleryFolderTemp(tempFileIdentity);
        byte[] tempFileBytes = FileUtils.readFileToByteArray(tempFile);

        UUID galleryPhotoIdentity = UUID.randomUUID();
        String galleryPhotoIdentityText = galleryPhotoIdentity.toString();

        StringBuilder sb = new StringBuilder();
        sb
                .append(Commons.fillSpaces(galleryPhotoIdentityText, IDENTITY_LENGTH))
                .append(Commons.fillSpaces(fileOriginalName, FILE_NAME_LENGTH))
                .append(Base64.getEncoder().encodeToString(tempFileBytes))
                .append("\n");

        try (BufferedWriter writer = Files.newBufferedWriter(galleryFile.toPath(), StandardOpenOption.APPEND)) {
            writer.write(sb.toString());
        }

        return galleryPhotoIdentity;
    }

    public List<UUID> getIdentitiesFromGalleryTempFile(UUID galleryTempFile) throws Exception {
        List<UUID> ids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getFileFromGalleryFolderTemp(galleryTempFile)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > (IDENTITY_LENGTH + FILE_NAME_LENGTH + 1)) {
                    UUID uuid = UUID.fromString(line.substring(0, IDENTITY_LENGTH).trim());
                    ids.add(uuid);
                }
            }
        }
        return ids;
    }

    public void checkTempGalleryCorrection(UUID galleryIdentity, List<UUID> photosIdentities) throws Exception {

        if (!fileExistsInGalleryFolderTemp(galleryIdentity)) throw new Exception("File not exists in folder temp");
        List<UUID> identitiesFromFile = getIdentitiesFromGalleryTempFile(galleryIdentity);

        if (identitiesFromFile.size() == photosIdentities.size()) {
            Iterator<UUID> identitiesIterator = identitiesFromFile.iterator();
            IDENTITIES_LOOP:
            while (identitiesIterator.hasNext()) {
                if (!photosIdentities.contains(identitiesIterator.next())) break IDENTITIES_LOOP;
                if (!identitiesIterator.hasNext()) return;
            }
        }

        throw new Exception("Identities in file and in POJO are not identical");

    }

    public byte[] getPhotoBytesFromGallery(UUID galleryIdentity, UUID photoIdentity) throws Exception{
        if (fileExistsInGalleryFolder(galleryIdentity)) {
            return getPhotoFromGalleryFile(getFileFromGalleryFolder(galleryIdentity), photoIdentity);
        } else if (fileExistsInGalleryFolderTemp(galleryIdentity)) {
            return getPhotoFromGalleryFile(getFileFromGalleryFolderTemp(galleryIdentity), photoIdentity);
        } else {
            logger.error("Gallery not found: " + galleryIdentity.toString());
            return null;
        }

    }

    public byte[] getPhotoFromGalleryFile(File galleryFile, UUID photoIdentity) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(galleryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > (IDENTITY_LENGTH + FILE_NAME_LENGTH + 1)) {
                    if (line.startsWith(photoIdentity.toString())) {
                        String base64 = line.substring(IDENTITY_LENGTH + FILE_NAME_LENGTH);
                        return Base64.getDecoder().decode(base64);
                    }
                }
            }
        }
        logger.error("Photo not found: " + photoIdentity.toString());
        return null;
    }

}
