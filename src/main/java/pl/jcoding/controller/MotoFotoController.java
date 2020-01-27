package pl.jcoding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.jcoding.entity.MotoFotoProfile;
import pl.jcoding.model.*;
import pl.jcoding.service.MotoFotoService;
import pl.jcoding.util.ApiException;
import pl.jcoding.util.ApiExceptionCode;
import pl.jcoding.util.MotoFotoPhotoUtil;
import pl.jcoding.util.TokenUtil;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("moto-foto-api")
public class MotoFotoController {

    private final ObjectMapper objectMapper;

    private final TokenUtil tokenUtil;

    private final AuthenticationManager authenticationManager;

    private final MotoFotoService motoFotoService;

    public MotoFotoController(ObjectMapper objectMapper, TokenUtil tokenUtil, AuthenticationManager authenticationManager, MotoFotoService motoFotoService) {
        this.objectMapper = objectMapper;
        this.tokenUtil = tokenUtil;
        this.authenticationManager = authenticationManager;
        this.motoFotoService = motoFotoService;
    }

    @GetMapping("test")
    public String testing() {
        return "OK";
    }

    @PostMapping("register")
    public void register(@Valid @RequestBody MotoFotoRegister register) throws ApiException {

        String p1 = register.getPassword();
        String p2 = register.getPasswordAgain();

        if (p1 == null || p2 == null || !p1.equals(p2))
            throw ApiException.of(ApiExceptionCode.PASSWORD_MISMATCH);

        motoFotoService.register(register);

    }

    @PostMapping("login")
    public ApiTokenResponse login(@RequestBody ApiTokenRequest request) throws Throwable {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            MotoFotoProfile profile = motoFotoService
                    .getByUsername(request.getUsername())
                    .orElseThrow(() -> ApiException.of(ApiExceptionCode.USER_IN_NOT_MOTO_FOTO_MEMBER));

            return new ApiTokenResponse(tokenUtil.generateToken(profile.getProfileOwner()));

        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

    @GetMapping("my-profile")
    public MotoFotoMember getMyProfile() throws Throwable {
        return motoFotoService.getLoginProfile().orElseThrow(() -> ApiException.of(ApiExceptionCode.USER_IN_NOT_AUTHENTICATED));
    }

    @GetMapping("my-profile/edit")
    public MotoFotoMemberEdit myProfileEdit() throws Throwable {
        return motoFotoService.getMotoFotoMemberEdit().orElseThrow(() -> ApiException.of(ApiExceptionCode.USER_IN_NOT_AUTHENTICATED));
    }

    @PostMapping("my-profile/edit")
    public void saveMyProfile(@RequestBody MotoFotoMemberEdit memberEdit) {
        motoFotoService.saveMyProfile(memberEdit);
    }

    @PostMapping("my-profile/edit/image")
    public void saveMyProfileWithImage(@RequestPart("member-edit") String memberEdit, @RequestPart("file") MultipartFile file) throws Exception {
        System.out.println("My member: " + memberEdit);
        MotoFotoMemberEdit member = objectMapper.readValue(memberEdit, MotoFotoMemberEdit.class);
        motoFotoService.saveMyProfile(file, member);
    }

    @PostMapping("my-profile/image")
    public void imageMyProfile(HttpServletResponse response) throws Exception {
        MotoFotoProfile profile = motoFotoService.getLoginMotoProfile();
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        OutputStream outputStream = response.getOutputStream();
        getImage(profile.getId(), MotoFotoPhotoUtil.PhotoKind.PROFILE, outputStream);
    }

    @PostMapping("my-profile/add-post")
    public void addPost(@RequestPart("post-info") String postInfo, @RequestPart("file") MultipartFile file) throws Exception {
        MotoFotoPostAdd post = objectMapper.readValue(postInfo, MotoFotoPostAdd.class);
        motoFotoService.savePost(file, post);
    }

    @GetMapping("my-profile/posts")
    public List<MotoFotoPostView> myPosts() {
        return motoFotoService.getMyPosts();
    }

    @GetMapping("board/posts")
    public List<MotoFotoPostView> getMyBoardPosts() {
        return motoFotoService.getBoardPosts();
    }

    @GetMapping(value = "post/{post-id}/image")
    public void getImageAsByteArray(HttpServletResponse response, @PathVariable("post-id") Long postId) throws Exception {
        InputStream in = new FileInputStream(new File(MotoFotoPhotoUtil.getMotoFotoImagePath(postId, MotoFotoPhotoUtil.PhotoKind.POST)));
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("profile/search")
    public List<MotoFotoProfileResult> getProfiles(@RequestParam(value = "search") String search) {
        return motoFotoService.getProfileSearchResults(search);
    }

    @GetMapping("profile/{profile-id}")
    public MotoFotoMember getProfile(@PathVariable("profile-id") Long profileId) {
        return motoFotoService.getMember(profileId).get();
    }

    @GetMapping("profile/{profile-id}/posts")
    public List<MotoFotoPostView> profilePosts(@PathVariable("profile-id") Long profileId) {
        return motoFotoService.getProfilePosts(profileId);
    }

    @GetMapping("profile/{profile-id}/follow")
    public void follow(@PathVariable("profile-id") Long profileId) {
        motoFotoService.followProfile(profileId);
    }

    @GetMapping("profile/{profile-id}/unfollow")
    public void unfollow(@PathVariable("profile-id") Long profileId) {
        motoFotoService.unfollowProfile(profileId);
    }

    @GetMapping("profile/{profile-id}/photo")
    public void imageMyProfile(HttpServletResponse response, @PathVariable("profile-id") Long profileId) throws Exception {
        MotoFotoProfile profile = motoFotoService.getProfile(profileId);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        OutputStream outputStream = response.getOutputStream();
        getImage(profile.getId(), MotoFotoPhotoUtil.PhotoKind.PROFILE, outputStream);
    }

    private void getImage(Long id, MotoFotoPhotoUtil.PhotoKind kind, OutputStream outputStream) throws Exception {
        try (InputStream in = new FileInputStream(new File(MotoFotoPhotoUtil.getMotoFotoImagePath(id, kind)))) {
            IOUtils.copy(in, outputStream);
        } catch (Exception e) {
            try (InputStream inputStream = new FileInputStream(new File("/srv/jcoding-api/moto-foto-image/def-img/default-user.png"))) {
                IOUtils.copy(inputStream, outputStream);
            }
        }
    }

    @DeleteMapping("/post/{post-id}/remove")
    public void removePost(@PathVariable("post-id") Long postId) {
        motoFotoService.removePost(postId);
    }

    @PostMapping("post/{post-id}/add-like")
    public void addLikePost(@PathVariable("post-id") Long postId) {
        motoFotoService.addLikePost(postId);
    }

    @PostMapping("post/{post-id}/remove-like")
    public void removeLikePost(@PathVariable("post-id") Long postId) {
        motoFotoService.removeLikePost(postId);
    }

}
