package com.media_storage.auth_core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonAssertUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String readJsonFromClassPath(String path) throws IOException {
        return objectMapper.readTree(new ClassPathResource(path).getURL()).toString();
    }

    public static void assertResultActionsAndJsonFile(ResultActions currentExtensions, String jsonFilePath, String... ignoredField) throws Exception {
        JsonComparator comparator = new JsonComparator();
        Arrays.stream(ignoredField).forEach(comparator::addIgnoredField);
        assertResultActionsAndJsonFile(currentExtensions, comparator, jsonFilePath);
    }

    public static void assertJsons(String expectedJson, String actualJson, String... ignoredField) throws JSONException {
        JsonComparator comparator = new JsonComparator();
        Arrays.stream(ignoredField).forEach(comparator::addIgnoredField);
        JSONAssert.assertEquals(expectedJson, actualJson, comparator.getComparator());
    }

    public static void assertResultActionsAndJsonPageable(ResultActions currentExtensions, String secondJson, String... ignoredField) throws Exception {
        String[] pageableIgnoredFields = getPageableIgnoreFields();

        String[] result = Arrays.copyOf(ignoredField, ignoredField.length + pageableIgnoredFields.length);
        System.arraycopy(pageableIgnoredFields, 0, result, ignoredField.length, pageableIgnoredFields.length);

        assertResultActionsAndJsonFile(currentExtensions, secondJson, result);
    }

    private static String[] getPageableIgnoreFields() {
        return new String[]{
                "pageable",
                "totalPages",
                "totalElements",
                "last",
                "numberOfElements",
                "first",
                "number",
                "sort",
                "size",
                "empty"
        };
    }

    public static void assertResultActionsAndJsonFile(ResultActions currentExtensions, JsonComparator customComparatorHelper, String jsonFilePath) throws Exception {
        String expectedJson = objectMapper.readTree(new ClassPathResource(jsonFilePath).getURL()).toString();
        String requestResponse = currentExtensions.andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, requestResponse, customComparatorHelper.getComparator());
    }

    public static class JsonComparator {
        private final List<Customization> customizationList;

        JsonComparator() {
            customizationList = new ArrayList<>();
        }

        CustomComparator getComparator() {
            return new CustomComparator(JSONCompareMode.STRICT, customizationList.toArray(new Customization[0]));
        }


        public JsonComparator addIgnoredField(String field) {
            customizationList.add(new Customization("[*]." + field, ignore()));
            customizationList.add(new Customization(field, ignore()));
            return this;
        }

        private ValueMatcher ignore() {
            return (o, t1) -> true;
        }
    }
}
