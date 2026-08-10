package com.ultronai.security;

import com.ultronai.model.enums.Role;
import com.ultronai.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(StompAuthChannelInterceptor.class);
    private static final Pattern TOPIC_PATTERN = Pattern.compile("^/topic/tenants/(\\d+)/conversations/(\\d+)$");

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ConversationRepository conversationRepository;

    public StompAuthChannelInterceptor(
        JwtTokenProvider tokenProvider,
        CustomUserDetailsService userDetailsService,
        ConversationRepository conversationRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                authenticateStompConnection(accessor);
            } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
                authorizeStompDestination(accessor);
            }
        }

        return message;
    }

    private void authenticateStompConnection(StompHeaderAccessor accessor) {
        String token = extractBearerToken(accessor);

        if (!StringUtils.hasText(token) || !tokenProvider.validateToken(token)) {
            logger.warn("STOMP connection rejected: Missing or invalid JWT token");
            throw new AccessDeniedException("STOMP Authentication Failed: Invalid or missing JWT token");
        }

        String email = tokenProvider.getEmailFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!userDetails.isEnabled()) {
            throw new AccessDeniedException("STOMP Authentication Failed: User account is disabled");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        accessor.setUser(auth);
        logger.info("STOMP authenticated successfully for user: {}", email);
    }

    private void authorizeStompDestination(StompHeaderAccessor accessor) {
        if (accessor.getUser() == null || !(accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth)) {
            throw new AccessDeniedException("Unauthorized STOMP frame: Unauthenticated principal");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String destination = accessor.getDestination();

        if (destination != null) {
            Matcher matcher = TOPIC_PATTERN.matcher(destination);
            if (matcher.matches()) {
                Long topicTenantId = Long.parseLong(matcher.group(1));
                Long topicConvId = Long.parseLong(matcher.group(2));

                // Enforce tenant boundary
                if (!topicTenantId.equals(principal.getTenantId())) {
                    logger.warn("STOMP access denied: User tenantId {} tried to access topic tenantId {}", principal.getTenantId(), topicTenantId);
                    throw new AccessDeniedException("Cross-tenant STOMP access prohibited");
                }

                // Enforce conversation authorization for CUSTOMER role
                if (principal.getRole() == Role.CUSTOMER) {
                    boolean isOwner = conversationRepository.findByIdAndTenantId(topicConvId, topicTenantId)
                        .map(conv -> conv.getUser().getId().equals(principal.getId()))
                        .orElse(false);
                    if (!isOwner) {
                        logger.warn("STOMP access denied: Customer user {} tried to access conversation {}", principal.getId(), topicConvId);
                        throw new AccessDeniedException("Unauthorized STOMP conversation access");
                    }
                }
            }
        }
    }

    private String extractBearerToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (!StringUtils.hasText(bearerToken)) {
            bearerToken = accessor.getFirstNativeHeader("token");
        }
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }
}
