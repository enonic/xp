package com.enonic.xp.core.impl.app;

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
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(configurationPid = "com.enonic.xp.schema")
public class DynamicSchemaAuditLogSupportImpl
    implements DynamicSchemaAuditLogSupport
{
    private static final String SOURCE = "com.enonic.xp.schema";

    private final AuditLogService auditLogService;

    private volatile boolean isEnabledAuditLog = true;

    @Activate
    public DynamicSchemaAuditLogSupportImpl( @Reference final AuditLogService auditLogService )
    {
        this.auditLogService = auditLogService;
    }

    @Activate
    @Modified
    public void activate( final SchemaConfig config )
    {
        this.isEnabledAuditLog = config.auditlog_enabled();
    }

    @Override
    public void createComponent( final CreateDynamicComponentParams params, final DynamicComponentType type,
                                 final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.component.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateComponent( final UpdateDynamicComponentParams params, final DynamicComponentType type,
                                 final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", params.getKey().toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.component.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteComponent( final DescriptorKey key, final DynamicComponentType type )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );
            paramsSet.addString( "type", type.toString() );

            log( "system.schema.component.delete", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void setComponentIcon( final DescriptorKey key, final DynamicComponentType type, final String mimeType, final long size )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "mimeType", mimeType );
            paramsSet.addLong( "size", size );

            log( "system.schema.component.icon.set", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void deleteComponentIcon( final DescriptorKey key, final DynamicComponentType type )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );
            paramsSet.addString( "type", type.toString() );

            log( "system.schema.component.icon.delete", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void createContentSchema( final CreateDynamicContentSchemaParams params, final DynamicContentSchemaType type,
                                     final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", params.getName().toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.contentSchema.create", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void updateContentSchema( final UpdateDynamicContentSchemaParams params, final DynamicContentSchemaType type,
                                     final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", params.getName().toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "resource", params.getResource() );

            log( "system.schema.contentSchema.update", data, AuditLogUris.from( result.getResource().getKey().toString() ) );
        }
    }

    @Override
    public void deleteContentSchema( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", name.toString() );
            paramsSet.addString( "type", type.toString() );

            log( "system.schema.contentSchema.delete", data, AuditLogUris.from( name.toString() ) );
        }
    }

    @Override
    public void setContentSchemaIcon( final BaseSchemaName name, final DynamicContentSchemaType type, final String mimeType,
                                      final long size )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", name.toString() );
            paramsSet.addString( "type", type.toString() );
            paramsSet.addString( "mimeType", mimeType );
            paramsSet.addLong( "size", size );

            log( "system.schema.contentSchema.icon.set", data, AuditLogUris.from( name.toString() ) );
        }
    }

    @Override
    public void deleteContentSchemaIcon( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "name", name.toString() );
            paramsSet.addString( "type", type.toString() );

            log( "system.schema.contentSchema.icon.delete", data, AuditLogUris.from( name.toString() ) );
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
    public void deleteMacro( final MacroKey key )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );

            log( "system.schema.macro.delete", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void setMacroIcon( final MacroKey key, final String mimeType, final long size )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );
            paramsSet.addString( "mimeType", mimeType );
            paramsSet.addLong( "size", size );

            log( "system.schema.macro.icon.set", data, AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void deleteMacroIcon( final MacroKey key )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.addString( "key", key.toString() );

            log( "system.schema.macro.icon.delete", data, AuditLogUris.from( key.toString() ) );
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

    private void log( final String type, final PropertyTree data, final AuditLogUris uris )
    {
        final Context rootContext = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final PrincipalKey userPrincipalKey =
            rootContext.getAuthInfo().getUser() != null ? rootContext.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        ContextBuilder.from( rootContext )
            .authInfo( AuthenticationInfo.copyOf( rootContext.getAuthInfo() ).principals( RoleKeys.AUDIT_LOG ).build() )
            .build()
            .callWith( () -> auditLogService.log( LogAuditLogParams.create()
                                                      .type( type )
                                                      .source( SOURCE )
                                                      .data( data )
                                                      .objectUris( uris )
                                                      .user( userPrincipalKey )
                                                      .build() ) );
    }
}
