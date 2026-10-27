package com.enonic.xp.core.impl.security;

import java.util.Objects;

import java.util.List;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.UpdateClientParams;
import com.enonic.xp.security.token.UpdateScopeParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityAuditLogSupportImplTest
{
    private SecurityAuditLogSupportImpl instance;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        SecurityConfig config = mock( SecurityConfig.class );
        when( config.auditlog_enabled() ).thenReturn( true );

        auditLogService = mock( AuditLogService.class );

        instance = new SecurityAuditLogSupportImpl( auditLogService );
        instance.activate( config );
    }

    @Test
    void actorOfTheCallerReachesTheLog()
    {
        final PrincipalKey alice = PrincipalKey.from( "user:system:alice" );
        final PrincipalKey service = PrincipalKey.from( "user:system:automation" );
        final AuthenticationInfo acted =
            AuthenticationInfo.create().user( User.create().key( alice ).login( "alice" ).build() ).actor( service ).build();
        final AuthenticationInfo[] atLogTime = new AuthenticationInfo[1];
        when( auditLogService.log( any() ) ).thenAnswer( i -> {
            atLogTime[0] = ContextAccessor.current().getAuthInfo();
            return null;
        } );

        ContextBuilder.create().authInfo( acted ).build().runWith( () -> instance.createUser(
            CreateUserParams.create().userKey( PrincipalKey.from( "user:system:testUser" ) ).displayName( "d" ).login( "testUser" ).build() ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );
        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );
        assertEquals( alice, argumentCaptor.getValue().getUser() );
        assertEquals( null, argumentCaptor.getValue().getActor() );
        assertEquals( service, atLogTime[0].getActor(), "the serializer takes the actor from the context the writer logs under" );
    }

    @Test
    void testCreateUser()
    {
        instance.createUser( CreateUserParams.create()
                                 .userKey( PrincipalKey.from( "user:system:testUser" ) )
                                 .displayName( "displayName" )
                                 .email( "testUser@gmail.com" )
                                 .login( "testUser" )
                                 .password( "**********" )
                                 .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.create", value.getType() );
        assertEquals( "user:system:testUser", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void testUpdateUser()
    {
        instance.updateUser( UpdateUserParams.create()
                                 .userKey( PrincipalKey.from( "user:system:testUser" ) )
                                 .displayName( "displayName" )
                                 .email( "testUser@gmail.com" )
                                 .login( "testUser" )
                                 .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.update", value.getType() );
        assertEquals( "user:system:testUser", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void testCreateRole()
    {
        instance.createRole( CreateRoleParams.create()
                                 .roleKey( PrincipalKey.ofRole( "testRole" ) )
                                 .description( "testRole" )
                                 .displayName( "Test Role" )
                                 .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.create", value.getType() );
        assertEquals( "role:testRole", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void testUpdateRole()
    {
        instance.updateRole( UpdateRoleParams.create()
                                 .roleKey( PrincipalKey.ofRole( "testRole" ) )
                                 .description( "testRole" )
                                 .displayName( "Test Role" )
                                 .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.update", value.getType() );
    }

    @Test
    void testCreateGroup()
    {
        instance.createGroup( CreateGroupParams.create()
                                  .groupKey( PrincipalKey.ofGroup( IdProviderKey.from( "idProvider" ), "groupId" ) )
                                  .description( "description" )
                                  .displayName( "displayName" )
                                  .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.create", value.getType() );
    }

    @Test
    void testUpdateGroup()
    {
        instance.updateGroup( UpdateGroupParams.create()
                                  .groupKey( PrincipalKey.ofGroup( IdProviderKey.from( "idProvider" ), "groupId" ) )
                                  .description( "description" )
                                  .displayName( "displayName" )
                                  .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.update", value.getType() );
    }

    @Test
    void testRemovePrincipal()
    {
        instance.removePrincipal( PrincipalKey.ofUser( IdProviderKey.from( "system" ), "userId" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.delete", value.getType() );
        assertEquals( "user:system:userId", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
    }

    @Test
    void testCreateIdProvider()
    {
        instance.createIdProvider( CreateIdProviderParams.create()
                                       .key( IdProviderKey.from( "system" ) )
                                       .displayName( "displayName" )
                                       .description( "description" )
                                       .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.idProvider.create", value.getType() );
    }

    @Test
    void testUpdateIdProvider()
    {
        instance.updateIdProvider( UpdateIdProviderParams.create()
                                       .key( IdProviderKey.from( "system" ) )
                                       .displayName( "displayName" )
                                       .description( "description" )
                                       .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.idProvider.update", value.getType() );
    }

    @Test
    void testRemoveIdProvider()
    {
        instance.removeIdProvider( IdProviderKey.from( "system" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.idProvider.delete", value.getType() );
    }

    @Test
    void testAddRelationship()
    {
        instance.addRelationship(
            PrincipalRelationship.from( PrincipalKey.from( "role:roleId" ) ).to( PrincipalKey.from( "user:custom:userId" ) ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.addRelationship", value.getType() );
    }

    @Test
    void testRemoveRelationship()
    {
        instance.removeRelationship(
            PrincipalRelationship.from( PrincipalKey.from( "role:roleId" ) ).to( PrincipalKey.from( "user:custom:userId" ) ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.removeRelationship", value.getType() );
    }

    @Test
    void testRemoveRelationships()
    {
        instance.removeRelationships( PrincipalKey.from( "role:roleId" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.removeRelationships", value.getType() );
    }

    @Test
    void testSetPassword()
    {
        instance.setPassword( PrincipalKey.from( "user:system:userId" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService, times( 1 ) ).log( argumentCaptor.capture() );

        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.security.principal.setPassword", value.getType() );
    }

    @Test
    void scopeChangesAreLogged()
    {
        instance.createScope( CreateScopeParams.create()
                                  .idProvider( IdProviderKey.system() )
                                  .id( "content:read" )
                                  .roles( PrincipalKeys.from( "role:cms.cm.app" ) )
                                  .build() );
        instance.updateScope( UpdateScopeParams.create().idProvider( IdProviderKey.system() ).id( "content:read" ).editor( e -> e.required = true ).build() );
        instance.removeScope( IdProviderKey.system(), "content:read" );

        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );
        verify( auditLogService, times( 3 ) ).log( captor.capture() );
        assertEquals( List.of( "system.security.scope.create", "system.security.scope.update", "system.security.scope.delete" ),
                      captor.getAllValues().stream().map( LogAuditLogParams::getType ).toList() );
        final PropertySet params = captor.getAllValues().get( 0 ).getData().getSet( "params" );
        assertEquals( "system", params.getString( "idProvider" ) );
        assertEquals( "content:read", params.getString( "id" ) );
        assertEquals( "role:cms.cm.app", params.getString( "roles" ) );
        assertEquals( AuditLogUris.from( "system" ), captor.getAllValues().get( 2 ).getObjectUris() );
    }

    @Test
    void clientChangesAreLogged()
    {
        instance.createClient( CreateClientParams.create()
                                   .idProvider( IdProviderKey.system() )
                                   .id( "claude" )
                                   .grantTypes( List.of( "authorization_code" ) )
                                   .redirectUris( List.of( "https://claude.ai/callback" ) )
                                   .build() );
        instance.updateClient( UpdateClientParams.create().idProvider( IdProviderKey.system() ).id( "claude" ).editor( e -> e.enabled = false ).build() );
        instance.removeClient( IdProviderKey.system(), "claude" );

        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );
        verify( auditLogService, times( 3 ) ).log( captor.capture() );
        assertEquals( List.of( "system.security.client.create", "system.security.client.update", "system.security.client.delete" ),
                      captor.getAllValues().stream().map( LogAuditLogParams::getType ).toList() );
        final PropertySet params = captor.getAllValues().get( 0 ).getData().getSet( "params" );
        assertEquals( "claude", params.getString( "id" ) );
        assertEquals( "https://claude.ai/callback", params.getString( "redirectUris" ) );
        assertEquals( AuditLogUris.from( "system" ), captor.getAllValues().get( 2 ).getObjectUris() );
    }
}
