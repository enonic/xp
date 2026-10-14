package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

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
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.CreateOrUpdatePhrasesParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
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
    public void createComponent( final CreateOrUpdateComponentParams params, final DynamicComponentType type,
                                 final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.component.create", componentData( params, type ), resultUris( result ) );
        }
    }

    @Override
    public void updateComponent( final CreateOrUpdateComponentParams params, final DynamicComponentType type,
                                 final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.component.update", componentData( params, type ), resultUris( result ) );
        }
    }

    private static PropertyTree componentData( final CreateOrUpdateComponentParams params, final DynamicComponentType type )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "key", params.getKey().toString() );
        paramsSet.addString( "type", type.toString() );
        addResource( paramsSet, params.getResource() );
        addIcon( paramsSet, params.getIcon() );
        return data;
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
    public void createContentSchema( final CreateOrUpdateContentSchemaParams params, final DynamicContentSchemaType type,
                                     final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.contentSchema.create", contentSchemaData( params, type ), resultUris( result ) );
        }
    }

    @Override
    public void updateContentSchema( final CreateOrUpdateContentSchemaParams params, final DynamicContentSchemaType type,
                                     final DynamicSchemaResult<?> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.contentSchema.update", contentSchemaData( params, type ), resultUris( result ) );
        }
    }

    private static PropertyTree contentSchemaData( final CreateOrUpdateContentSchemaParams params, final DynamicContentSchemaType type )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "name", params.getName().toString() );
        paramsSet.addString( "type", type.toString() );
        addResource( paramsSet, params.getResource() );
        addIcon( paramsSet, params.getIcon() );
        return data;
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
    public void createCms( final CreateOrUpdateCmsParams params, final DynamicSchemaResult<CmsDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.cms.create", applicationData( params.getKey(), params.getResource() ), resultUris( result ) );
        }
    }

    @Override
    public void updateCms( final CreateOrUpdateCmsParams params, final DynamicSchemaResult<CmsDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.cms.update", applicationData( params.getKey(), params.getResource() ), resultUris( result ) );
        }
    }

    @Override
    public void deleteCms( final ApplicationKey key )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.cms.delete", applicationData( key, null ), AuditLogUris.from( key.toString() ) );
        }
    }

    @Override
    public void createStyles( final CreateOrUpdateStylesParams params, final DynamicSchemaResult<StyleDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.styles.create", applicationData( params.getKey(), params.getResource() ), resultUris( result ) );
        }
    }

    @Override
    public void updateStyles( final CreateOrUpdateStylesParams params, final DynamicSchemaResult<StyleDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.styles.update", applicationData( params.getKey(), params.getResource() ), resultUris( result ) );
        }
    }

    @Override
    public void deleteStyles( final ApplicationKey key )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.styles.delete", applicationData( key, null ), AuditLogUris.from( key.toString() ) );
        }
    }

    private static PropertyTree applicationData( final ApplicationKey key, final String resource )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "application", key.toString() );
        addResource( paramsSet, resource );
        return data;
    }

    @Override
    public void createMacro( final CreateOrUpdateMacroParams params, final DynamicSchemaResult<MacroDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.macro.create", macroData( params ), resultUris( result ) );
        }
    }

    @Override
    public void updateMacro( final CreateOrUpdateMacroParams params, final DynamicSchemaResult<MacroDescriptor> result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.macro.update", macroData( params ), resultUris( result ) );
        }
    }

    private static PropertyTree macroData( final CreateOrUpdateMacroParams params )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "key", params.getKey().toString() );
        addResource( paramsSet, params.getResource() );
        addIcon( paramsSet, params.getIcon() );
        return data;
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
    public void createApplicationDescriptor( final ApplicationKey key, final String resource, final String iconMimeType,
                                             final long iconSize )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.application.create", applicationDescriptorData( key, resource, iconMimeType, iconSize, false ),
                 applicationDescriptorUris( key ) );
        }
    }

    @Override
    public void updateApplicationDescriptor( final ApplicationKey key, final String resource, final String iconMimeType,
                                             final long iconSize, final boolean iconRemoved )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.application.update", applicationDescriptorData( key, resource, iconMimeType, iconSize, iconRemoved ),
                 applicationDescriptorUris( key ) );
        }
    }

    private static PropertyTree applicationDescriptorData( final ApplicationKey key, final String resource, final String iconMimeType,
                                                           final long iconSize, final boolean iconRemoved )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "application", key.toString() );
        paramsSet.addString( "resource", resource );
        if ( iconMimeType != null )
        {
            addIcon( paramsSet, iconMimeType, iconSize );
        }
        if ( iconRemoved )
        {
            paramsSet.addBoolean( "iconRemoved", true );
        }
        return data;
    }

    private static AuditLogUris applicationDescriptorUris( final ApplicationKey key )
    {
        return AuditLogUris.from( key + ":/" + SchemaResourcePaths.APP_DESCRIPTOR_NAME );
    }

    @Override
    public void createPhrases( final CreateOrUpdatePhrasesParams params, final Resource result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.phrases.create", phrasesData( params ), AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    @Override
    public void updatePhrases( final CreateOrUpdatePhrasesParams params, final Resource result )
    {
        if ( isEnabledAuditLog )
        {
            log( "system.schema.phrases.update", phrasesData( params ), AuditLogUris.from( result.getKey().toString() ) );
        }
    }

    private static PropertyTree phrasesData( final CreateOrUpdatePhrasesParams params )
    {
        final PropertyTree data = new PropertyTree();
        final PropertySet paramsSet = data.addSet( "params" );
        paramsSet.addString( "application", params.getKey().toString() );
        paramsSet.addString( "name", params.getName() );
        paramsSet.addString( "resource", params.getResource() );
        return data;
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

    // the resource is left out when the descriptor is kept and only the icon changes
    private static void addResource( final PropertySet paramsSet, final String resource )
    {
        if ( resource != null )
        {
            paramsSet.addString( "resource", resource );
        }
    }

    private static void addIcon( final PropertySet paramsSet, final IconUpdate iconUpdate )
    {
        switch ( iconUpdate )
        {
            case IconUpdate.Replace replace -> addIcon( paramsSet, replace.mimeType(), size( replace.data() ) );
            case IconUpdate.Remove remove -> paramsSet.addBoolean( "iconRemoved", true );
            case IconUpdate.Keep keep ->
            {
            }
        }
    }

    private static void addIcon( final PropertySet paramsSet, final String mimeType, final long size )
    {
        final PropertySet iconSet = paramsSet.addSet( "icon" );
        iconSet.addString( "mimeType", mimeType );
        iconSet.addLong( "size", size );
    }

    private static long size( final ByteSource data )
    {
        try
        {
            return data.size();
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static AuditLogUris resultUris( final DynamicSchemaResult<?> result )
    {
        return AuditLogUris.from( result.getResource().getKey().toString() );
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