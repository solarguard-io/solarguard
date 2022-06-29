package org.silentsoft.solarguard.core.interceptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"rate-limit.enabled=true", "rate-limit.max-requests-per-ip=5", "rate-limit.reset-interval-in-seconds=1"})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class RateLimitInterceptorTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithUserDetails
    public void rateLimitTest() throws Exception {
        request();
        Thread.sleep(1000);
        request();
    }

    private void request() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(header().string("X-Rate-Limit", "5"))
                .andExpect(header().string("X-Rate-Limit-Remaining", "4"))
                .andDo(result -> {
                    long reset = Long.valueOf(result.getResponse().getHeader("X-Rate-Limit-Reset"));
                    long diff = reset - System.currentTimeMillis();
                    Assertions.assertTrue(diff >= 0 && diff <= 1000);
                })
                .andExpect(status().isOk());

        for (int i = 0; i < 3; i++) {
            mvc.perform(get("/api/users")).andExpect(status().isOk());
        }

        mvc.perform(get("/api/users"))
                .andExpect(header().string("X-Rate-Limit", "5"))
                .andExpect(header().string("X-Rate-Limit-Remaining", "0"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/users")).andExpect(status().isTooManyRequests());
    }

}
