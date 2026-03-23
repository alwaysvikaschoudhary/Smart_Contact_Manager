package com.scm.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.forms.UserProfileForm;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    // user dashbaord page

    @RequestMapping(value = "/dashboard")
    public String userDashboard() {
        System.out.println("User dashboard");
        return "user/dashboard";
    }

    // user profile page

    @RequestMapping(value = "/profile")
    public String userProfile(Model model, Authentication authentication) {

        return "user/profile";
    }

    // edit profile page
    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        UserProfileForm userProfileForm = new UserProfileForm();
        userProfileForm.setName(user.getName());
        userProfileForm.setAbout(user.getAbout());
        userProfileForm.setPhoneNumber(user.getPhoneNumber());
        userProfileForm.setProfilePic(user.getProfilePic());

        model.addAttribute("userProfileForm", userProfileForm);

        return "user/edit_profile";
    }

    // process update profile
    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute UserProfileForm userProfileForm,
            BindingResult bindingResult,
            Authentication authentication,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "user/edit_profile";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);

        user.setName(userProfileForm.getName());
        user.setAbout(userProfileForm.getAbout());
        user.setPhoneNumber(userProfileForm.getPhoneNumber());

        // upload image
        if (userProfileForm.getProfileImage() != null && !userProfileForm.getProfileImage().isEmpty()) {
            String fileName = UUID.randomUUID().toString();
            String profileImageUrl = imageService.uploadImage(userProfileForm.getProfileImage(), fileName);
            user.setProfilePic(profileImageUrl);
        }

        userService.updateUser(user);

        session.setAttribute("message", Message.builder()
                .content("Profile Updated Successfully")
                .type(MessageType.green)
                .build());

        return "redirect:/user/profile";
    }

    // user add contacts page

    // user view contacts

    // user edit contact

    // user delete contact

}
