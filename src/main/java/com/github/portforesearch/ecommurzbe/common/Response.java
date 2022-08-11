package com.github.portforesearch.ecommurzbe.common;

import com.github.portforesearch.ecommurzbe.dto.ResponseSuccessDto;
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
    public static ResponseSuccessDto getResponseSuccessDto(Object data, String message) {
        ResponseSuccessDto responseSuccessDto = getResponseSuccessDto(message);
        responseSuccessDto.setData(data);
        return responseSuccessDto;
    }

    /**
     * Generate response success when response doesn't need to return the data
     *
     * @param message
     * @return
     */
    public static ResponseSuccessDto getResponseSuccessDto(String message) {
        ResponseSuccessDto responseSuccessDto = new ResponseSuccessDto();
        responseSuccessDto.setMessage(message);
        responseSuccessDto.setStatusCode(200);
        return responseSuccessDto;
    }
}
