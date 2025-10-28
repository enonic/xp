package com.enonic.xp.core.impl.security;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
