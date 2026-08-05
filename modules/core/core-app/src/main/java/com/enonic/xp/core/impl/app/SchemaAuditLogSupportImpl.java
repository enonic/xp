package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.resource.CreateDynamicCmsParams;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(configurationPid = "com.enonic.xp.app")
public class SchemaAuditLogSupportImpl
    implements SchemaAuditLogSupport
{
    private static final String SOURCE = "com.enonic.xp.app";

    private final AuditLogService auditLogService;

    private volatile boolean isEnabledAuditLog = true;

    @Activate
    public SchemaAuditLogSupportImpl( @Reference final AuditLogService auditLogService )
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
    public void createComponent( final CreateDynamicComponentParams params, final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "type", params.getType().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.component.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateComponent( final UpdateDynamicComponentParams params, final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "type", params.getType().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.component.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteComponent( final DeleteDynamicComponentParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "type", params.getType().toString() );

            log( "system.schema.component.delete", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createContentSchema( final CreateDynamicContentSchemaParams params, final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", params.getName().toString() );
            paramsSet.addString( "type", params.getType().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.contentSchema.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateContentSchema( final UpdateDynamicContentSchemaParams params, final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", params.getName().toString() );
            paramsSet.addString( "type", params.getType().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.contentSchema.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteContentSchema( final DeleteDynamicContentSchemaParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", params.getName().toString() );
            paramsSet.addString( "type", params.getType().toString() );

            log( "system.schema.contentSchema.delete", data, AuditLogUris.from( params.getName().toString() ) );
        }
    }

    @Override
    public void createCms( final CreateDynamicCmsParams params, final DynamicSchemaResult<CmsDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.cms.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateCms( final UpdateDynamicCmsParams params, final DynamicSchemaResult<CmsDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.cms.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteCms( final ApplicationKey key )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", key.toString() );

            log( "system.schema.cms.delete", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void createStyles( final CreateDynamicStylesParams params, final DynamicSchemaResult<StyleDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.styles.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateStyles( final UpdateDynamicStylesParams params, final DynamicSchemaResult<StyleDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.styles.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteStyles( final ApplicationKey key )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", key.toString() );

            log( "system.schema.styles.delete", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void createMacro( final CreateDynamicMacroParams params, final DynamicSchemaResult<MacroDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.macro.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateMacro( final UpdateDynamicMacroParams params, final DynamicSchemaResult<MacroDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.macro.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteMacro( final DeleteDynamicMacroParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );

            log( "system.schema.macro.delete", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createPhrases( final CreateDynamicPhrasesParams params, final Resource result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "name", params.getName() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.phrases.create", data, AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    @Override
    public void updatePhrases( final UpdateDynamicPhrasesParams params, final Resource result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "name", params.getName() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.phrases.update", data, AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    @Override
    public void deletePhrases( final DeleteDynamicPhrasesParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "application", params.getKey().toString() );
            paramsSet.addString( "name", params.getName() );

            log( "system.schema.phrases.delete", data, AuditLogUris.from( params.getKey().toString() ) );
        }
    }

    @Override
    public void createNamespace( final CreateNamespaceParams params, final Namespace result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            if ( params.getDescription() != null )
            {
                paramsSet.addString( "description", params.getDescription() );
            }

            log( "system.schema.namespace.create", data, AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    @Override
    public void updateNamespace( final UpdateNamespaceParams params, final Namespace result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            if ( params.getDescription() != null )
            {
                paramsSet.addString( "description", params.getDescription() );
            }

            log( "system.schema.namespace.update", data, AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    @Override
    public void deleteNamespace( final ApplicationKey key )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );

            log( "system.schema.namespace.delete", data, AuditLogUris.from( key.toString() ) );
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
