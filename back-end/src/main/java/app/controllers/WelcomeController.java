package app.controllers;

import app.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class WelcomeController {

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = {"/welcome"}, method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> welcome() {

        Message message = new Message() {{
            setMessage("Welcome");
        }};
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
