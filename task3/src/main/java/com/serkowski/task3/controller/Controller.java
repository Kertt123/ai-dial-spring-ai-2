package com.serkowski.task3.controller;

import com.serkowski.task3.clients.DialClient;
import com.serkowski.task3.clients.DialCustomClient;
import com.serkowski.task3.model.TextWithImgPathRequest;
import com.serkowski.task3.model.TextWithImgUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {


    @Autowired
    private DialClient dialClient;

    @Autowired
    private DialCustomClient dialCustomClient;

    @PostMapping("/textWithImageUrl")
    Mono<String> textWithImageUrl(@RequestBody Mono<TextWithImgUrlRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletionsWithImageUrl(request.message(), request.imageType(), request.imageUrl()));
    }

    @PostMapping("/textWithImagePath")
    Mono<String> textWithImagePath(@RequestBody Mono<TextWithImgPathRequest> requestBody) {
        return requestBody.flatMap(request -> dialClient.getCompletionsWithImagePath(request.message(), request.imageType(), request.imagePath()));
    }

    @PostMapping("/textWithImagePathDial")
    Mono<String> textWithImagePathDial(@RequestBody Mono<TextWithImgPathRequest> requestBody) {
        return requestBody.flatMap(request -> dialCustomClient.getCompletionsWithImagePathDIAL(request.message(), request.imageType(), request.imagePath()));
    }


}
