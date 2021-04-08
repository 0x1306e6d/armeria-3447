/*
 * MIT License
 *
 * Copyright (c) 2021 Gihwan Kim
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.gihwan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.Server;

@SpringBootTest(
        classes = {
                ArmeriaServerConfiguration.class,
                SimpleController.class
        },
        webEnvironment = WebEnvironment.DEFINED_PORT)
@EnableAutoConfiguration
class SimpleControllerTest {

    @Autowired
    Server server;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void foo() {
        final WebClient client = WebClient.of("http://127.0.0.1:" + server.activeLocalPort());
        final AggregatedHttpResponse res = client.get("/foo").aggregate().join();
        assertThat(res.status()).isEqualTo(HttpStatus.OK);
        assertThat(res.contentUtf8()).isEqualTo("Hello, World!");
    }

    @Test
    void bar() {
        final WebClient client = WebClient.of("http://127.0.0.1:" + server.activeLocalPort());
        final AggregatedHttpResponse res = client.get("/bar").aggregate().join();
        // Received 500 Internal Server Error well.
        assertThat(res.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // Fail to deserialize JSON because the res didn't receive any content from the server.
        // The Spring Boot application can't handle error the “global” error page in the servlet container
        // described in the Error Handling document. Please refer to
        // https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-error-handling
        try {
            @SuppressWarnings("rawtypes")
            final Map content = objectMapper.readValue(res.contentUtf8(), Map.class);
            //noinspection unchecked
            assertThat(content)
                    .containsEntry("status", 500)
                    .containsEntry("error", "Internal Server Error");
        } catch (JsonProcessingException e) {
            fail("Fail to deserialize JSON.");
        }
    }

    @Test
    void baz() {
        final WebClient client = WebClient.of("http://127.0.0.1:" + server.activeLocalPort());
        final AggregatedHttpResponse res = client.get("/baz").aggregate().join();
        // Received 500 Internal Server Error well.
        assertThat(res.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        // Fail to deserialize JSON because the res didn't receive any content from the server.
        // The Spring Boot application can't handle error the “global” error page in the servlet container
        // described in the Error Handling document. Please refer to
        // https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-error-handling
        try {
            final CustomErrorMessage body = objectMapper.readValue(res.contentUtf8(), CustomErrorMessage.class);
            assertThat(body.getErrorStatusCode()).isEqualTo(500);
            assertThat(body.getErrorMessage()).isEqualTo("a custom error occurred");
        } catch (JsonProcessingException e) {
            fail("Fail to deserialize JSON.");
        }
    }
}
