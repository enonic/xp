package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.app")
public class ApplicationAuditLogSupportImpl
    implements ApplicationAuditLogSupport
{
    private static final String SOURCE = "com.enonic.xp.app";

    private final AuditLogService auditLogService;

    private volatile boolean isEnabledAuditLog = true;

    @Activate
    public ApplicationAuditLogSupportImpl( @Reference final AuditLogService auditLogService )
    {
        this.auditLogService = auditLogService;
    }

    @Activate
    @Modified
    public void activate( final AppConfig appConfig )
    {
        this.isEnabledAuditLog = appConfig.auditlog_enabled();
    }

    @Override
    public void startApplication( final ApplicationKey applicationKey )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();

            log( "system.application.start", data, AuditLogUris.from( applicationKey.toString() ) );
        }
    }

    @Override
    public void stopApplication( ApplicationKey applicationKey )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();

            log( "system.application.stop", data, AuditLogUris.from( applicationKey.toString() ) );
        }
    }

    @Override
    public void installApplication( final ApplicationKey applicationKey, final URL url )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "url", url.toString().replace( "?" + url.getQuery(), "" ) );

            log( "system.application.install", data, AuditLogUris.from( applicationKey.toString() ) );
        }
    }

    @Override
    public void installApplication( final ApplicationKey applicationKey )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();

            log( "system.application.install", data, AuditLogUris.from( applicationKey.toString() ) );
        }
    }

    @Override
    public void uninstallApplication( final ApplicationKey applicationKey )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();

            log( "system.application.uninstall", data, AuditLogUris.from( applicationKey.toString() ) );
        }
    }

    private void log( final String type, final PropertyTree data, final AuditLogUris uris )
    {
        final Context rootContext = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final PrincipalKey userPrincipalKey =
            rootContext.getAuthInfo().getUser() != null ? rootContext.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        ContextBuilder.from( rootContext ).
            authInfo( AuthenticationInfo.copyOf( rootContext.getAuthInfo() ).
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
