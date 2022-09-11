package com.github.portforesearch.ecommurzbe.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Response Generator, generate response when endpoint has called
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Response {
    /**
     * Generate response success when response need to return the data
     *
     * @param message
     * @return
     */
    public static ResponseSuccess getResponseSuccessDto(Object data, String message) {
        ResponseSuccess responseSuccessDto = getResponseSuccessDto(message);
        responseSuccessDto.setData(data);
        return responseSuccessDto;
    }

    /**
     * Generate response success when response doesn't need to return the data
     *
     * @param message
     * @return
     */
    public static ResponseSuccess getResponseSuccessDto(String message) {
        ResponseSuccess responseSuccessDto = new ResponseSuccess();
        responseSuccessDto.setMessage(message);
        responseSuccessDto.setStatusCode(200);
        return responseSuccessDto;
    }
}
