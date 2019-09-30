package app.controllers;

import app.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AccessController {

    @RequestMapping(value = {"/unauthorized"}, method = RequestMethod.GET)
    public ResponseEntity<?> hey() {

        Message message = new Message() {{
            setMessage("You are unauthorized");
        }};
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
