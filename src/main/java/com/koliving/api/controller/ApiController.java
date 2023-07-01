package com.koliving.api.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/careateRoom")
    public void createRoom(@RequestBody @Valid CreateRoomRequest createRoomRequest) {

    }

    @PostMapping("/getRoom")
    public void getRoom() {

    }

    @PostMapping("/getRoomList")
    public void getRoomList() {

    }
}
