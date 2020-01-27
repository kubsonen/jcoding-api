package pl.jcoding.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.jcoding.entity.MotoFotoPhotoPiece;
import pl.jcoding.entity.MotoFotoPost;
import pl.jcoding.entity.MotoFotoProfile;
import pl.jcoding.entity.User;
import pl.jcoding.model.*;
import pl.jcoding.repository.MotoFotoPhotoPieceRepository;
import pl.jcoding.repository.MotoFotoPostRepository;
import pl.jcoding.repository.MotoFotoProfileRepository;
import pl.jcoding.repository.UserRepository;
import pl.jcoding.util.MotoFotoPhotoUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MotoFotoService {

    private final MotoFotoProfileRepository profileRepository;

    private final MotoFotoPostRepository postRepository;

    private final MotoFotoPhotoPieceRepository photoPieceRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public MotoFotoService(MotoFotoProfileRepository motoFotoProfileRepository, MotoFotoPostRepository motoFotoPostRepository, MotoFotoPhotoPieceRepository motoFotoPhotoPieceRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.profileRepository = motoFotoProfileRepository;
        this.postRepository = motoFotoPostRepository;
        this.photoPieceRepository = motoFotoPhotoPieceRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = Throwable.class)
    public MotoFotoProfile getLoginMotoProfile() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return null;
        Object o = a.getPrincipal();
        if (o == null) return null;
        if (o instanceof User) {
            User user = (User) o;
            return profileRepository.findByUser(user).orElse(null);
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoPostView> getMyPosts() {
        MotoFotoProfile profile = getLoginMotoProfile();
        List<Long> myLikePosts = postRepository.getByProfileLikePosts(profile);
        return postRepository.getPostsForProfile(profile).stream().map(post -> {
            MotoFotoProfile profileCreator = post.getProfile();
            return convertPost(post, profileCreator, myLikePosts);
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoPostView> getProfilePosts(Long profileId) {
        MotoFotoProfile profile = profileRepository.findByIdentification(profileId);
        List<Long> myLikePosts = postRepository.getByProfileLikePosts(profile);
        return postRepository.getPostsForProfile(profile).stream().map(post -> {
            MotoFotoProfile profileCreator = post.getProfile();
            return convertPost(post, profileCreator, myLikePosts);
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void savePost(MultipartFile photoImage, MotoFotoPostAdd postAdd) throws Exception {
        MotoFotoPost fotoPost = new MotoFotoPost();
        fotoPost.setPhotoDescription(postAdd.getDescription());
        fotoPost.setProfile(getLoginMotoProfile());
        postRepository.save(fotoPost);
        String photoFile = MotoFotoPhotoUtil.getMotoFotoImageNewFile(fotoPost.getId(), MotoFotoPhotoUtil.PhotoKind.POST);
        photoImage.transferTo(new File(photoFile));
    }

    @Transactional(rollbackFor = Throwable.class)
    public MotoFotoProfile register(MotoFotoRegister register) {
        User user = new User();
        user.setUsername(register.getUsername());
        user.setPassword(passwordEncoder.encode(register.getPassword()));
        user.setEmail(register.getEmail());
        MotoFotoProfile profile = new MotoFotoProfile();
        profile.setProfileOwner(user);
        profile.setNickname(register.getUsername());
        profile.setName(register.getName());
        return profileRepository.save(profile);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Optional<MotoFotoProfile> getByUsername(String username) {
        User user = userRepository.findByUsername(username).get();
        return profileRepository.findByUser(user);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Optional<MotoFotoMember> getLoginProfile() {
        MotoFotoProfile profile = getLoginMotoProfile();
        if (profile == null) return Optional.empty();
        return Optional.of(convertProfile(profile));
    }

    @Transactional(rollbackFor = Throwable.class)
    public MotoFotoProfile getProfile(Long profileId) {
        return profileRepository.findByIdentification(profileId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Optional<MotoFotoMember> getMember(Long id) {
        MotoFotoProfile loginProfile = getLoginMotoProfile();
        MotoFotoProfile profile = profileRepository.findByIdentification(id);
        final MotoFotoMember member = convertProfile(profile);
        profileRepository
                .findMyFollower(profile, loginProfile)
                .ifPresent(p -> member.setMyFollow(true));
        return Optional.of(member);
    }

    public MotoFotoMember convertProfile(MotoFotoProfile profile) {
        MotoFotoMember member = new MotoFotoMember();
        member.setNickname(profile.getNickname());
        member.setPosts(postRepository.postsCount(profile));
        member.setFollowing(profileRepository.countFollowing(profile));
        member.setFollowers(profileRepository.countFollowers(profile));
        return member;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Optional<MotoFotoMemberEdit> getMotoFotoMemberEdit() {
        MotoFotoProfile profile = getLoginMotoProfile();
        if (profile == null) return Optional.empty();
        MotoFotoMemberEdit member = new MotoFotoMemberEdit();
        member.setName(profile.getName());
        member.setNickname(profile.getNickname());
        return Optional.of(member);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveMyProfile(MotoFotoMemberEdit memberEdit) {
        MotoFotoProfile profile = getLoginMotoProfile();
        profile.setName(memberEdit.getName());
        profile.setNickname(memberEdit.getNickname());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveMyProfile(MultipartFile file, MotoFotoMemberEdit memberEdit) throws Exception {
        saveMyProfile(memberEdit);
        MotoFotoProfile profile = getLoginMotoProfile();
        String photoFile = MotoFotoPhotoUtil.getMotoFotoImageNewFile(profile.getId(), MotoFotoPhotoUtil.PhotoKind.PROFILE);
        file.transferTo(new File(photoFile));
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoPostView> getBoardPosts() {
        MotoFotoProfile profileLogin = getLoginMotoProfile();
        List<Long> myLikePosts = postRepository.getByProfileLikePosts(profileLogin);
        return postRepository
                .getPostsForBoard(profileLogin, PageRequest.of(0, 25))
                .stream()
                .map(post -> {
                    MotoFotoProfile profile = post.getProfile();
                    return convertPost(post, profile, myLikePosts);
                }).collect(Collectors.toList());
    }

    private MotoFotoPostView convertPost(MotoFotoPost post, MotoFotoProfile profile, List<Long> myLikePosts) {
        MotoFotoPostView postView = new MotoFotoPostView();
        if (profile != null) {
            postView.setProfileId(profile.getId());
            postView.setProfileNickname(profile.getNickname());
        }
        postView.setPostId(post.getId());
        postView.setDescription(post.getPhotoDescription());

        LocalDateTime create = post.getCreatedDate();
        if (create != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formattedDateTime = create.format(formatter);
            postView.setCreateDate(formattedDateTime);
        }

        if (myLikePosts != null && myLikePosts.stream().anyMatch(i -> {
            if (i == null) return false;
            return i.equals(post.getId());
        }))
            postView.setLike(true);

        return postView;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoPhotoPiece> getPostPhotoPieces(Long postId) {
        MotoFotoPost fotoPost = postRepository.getPost(postId);
        return photoPieceRepository.getPhotoPiecesForPost(fotoPost);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Optional<MotoFotoMember> getMemberByNickname(String nickname) {
        return profileRepository.findByNickname(nickname).map(p -> {
            MotoFotoMember member = new MotoFotoMember();
            member.setNickname(p.getNickname());
            return member;
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoMember> searchMembers(String searchQuery) {
        return profileRepository
                .searchNicknames(searchQuery)
                .stream()
                .map(mfp -> {
                    MotoFotoMember member = new MotoFotoMember();
                    member.setNickname(mfp.getNickname());
                    return member;
                }).collect(Collectors.toList());
    }

    public String defaultProfileImageBase64() {
        try {
            BufferedImage bi = ImageIO.read(new File("/srv/jcoding-api/moto-foto-image/def-img/default-user.png"));
            String imageString = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ImageIO.write(bi, "png", bos);
                byte[] imageBytes = bos.toByteArray();

                Base64.Encoder encoder = Base64.getEncoder();
                imageString = encoder.encodeToString(imageBytes);

                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imageString;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<MotoFotoProfileResult> getProfileSearchResults(String search) {
        MotoFotoProfile profile = getLoginMotoProfile();
        return profileRepository.getProfileResults(search, profile);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void followProfile(Long profileId) {
        MotoFotoProfile loginProfile = profileRepository.findByIdentification(getLoginMotoProfile().getId());
        MotoFotoProfile profile = profileRepository.findByIdentification(profileId);
        loginProfile.getFollows().add(profile);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void unfollowProfile(Long profileId) {
        MotoFotoProfile loginProfile = profileRepository.findByIdentification(getLoginMotoProfile().getId());
        MotoFotoProfile profile = profileRepository.findByIdentification(profileId);
        loginProfile.getFollows().removeIf(p -> p.getId().equals(profile.getId()));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removePost(Long postId) {
        MotoFotoPost fotoPost = postRepository.getPost(postId);
        fotoPost.setArchive(true);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void addLikePost(Long postId) {
        MotoFotoProfile lp = getLoginMotoProfile();
        MotoFotoPost fotoPost = postRepository.getPost(postId);
        if (!fotoPost.getLikes().stream().anyMatch(profile -> profile.getId().equals(lp.getId()))) {
            fotoPost.getLikes().add(lp);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void removeLikePost(Long postId) {
        MotoFotoProfile lp = getLoginMotoProfile();
        MotoFotoPost fotoPost = postRepository.getPost(postId);
        fotoPost.getLikes().removeIf(profile -> {
            if (profile == null || lp == null) return false;
            return profile.getId().equals(lp.getId());
        });
    }

}
