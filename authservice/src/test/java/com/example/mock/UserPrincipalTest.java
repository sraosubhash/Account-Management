package com.example.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.example.authservice.model.User;
import com.example.authservice.model.UserRole;
import com.example.authservice.service.UserPrincipal;
 class UserPrincipalTest {

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .role(UserRole.USER)
                .build();

        userPrincipal = UserPrincipal.create(user);
    }

    /**
     * Test for creating UserPrincipal from a User entity
     */
    @Test
    void testCreateUserPrincipal() {
        // Assert
        assertNotNull(userPrincipal);
        assertEquals(user.getId(), userPrincipal.getId());
        assertEquals(user.getEmail(), userPrincipal.getUsername());
        assertEquals(user.getPassword(), userPrincipal.getPassword());

        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    /**
     * Test getUsername method
     */
    @Test
    void testGetUsername() {
        assertEquals("test@example.com", userPrincipal.getUsername());
    }

    /**
     * Test getPassword method
     */
    @Test
    void testGetPassword() {
        assertEquals("password123", userPrincipal.getPassword());
    }

    /**
     * Test getAuthorities method
     */
    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
    }

    /**
     * Test isAccountNonExpired method
     */
    @Test
    void testIsAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    /**
     * Test isAccountNonLocked method
     */
    @Test
    void testIsAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    /**
     * Test isCredentialsNonExpired method
     */
    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    /**
     * Test isEnabled method
     */
    @Test
    void testIsEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }

    /**
     * Test equals() method
     */
    @Test
    void testEquals() {
        UserPrincipal sameUserPrincipal = UserPrincipal.create(user);
        UserPrincipal differentUserPrincipal = new UserPrincipal(2L, "other@example.com", "password", Collections.emptyList());

        assertEquals(userPrincipal, sameUserPrincipal);
        assertNotEquals(userPrincipal, differentUserPrincipal);
    }

    /**
     * Test hashCode() method
     */
    @Test
    void testHashCode() {
        UserPrincipal sameUserPrincipal = UserPrincipal.create(user);
        assertEquals(userPrincipal.hashCode(), sameUserPrincipal.hashCode());
    }
}
