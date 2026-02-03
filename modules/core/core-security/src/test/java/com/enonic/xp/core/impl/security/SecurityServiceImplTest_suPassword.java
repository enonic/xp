package com.enonic.xp.core.impl.security;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest_suPassword
{
    @Mock
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private SecurityAuditLogSupport auditLogSupport;

    @Test
    void authenticate_su_correct_password()
    {
        final String password = "superSecret123";
        final SecurityServiceImpl securityService = createSecurityService( password );

        final UsernamePasswordAuthToken authToken =
            new UsernamePasswordAuthToken( IdProviderKey.system(), PrincipalKey.ofSuperUser().getId(), password );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );

        assertThat( authInfo.isAuthenticated() ).isTrue();
        assertThat( authInfo.getUser().getKey() ).isEqualTo( PrincipalKey.ofSuperUser() );
        assertThat( authInfo.getPrincipals() ).containsExactlyInAnyOrder( PrincipalKey.ofSuperUser(), RoleKeys.ADMIN, RoleKeys.AUTHENTICATED,
                                                                          RoleKeys.EVERYONE );
        verifyNoInteractions( nodeService );
    }

    @Test
    void authenticate_su_wrong_password()
    {
        final String password = "superSecret123";
        final SecurityServiceImpl securityService = createSecurityService( password );

        final UsernamePasswordAuthToken authToken =
            new UsernamePasswordAuthToken( IdProviderKey.system(), PrincipalKey.ofSuperUser().getId(), "wrongPassword" );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );

        assertThat( authInfo.isAuthenticated() ).isFalse();
        verifyNoInteractions( nodeService );
    }

    @Test
    void authenticate_su_empty_password()
    {
        final String password = "superSecret123";
        final SecurityServiceImpl securityService = createSecurityService( password );

        final UsernamePasswordAuthToken authToken =
            new UsernamePasswordAuthToken( IdProviderKey.system(), PrincipalKey.ofSuperUser().getId(), "" );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );

        assertThat( authInfo.isAuthenticated() ).isFalse();
        verifyNoInteractions( nodeService );
    }

    @Test
    void authenticate_su_no_configured_password()
    {
        final SuPasswordVerifier suPasswordVerifier = new SuPasswordVerifier( "" );
        final PasswordSecurityService passwordSecurityService = createPasswordSecurityService( suPasswordVerifier );
        final SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService );

        final UsernamePasswordAuthToken authToken =
            new UsernamePasswordAuthToken( IdProviderKey.system(), PrincipalKey.ofSuperUser().getId(), "anyPassword" );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );

        assertThat( authInfo.isAuthenticated() ).isFalse();
        verifyNoInteractions( nodeService );
    }

    @Test
    void authenticate_su_no_configured_empty_password()
    {
        final SuPasswordVerifier suPasswordVerifier = new SuPasswordVerifier( "" );
        final PasswordSecurityService passwordSecurityService = createPasswordSecurityService( suPasswordVerifier );
        final SecurityServiceImpl securityService = new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService );

        final UsernamePasswordAuthToken authToken =
            new UsernamePasswordAuthToken( IdProviderKey.system(), PrincipalKey.ofSuperUser().getId(), "" );

        final AuthenticationInfo authInfo = securityService.authenticate( authToken );

        assertThat( authInfo.isAuthenticated() ).isFalse();
        verifyNoInteractions( nodeService );
    }

    private SecurityServiceImpl createSecurityService( final String suPassword )
    {
        final String hash = computeSha256Hash( suPassword.toCharArray() );
        final SuPasswordVerifier suPasswordVerifier = new SuPasswordVerifier( "{sha256}" + hash );
        final PasswordSecurityService passwordSecurityService = createPasswordSecurityService( suPasswordVerifier );
        return new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService );
    }

    private PasswordSecurityService createPasswordSecurityService( final SuPasswordVerifier suPasswordVerifier )
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService( suPasswordVerifier );
        passwordSecurityService.activate( securityConfig );
        return passwordSecurityService;
    }

    private static String computeSha256Hash( final char[] password )
    {
        final MessageDigest digest = MessageDigests.sha256();
        digest.update( StandardCharsets.UTF_8.encode( CharBuffer.wrap( password ) ) );
        return HexFormat.of().formatHex( digest.digest() );
    }
}
