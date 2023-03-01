package com.media_storage.shared.error_decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.shared_data.exception.ErrorResponse;
import com.media_storage.shared_data.exception.FeignClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try(InputStream inputStream = response.body().asInputStream()) {
            ErrorResponse errorResponse = objectMapper.readValue(inputStream, ErrorResponse.class);
            return new FeignClientException(errorResponse);
        } catch (IOException e) {
            return errorDecoder.decode(methodKey, response);
        }
    }
}
