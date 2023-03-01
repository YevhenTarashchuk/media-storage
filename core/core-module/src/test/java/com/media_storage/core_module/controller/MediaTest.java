package com.media_storage.core_module.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.media_storage.core_data.model.response.FileDataResponse;
import com.media_storage.core_module.JsonAssertUtil;
import com.media_storage.core_module.config.TestPersistenceConfig;
import com.media_storage.core_module.entity.FileDataEntity;
import com.media_storage.core_module.repository.FileDataRepository;
import com.media_storage.media_client.MediaClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static com.media_storage.shared_data.constant.ExceptionConstants.FILE_DATA_NOT_FOUND;
import static com.media_storage.shared_data.constant.ExceptionConstants.USER_NOT_EXISTS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"server.port=0", "eureka.client.enabled=false"})
@ContextConfiguration(classes = {TestPersistenceConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor
@Transactional
class MediaTest {

    private static final String MEDIA_URL = "/v1/users/{userId}/media";
    private static final String FILE_DATA_URL = "/v1/users/{userId}/media/file-data";
    private static final String FILE_DATA_ID_URL = "/v1/users/{userId}/media/file-data/{fileDataId}";
    private static final String FILE_TYPE_PARAM = "fileTypes";
    private static final String SEARCH_PARAM = "search";
    private static final Long FILE_DATA_ID = 1L;
    private static final Long INVALID_ID = 0L;
    private static final Long USER_ID = 1L;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileDataRepository fileDataRepository;
    @Autowired
    private MediaClient mediaClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("classpath:data/sql/user/active_user.sql")
    void shouldUploadFile() throws Exception {
        MockMultipartFile file = buildFile();

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, MEDIA_URL, USER_ID)
                        .file(file)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/media/FileDataResponse.json",
                "id", "url"
        );

        Long fileDataId = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(),
                FileDataResponse.class
        ).getId();

        Optional<FileDataEntity> fileData = fileDataRepository.findById(fileDataId);

        Assertions.assertTrue(fileData.isPresent());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/media/FileDataEntity.json"),
                objectMapper.writeValueAsString(fileData.get()),
                "id", "url", "createdAt", "updatedAt"
        );

        Mockito.verify(mediaClient).uploadFile(eq(file), anyString());
    }

    @Test
    void shouldNotUploadFileThanNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, MEDIA_URL, USER_ID)
                        .file(buildFile())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(USER_NOT_EXISTS, USER_ID))));

        Mockito.verifyNoInteractions(mediaClient);
    }

    @Test
    @Sql(scripts = {
            "classpath:data/sql/user/active_user.sql",
            "classpath:data/sql/media/file_data.sql"
    })
    void shouldGetFileData() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(FILE_DATA_ID_URL, USER_ID, FILE_DATA_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                "data/json/media/FileDataResponse.json"
        );
    }

    @ParameterizedTest
    @Sql(scripts = {
            "classpath:data/sql/user/active_user.sql",
            "classpath:data/sql/media/file_data.sql"
    })
    @MethodSource("getIdParams")
    void shouldNotGetFileDataThanNotFound(Long userId, Long fileDataId) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(FILE_DATA_ID_URL, userId, fileDataId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(
                        "$.detail",
                        Matchers.containsString(String.format(FILE_DATA_NOT_FOUND, fileDataId, userId))
                ));
    }

    @ParameterizedTest
    @Sql(scripts = {
            "classpath:data/sql/user/active_user.sql",
            "classpath:data/sql/media/file_data.sql"
    })
    @MethodSource("getGetFilesDataParams")
    void shouldGetFilesData(String fileTypes, String search, String responsePath) throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(FILE_DATA_URL, USER_ID)
                        .param(FILE_TYPE_PARAM, fileTypes)
                        .param(SEARCH_PARAM, search)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        JsonAssertUtil.assertResultActionsAndJsonPageable(
                result,
                responsePath
        );
    }

    @SneakyThrows
    private static MockMultipartFile buildFile() {
        byte[] fileForSavingInBytes = IOUtils.toByteArray(new ClassPathResource("data/file/photo.jpeg").getInputStream());

        return new MockMultipartFile(
                "file",
                "data/file/photo.jpeg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileForSavingInBytes
        );
    }

    private static Stream<Arguments> getIdParams() {
        return Stream.of(
                Arguments.of(USER_ID, INVALID_ID),
                Arguments.of(INVALID_ID, FILE_DATA_ID)
        );
    }

    private static Stream<Arguments> getGetFilesDataParams() {
        return Stream.of(
                Arguments.of(
                        "AUDIO, VIDEO",
                        null,
                        "data/json/media/FileDataResponsesWithFilter.json"
                ),
                Arguments.of(
                        null,
                        null,
                        "data/json/media/FileDataResponses.json"

                ),
                Arguments.of(
                        "OTHER",
                        "APP",
                        "data/json/media/FileDataResponsesWithSearch.json"

                )
        );
    }
}
