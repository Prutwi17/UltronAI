package com.ultronai.security;

import com.ultronai.model.entity.Conversation;
import com.ultronai.model.entity.Tenant;
import com.ultronai.model.entity.User;
import com.ultronai.model.enums.Role;
import com.ultronai.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageChannel messageChannel;

    @InjectMocks
    private StompAuthChannelInterceptor interceptor;

    private UserPrincipal adminPrincipal;
    private UserPrincipal customerPrincipal;
    private User customerUser;
    private Tenant testTenant;
    private Conversation testConv;

    @BeforeEach
    void setUp() {
        adminPrincipal = new UserPrincipal(1L, 1L, "Admin", "admin@acme.com", "hash", Role.TENANT_ADMIN, true);
        customerPrincipal = new UserPrincipal(10L, 1L, "Customer", "cust@acme.com", "hash", Role.CUSTOMER, true);

        testTenant = new Tenant("Acme", "acme");
        testTenant.setId(1L);

        customerUser = new User(testTenant, "Customer", "cust@acme.com", "hash", Role.CUSTOMER);
        customerUser.setId(10L);

        testConv = new Conversation(testTenant, customerUser, "WEB");
        testConv.setId(100L);
    }

    @Test
    void testConnectWithValidJwtSuccess() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        accessor.addNativeHeader("Authorization", "Bearer valid.jwt.token");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(tokenProvider.validateToken("valid.jwt.token")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("valid.jwt.token")).thenReturn("admin@acme.com");
        when(userDetailsService.loadUserByUsername("admin@acme.com")).thenReturn(adminPrincipal);

        Message<?> result = interceptor.preSend(message, messageChannel);

        assertNotNull(result);
        StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);
        assertNotNull(resultAccessor.getUser());
        assertEquals("admin@acme.com", resultAccessor.getUser().getName());
    }

    @Test
    void testConnectWithInvalidJwtRejection() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.addNativeHeader("Authorization", "Bearer invalid.jwt.token");
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        when(tokenProvider.validateToken("invalid.jwt.token")).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, messageChannel));
    }

    @Test
    void testSubscribeSameTenantTopicSuccess() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/tenants/1/conversations/100");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities());
        accessor.setUser(auth);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> result = interceptor.preSend(message, messageChannel);
        assertNotNull(result);
    }

    @Test
    void testSubscribeCrossTenantTopicRejection() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination("/topic/tenants/2/conversations/100"); // User tenant is 1, topic is tenant 2
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities());
        accessor.setUser(auth);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThrows(AccessDeniedException.class, () -> interceptor.preSend(message, messageChannel));
    }
}
