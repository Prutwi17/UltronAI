package com.ultronai.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.util.Set;

@Component
public class SsrfProtectionGuard {

    private static final Logger logger = LoggerFactory.getLogger(SsrfProtectionGuard.class);

    private static final Set<String> BLOCKED_HOSTNAMES = Set.of(
        "metadata.google.internal",
        "instance-data"
    );

    private final boolean allowLocalIpInDev;

    public SsrfProtectionGuard(
        @Value("${ALLOW_LOCAL_IP_IN_DEV:true}") boolean allowLocalIpInDev
    ) {
        this.allowLocalIpInDev = allowLocalIpInDev;
    }

    public void validateUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new IllegalArgumentException("Target URL cannot be empty");
        }

        URI uri;
        try {
            uri = URI.create(urlString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI syntax: " + urlString);
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new SecurityException("SSRF Protection: Only HTTP and HTTPS protocols are allowed. Rejected scheme: " + scheme);
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Target URL missing valid host: " + urlString);
        }

        if (BLOCKED_HOSTNAMES.contains(host.toLowerCase())) {
            throw new SecurityException("SSRF Protection: Access to metadata hostname '" + host + "' is forbidden");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress addr : addresses) {
                if (isBlacklistedIp(addr)) {
                    throw new SecurityException("SSRF Protection: Target host '" + host + "' resolved to blacklisted IP " + addr.getHostAddress());
                }
            }
        } catch (SecurityException se) {
            throw se;
        } catch (Exception e) {
            logger.warn("DNS resolution check failed for host {}: {}", host, e.getMessage());
        }
    }

    private boolean isBlacklistedIp(InetAddress addr) {
        if (allowLocalIpInDev) {
            // Allow localhost during local dev testing if flag is set
            return false;
        }

        if (addr.isLoopbackAddress() || addr.isAnyLocalAddress() || addr.isLinkLocalAddress()) {
            return true;
        }

        byte[] ip = addr.getAddress();

        // IPv4 checks
        if (ip.length == 4) {
            int b0 = ip[0] & 0xFF;
            int b1 = ip[1] & 0xFF;

            // 127.0.0.0/8 (Loopback)
            if (b0 == 127) return true;
            // 10.0.0.0/8 (Class A Private)
            if (b0 == 10) return true;
            // 172.16.0.0/12 (Class B Private)
            if (b0 == 172 && (b1 >= 16 && b1 <= 31)) return true;
            // 192.168.0.0/16 (Class C Private)
            if (b0 == 192 && b1 == 168) return true;
            // 169.254.0.0/16 (Link Local / Cloud Metadata)
            if (b0 == 169 && b1 == 254) return true;
            // 0.0.0.0/8
            if (b0 == 0) return true;
        }

        return false;
    }
}
