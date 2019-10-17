package app.controllers;

import app.model.view.Message;
import app.model.binding.*;
import app.model.service.UserServiceModel;
import app.model.view.UserAllViewModel;
import app.model.view.UserProfileViewModel;
import app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    //Have to validate request for empty strings.
    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> registerConfirm(@RequestBody UserRegisterBindingModel model) {
        if (!model.getPassword().equals(model.getConfirmPassword())) {
            Message message = new Message();
            message.setMessage("Your password is not  equal with confirm password");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        this.userService.registerUser(this.modelMapper.map(model, UserServiceModel.class));

        Message message = new Message();
        message.setMessage("Your registration is successful");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> profile(Principal principal) {
        String name = principal.getName();
        Object userByUserName = this.userService.findUserByUserName(name);
        UserProfileViewModel userProfileViewModel = this.modelMapper.map(userByUserName, UserProfileViewModel.class);

        return new ResponseEntity<>(userProfileViewModel, HttpStatus.OK);
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editProfile(Principal principal) {
        String name = principal.getName();
        Object userByUserName = this.userService.findUserByUserName(name);
        UserProfileViewModel userProfileViewModel = this.modelMapper.map(userByUserName, UserProfileViewModel.class);

        return new ResponseEntity<>(userProfileViewModel, HttpStatus.OK);
    }

    @PatchMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editProfileConfirm(@RequestBody UserEditBindingModel model) {
        if (model.getPassword() != null && !model.getPassword().equals(model.getConfirmPassword())) {
            UserProfileViewModel userProfileViewModel = this.modelMapper.map(model, UserProfileViewModel.class);

            return new ResponseEntity<>(userProfileViewModel, HttpStatus.OK);
        }

        this.userService.editUserProfile(this.modelMapper.map(model, UserServiceModel.class), model.getOldPassword());
        Message message = new Message();
        message.setMessage("You successfully edited your profile");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> allUsers(/*ModelAndView modelAndView*/) {
        List<UserAllViewModel> users = this.userService.findAllUsers()
                .stream()
                .map(u -> {
                    UserAllViewModel user = this.modelMapper.map(u, UserAllViewModel.class);
                    user.setAuthorities(u.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet()));

                    return user;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setUser(@PathVariable String id) {
        this.userService.setUserRole(id, "user");
        Message message = new Message();
        message.setMessage("You successfully changed the role");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setModerator(@PathVariable String id) {
        this.userService.setUserRole(id, "moderator");
        Message message = new Message();
        message.setMessage("You successfully changed the role");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setAdmin(@PathVariable String id) {
        this.userService.setUserRole(id, "admin");
        Message message = new Message();
        message.setMessage("You successfully changed the role");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        boolean isDeleted = this.userService.deleteUserById(id);

        if (!isDeleted) {
            Message message = new Message();
            message.setMessage("User was not deleted");

            return new ResponseEntity<>(message, HttpStatus.OK);
        }

        Message message = new Message();
        message.setMessage("You successfully deleted the user");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
