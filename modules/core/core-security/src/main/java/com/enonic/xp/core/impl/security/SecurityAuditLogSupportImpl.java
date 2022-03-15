package com.enonic.xp.core.impl.security;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.security")
public class SecurityAuditLogSupportImpl
    implements SecurityAuditLogSupport
{
    private static final String SOURCE = "com.enonic.xp.security";

    private final AuditLogService auditLogService;

    private volatile boolean isEnabledAuditLogs = true;

    @Activate
    public SecurityAuditLogSupportImpl( @Reference final AuditLogService auditLogService )
    {
        this.auditLogService = auditLogService;
    }

    @Activate
    @Modified
    public void activate( final SecurityConfig config )
    {
        this.isEnabledAuditLogs = config.auditlog_enabled();
    }

    @Override
    public void createUser( final CreateUserParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "email", params.getEmail() );
            paramsSet.setString( "login", params.getLogin() );
            paramsSet.setString( "displayName", params.getDisplayName() );

            log( "system.security.principal.create", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void updateUser( final UpdateUserParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "email", params.getEmail() );
            paramsSet.setString( "login", params.getLogin() );
            paramsSet.setString( "displayName", params.getDisplayName() );

            log( "system.security.principal.update", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createGroup( final CreateGroupParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.principal.create", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void updateGroup( UpdateGroupParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.principal.update", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createRole( final CreateRoleParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.principal.create", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void updateRole( final UpdateRoleParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.principal.update", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createIdProvider( final CreateIdProviderParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.idProvider.create", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void updateIdProvider( final UpdateIdProviderParams params )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", params.getKey().toString() );
            paramsSet.setString( "displayName", params.getDisplayName() );
            paramsSet.setString( "description", params.getDescription() );

            log( "system.security.idProvider.update", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void removeIdProvider( final IdProviderKey idProviderKey )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", idProviderKey.toString() );

            log( "system.security.idProvider.delete", data, AuditLogUris.from( idProviderKey.toString() ) );
        }
    }

    @Override
    public void removePrincipal( final PrincipalKey principalKey )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", principalKey.toString() );

            log( "system.security.principal.delete", data, AuditLogUris.from( principalKey.toString() ) );
        }
    }

    @Override
    public void addRelationship( final PrincipalRelationship relationship )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "principal", relationship.getTo().toString() );
            paramsSet.setString( "joins", relationship.getFrom().toString() );

            AuditLogUris auditLogUris = AuditLogUris.from( relationship.getTo().toString(), relationship.getFrom().toString() );

            log( "system.security.principal.addRelationship", data, auditLogUris );
        }
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "principal", relationship.getTo().toString() );
            paramsSet.setString( "leaves", relationship.getFrom().toString() );

            AuditLogUris auditLogUris = AuditLogUris.from( relationship.getTo().toString(), relationship.getFrom().toString() );

            log( "system.security.principal.removeRelationship", data, auditLogUris );
        }
    }

    @Override
    public void removeRelationships( final PrincipalKey key )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "from", key.toString() );

            log( "system.security.principal.removeRelationships", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void setPassword( final PrincipalKey key )
    {
        if ( isEnabledAuditLogs )
        {
            PropertyTree data = new PropertyTree();
            PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "key", key.toString() );

            log( "system.security.principal.setPassword", data, AuditLogUris.from( key.toString() ) );
        }
    }

    private void log( final String type, final PropertyTree data, final AuditLogUris uris )
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final PrincipalKey userPrincipalKey =
            context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        ContextBuilder.from( context ).
            authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).
                principals( RoleKeys.AUDIT_LOG ).build() ).
            build().
            callWith( () -> auditLogService.log( LogAuditLogParams.create().
                type( type ).
                source( SOURCE ).
                data( data ).
                objectUris( uris ).
                user( userPrincipalKey ).
                build() ) );
    }
}
