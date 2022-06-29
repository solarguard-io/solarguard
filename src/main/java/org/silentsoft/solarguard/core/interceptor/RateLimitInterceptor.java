package org.silentsoft.solarguard.core.interceptor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.silentsoft.solarguard.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String X_RATE_LIMIT = "X-Rate-Limit";
    private static final String X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    private static final String X_RATE_LIMIT_RESET = "X-Rate-Limit-Reset";

    @Value("${rate-limit.enabled}")
    private boolean enabled;

    @Value("${rate-limit.max-requests-per-ip}")
    private int maxRequestsPerIp;

    @Value("${rate-limit.reset-interval-in-seconds}")
    private int resetIntervalInSeconds;

    private LoadingCache<String, Ticket> cache;

    private LoadingCache<String, Ticket> getCache() {
        if (cache == null) {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofSeconds(resetIntervalInSeconds))
                    .build(key -> new Ticket(maxRequestsPerIp, System.currentTimeMillis() + Duration.ofSeconds(resetIntervalInSeconds).toMillis()));
        }
        return cache;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!enabled) {
            return true;
        }

        String ipAddress = HttpUtil.extractIpAddressFromRequest(request);
        Ticket ticket = getCache().get(ipAddress);

        response.addHeader(X_RATE_LIMIT, String.valueOf(maxRequestsPerIp));
        response.addHeader(X_RATE_LIMIT_RESET, String.valueOf(ticket.getReset()));

        if (ticket.isAvailable()) {
            ticket.use();
            response.addHeader(X_RATE_LIMIT_REMAINING, String.valueOf(ticket.getRemaining()));
            return true;
        } else {
            response.addHeader(X_RATE_LIMIT_REMAINING, "0");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
    }

    class Ticket {
        private int remaining;
        private long reset;

        public Ticket(int remaining, long reset) {
            this.remaining = remaining;
            this.reset = reset;
        }

        public int getRemaining() {
            return remaining;
        }

        public long getReset() {
            return reset;
        }

        public boolean isAvailable() {
            return remaining > 0;
        }

        public void use() {
            if (isAvailable()) {
                remaining--;
            }
        }
    }

}
