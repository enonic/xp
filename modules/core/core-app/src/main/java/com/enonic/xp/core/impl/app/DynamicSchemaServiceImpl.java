package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.SchemaNodeLayout.Kind;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.CreateOrUpdatePhrasesParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

@Component(immediate = true)
public class DynamicSchemaServiceImpl
    implements DynamicSchemaService
{
    private static final long MAX_ICON_SIZE = 100 * 1024;

    private static final String PROPERTIES_EXTENSION = ".properties";

    private final DynamicResourceManager dynamicResourceManager;

    private final DynamicResourceParser dynamicResourceParser;

    private final DynamicSchemaAuditLogSupport auditLogSupport;

    private final EventPublisher eventPublisher;

    private final ApplicationGeneration applicationGeneration;

    @Activate
    public DynamicSchemaServiceImpl( @Reference final NodeService nodeService,
                                     @Reference final DynamicSchemaAuditLogSupport auditLogSupport,
                                     @Reference final EventPublisher eventPublisher,
                                     @Reference final ApplicationGeneration applicationGeneration )
    {
        this.dynamicResourceManager = new DynamicResourceManager( nodeService );
        this.dynamicResourceParser = new DynamicResourceParser();
        this.auditLogSupport = auditLogSupport;
        this.eventPublisher = eventPublisher;
        this.applicationGeneration = applicationGeneration;
    }

    // the generic methods of the 8.1 API dispatch by the type of the params to the method of the kind

    @Override
    @Deprecated
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( final CreateDynamicComponentParams params )
    {
        return doCreateOrUpdateComponent(
            CreateOrUpdateComponentParams.create().key( params.getKey() ).resource( params.getResource() ).build(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> updateComponent( final UpdateDynamicComponentParams params )
    {
        return doCreateOrUpdateComponent(
            CreateOrUpdateComponentParams.create().key( params.getKey() ).resource( params.getResource() ).build(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( final GetDynamicComponentParams params )
    {
        return doGetComponent( params.getKey(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( final ListDynamicComponentsParams params )
    {
        return doListComponents( params.getKey(), params.getType() );
    }

    @Override
    @Deprecated
    public boolean deleteComponent( final DeleteDynamicComponentParams params )
    {
        return doDeleteComponent( params.getKey(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( final CreateDynamicContentSchemaParams params )
    {
        return doCreateOrUpdateSchema(
            CreateOrUpdateContentSchemaParams.create().name( params.getName() ).resource( params.getResource() ).build(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( final UpdateDynamicContentSchemaParams params )
    {
        return doCreateOrUpdateSchema(
            CreateOrUpdateContentSchemaParams.create().name( params.getName() ).resource( params.getResource() ).build(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( final GetDynamicContentSchemaParams params )
    {
        requireSchemaName( params.getName(), params.getType() );
        return doGetSchema( params.getName(), params.getType() );
    }

    @Override
    @Deprecated
    public <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> listContentSchemas( final ListDynamicContentSchemasParams params )
    {
        return doListSchemas( params.getKey(), params.getType() );
    }

    @Override
    @Deprecated
    public boolean deleteContentSchema( final DeleteDynamicContentSchemaParams params )
    {
        requireSchemaName( params.getName(), params.getType() );
        return doDeleteSchema( params.getName(), params.getType() );
    }

    @Override
    public DynamicSchemaResult<PartDescriptor> createOrUpdatePart( final CreateOrUpdateComponentParams params )
    {
        return doCreateOrUpdateComponent( params, DynamicComponentType.PART );
    }

    @Override
    public DynamicSchemaResult<PartDescriptor> getPart( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.PART );
    }

    @Override
    public List<DynamicSchemaResult<PartDescriptor>> listParts( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.PART );
    }

    @Override
    public boolean deletePart( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.PART );
    }

    @Override
    public DynamicSchemaResult<LayoutDescriptor> createOrUpdateLayout( final CreateOrUpdateComponentParams params )
    {
        return doCreateOrUpdateComponent( params, DynamicComponentType.LAYOUT );
    }

    @Override
    public DynamicSchemaResult<LayoutDescriptor> getLayout( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public List<DynamicSchemaResult<LayoutDescriptor>> listLayouts( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public boolean deleteLayout( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.LAYOUT );
    }

    @Override
    public DynamicSchemaResult<PageDescriptor> createOrUpdatePage( final CreateOrUpdateComponentParams params )
    {
        return doCreateOrUpdateComponent( params, DynamicComponentType.PAGE );
    }

    @Override
    public DynamicSchemaResult<PageDescriptor> getPage( final DescriptorKey key )
    {
        return doGetComponent( key, DynamicComponentType.PAGE );
    }

    @Override
    public List<DynamicSchemaResult<PageDescriptor>> listPages( final ApplicationKey key )
    {
        return doListComponents( key, DynamicComponentType.PAGE );
    }

    @Override
    public boolean deletePage( final DescriptorKey key )
    {
        return doDeleteComponent( key, DynamicComponentType.PAGE );
    }

    @Override
    public DynamicSchemaResult<ContentType> createOrUpdateContentType( final CreateOrUpdateContentSchemaParams params )
    {
        return doCreateOrUpdateSchema( params, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public DynamicSchemaResult<ContentType> getContentType( final ContentTypeName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public List<DynamicSchemaResult<ContentType>> listContentTypes( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public boolean deleteContentType( final ContentTypeName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.CONTENT_TYPE );
    }

    @Override
    public DynamicSchemaResult<FormFragmentDescriptor> createOrUpdateFormFragment( final CreateOrUpdateContentSchemaParams params )
    {
        return doCreateOrUpdateSchema( params, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public DynamicSchemaResult<FormFragmentDescriptor> getFormFragment( final FormFragmentName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public List<DynamicSchemaResult<FormFragmentDescriptor>> listFormFragments( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public boolean deleteFormFragment( final FormFragmentName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.FORM_FRAGMENT );
    }

    @Override
    public DynamicSchemaResult<MixinDescriptor> createOrUpdateMixin( final CreateOrUpdateContentSchemaParams params )
    {
        return doCreateOrUpdateSchema( params, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public DynamicSchemaResult<MixinDescriptor> getMixin( final MixinName name )
    {
        return doGetSchema( name, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public List<DynamicSchemaResult<MixinDescriptor>> listMixins( final ApplicationKey key )
    {
        return doListSchemas( key, DynamicContentSchemaType.MIXIN );
    }

    @Override
    public boolean deleteMixin( final MixinName name )
    {
        return doDeleteSchema( name, DynamicContentSchemaType.MIXIN );
    }

    private <T extends ComponentDescriptor> DynamicSchemaResult<T> doCreateOrUpdateComponent( final CreateOrUpdateComponentParams params,
                                                                                            final DynamicComponentType type )
    {
        requireAdminRole();
        requireCmsDescriptor( params.getKey().getApplicationKey() );
        final Kind kind = kind( type );
        if ( !kind.hasIcon() && !( params.getIcon() instanceof IconUpdate.Keep ) )
        {
            throw new IllegalArgumentException( String.format( "icon is not supported for %s [%s]", type, params.getKey() ) );
        }

        final Stored<ComponentDescriptor> stored =
            store( params.getKey().getApplicationKey(), kind, params.getKey().getName(), params.getKey().toString(), params.getResource(),
                   params.getIcon(), text -> dynamicResourceParser.parseComponent( params.getKey(), type, text ) );

        final DynamicSchemaResult<T> result = new DynamicSchemaResult<>(
            (T) wrapDescriptor( stored.descriptor(), stored.resource().getTimestamp(), stored.icon() ), stored.resource() );

        if ( stored.created() )
        {
            auditLogSupport.createComponent( params, type, result );
        }
        else
        {
            auditLogSupport.updateComponent( params, type, result );
        }
        publishUpdated( params.getKey().getApplicationKey(), kind, params.getKey().toString(), stored.created() );

        return result;
    }

    private <T extends ComponentDescriptor> DynamicSchemaResult<T> doGetComponent( final DescriptorKey key, final DynamicComponentType type )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( key.getApplicationKey(), kind( type ), key.getName() );
        if ( node == null )
        {
            return null;
        }
        final Resource resource = DynamicResourceManager.toResource( key.getApplicationKey(), node );
        final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( key, type, resource.readString() );
        return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ),
                                          resource );
    }

    private <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> doListComponents( final ApplicationKey key,
                                                                                         final DynamicComponentType type )
    {
        requireAdminRole();

        return dynamicResourceManager.list( key, kind( type ) ).stream().map( node -> {
            final Resource resource = DynamicResourceManager.toResource( key, node );
            final DescriptorKey descriptorKey = DescriptorKey.from( key, SchemaNodeLayout.locate( node.name() ).name() );
            final ComponentDescriptor descriptor = dynamicResourceParser.parseComponent( descriptorKey, type, resource.readString() );
            return new DynamicSchemaResult<>( (T) wrapDescriptor( descriptor, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ),
                                              resource );
        } ).toList();
    }

    private boolean doDeleteComponent( final DescriptorKey key, final DynamicComponentType type )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.delete( key.getApplicationKey(), kind( type ), key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteComponent( key, type );
            publishDeleted( key.getApplicationKey(), kind( type ), key.toString() );
        }

        return deleted;
    }

    private <T extends BaseSchema<?>> DynamicSchemaResult<T> doCreateOrUpdateSchema( final CreateOrUpdateContentSchemaParams params,
                                                                                   final DynamicContentSchemaType type )
    {
        requireAdminRole();
        requireSchemaName( params.getName(), type );
        requireCmsDescriptor( params.getName().getApplicationKey() );

        final Stored<BaseSchema<?>> stored =
            store( params.getName().getApplicationKey(), kind( type ), params.getName().getLocalName(), params.getName().toString(),
                   params.getResource(), params.getIcon(), text -> dynamicResourceParser.parseSchema( params.getName(), type, text ) );

        final DynamicSchemaResult<T> result = new DynamicSchemaResult<>(
            (T) wrapSchema( stored.descriptor(), stored.resource().getTimestamp(), stored.icon() ), stored.resource() );

        if ( stored.created() )
        {
            auditLogSupport.createContentSchema( params, type, result );
        }
        else
        {
            auditLogSupport.updateContentSchema( params, type, result );
        }
        publishUpdated( params.getName().getApplicationKey(), kind( type ), params.getName().toString(), stored.created() );

        return result;
    }

    private <T extends BaseSchema<?>> DynamicSchemaResult<T> doGetSchema( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( name.getApplicationKey(), kind( type ), name.getLocalName() );
        if ( node == null )
        {
            return null;
        }
        final Resource resource = DynamicResourceManager.toResource( name.getApplicationKey(), node );
        final BaseSchema<?> schema = dynamicResourceParser.parseSchema( name, type, resource.readString() );
        return new DynamicSchemaResult<>( (T) wrapSchema( schema, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ), resource );
    }

    private <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> doListSchemas( final ApplicationKey key,
                                                                                final DynamicContentSchemaType type )
    {
        requireAdminRole();

        return dynamicResourceManager.list( key, kind( type ) ).stream().map( node -> {
            final Resource resource = DynamicResourceManager.toResource( key, node );
            final BaseSchemaName schemaName = getSchemaName( key, type, SchemaNodeLayout.locate( node.name() ).name() );
            final BaseSchema<?> schema = dynamicResourceParser.parseSchema( schemaName, type, resource.readString() );
            return new DynamicSchemaResult<T>( (T) wrapSchema( schema, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ),
                                               resource );
        } ).toList();
    }

    private boolean doDeleteSchema( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.delete( name.getApplicationKey(), kind( type ), name.getLocalName() );

        if ( deleted )
        {
            auditLogSupport.deleteContentSchema( name, type );
            publishDeleted( name.getApplicationKey(), kind( type ), name.toString() );
        }

        return deleted;
    }

    /**
     * The cms descriptor makes the application own a schema: the {@code cms} node is created with it, and the schemas can be
     * written from then on. An existing descriptor is updated in place.
     */
    @Override
    public DynamicSchemaResult<CmsDescriptor> createOrUpdateCms( final CreateOrUpdateCmsParams params )
    {
        requireAdminRole();
        requireApplicationNode( params.getKey() );

        final CmsDescriptor cmsDescriptor = dynamicResourceParser.parseCms( params.getKey(), params.getResource() );

        final boolean update = dynamicResourceManager.nodeExists( ApplicationRepoServiceImpl.cmsDescriptorNodePath( params.getKey() ) );
        final Node node = update
            ? dynamicResourceManager.upsert( params.getKey(), Kind.CMS, null, params.getResource(), IconUpdate.KEEP ).node()
            : dynamicResourceManager.createCms( params.getKey(), params.getResource() );
        final Resource resource = DynamicResourceManager.toResource( params.getKey(), node );

        final DynamicSchemaResult<CmsDescriptor> result = new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        if ( update )
        {
            auditLogSupport.updateCms( params, result );
        }
        else
        {
            auditLogSupport.createCms( params, result );
        }
        publishUpdated( params.getKey(), Kind.CMS, null, !update );

        return result;
    }

    @Override
    @Deprecated
    public DynamicSchemaResult<CmsDescriptor> updateCms( final UpdateDynamicCmsParams params )
    {
        return createOrUpdateCms( CreateOrUpdateCmsParams.create().key( params.getKey() ).resource( params.getResource() ).build() );
    }

    @Override
    public DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( final ApplicationKey key )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( key, Kind.CMS, null );
        if ( node == null )
        {
            return null;
        }
        final Resource resource = DynamicResourceManager.toResource( key, node );
        final CmsDescriptor cmsDescriptor = dynamicResourceParser.parseCms( key, resource.readString() );
        return new DynamicSchemaResult<>(
            CmsDescriptor.copyOf( cmsDescriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    /**
     * Removes the whole persisted schema of the application, the {@code cms} node with the schemas, styles, phrases and icons
     * below it: without its cms descriptor the application owns no schema.
     */
    @Override
    public boolean deleteCms( final ApplicationKey key )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.deleteCms( key );

        if ( deleted )
        {
            auditLogSupport.deleteCms( key );
            publishDeleted( key, Kind.CMS, null );
        }

        return deleted;
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> createOrUpdateStyles( final CreateOrUpdateStylesParams params )
    {
        requireAdminRole();
        requireCmsDescriptor( params.getKey() );

        final StyleDescriptor styles = dynamicResourceParser.parseStyles( params.getKey(), params.getResource() );

        final DynamicResourceManager.Stored stored =
            dynamicResourceManager.upsert( params.getKey(), Kind.STYLE, null, params.getResource(), IconUpdate.KEEP );
        final Resource resource = DynamicResourceManager.toResource( params.getKey(), stored.node() );

        final DynamicSchemaResult<StyleDescriptor> result = new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( styles ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );

        if ( stored.created() )
        {
            auditLogSupport.createStyles( params, result );
        }
        else
        {
            auditLogSupport.updateStyles( params, result );
        }
        publishUpdated( params.getKey(), Kind.STYLE, null, stored.created() );

        return result;
    }

    @Override
    @Deprecated
    public DynamicSchemaResult<StyleDescriptor> createStyles( final CreateDynamicStylesParams params )
    {
        return createOrUpdateStyles( CreateOrUpdateStylesParams.create().key( params.getKey() ).resource( params.getResource() ).build() );
    }

    @Override
    @Deprecated
    public DynamicSchemaResult<StyleDescriptor> updateStyles( final UpdateDynamicStylesParams params )
    {
        return createOrUpdateStyles( CreateOrUpdateStylesParams.create().key( params.getKey() ).resource( params.getResource() ).build() );
    }

    @Override
    public DynamicSchemaResult<StyleDescriptor> getStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( key, Kind.STYLE, null );
        if ( node == null )
        {
            return null;
        }
        final Resource resource = DynamicResourceManager.toResource( key, node );
        final StyleDescriptor descriptor = dynamicResourceParser.parseStyles( key, resource.readString() );
        return new DynamicSchemaResult<>(
            StyleDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( resource.getTimestamp() ) ).build(), resource );
    }

    @Override
    public boolean deleteStyles( final ApplicationKey key )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.delete( key, Kind.STYLE, null );

        if ( deleted )
        {
            auditLogSupport.deleteStyles( key );
            publishDeleted( key, Kind.STYLE, null );
        }

        return deleted;
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> createOrUpdateMacro( final CreateOrUpdateMacroParams params )
    {
        requireAdminRole();
        requireCmsDescriptor( params.getKey().getApplicationKey() );

        final Stored<MacroDescriptor> stored =
            store( params.getKey().getApplicationKey(), Kind.MACRO, params.getKey().getName(), params.getKey().toString(), params.getResource(),
                   params.getIcon(), text -> dynamicResourceParser.parseMacro( params.getKey(), text ) );

        final DynamicSchemaResult<MacroDescriptor> result =
            new DynamicSchemaResult<>( wrapMacro( stored.descriptor(), stored.resource().getTimestamp(), stored.icon() ), stored.resource() );

        if ( stored.created() )
        {
            auditLogSupport.createMacro( params, result );
        }
        else
        {
            auditLogSupport.updateMacro( params, result );
        }
        publishUpdated( params.getKey().getApplicationKey(), Kind.MACRO, params.getKey().toString(), stored.created() );

        return result;
    }

    @Override
    public DynamicSchemaResult<MacroDescriptor> getMacro( final MacroKey key )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( key.getApplicationKey(), Kind.MACRO, key.getName() );
        if ( node == null )
        {
            return null;
        }
        final Resource resource = DynamicResourceManager.toResource( key.getApplicationKey(), node );
        final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( key, resource.readString() );
        return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ), resource );
    }

    @Override
    public List<DynamicSchemaResult<MacroDescriptor>> listMacros( final ApplicationKey key )
    {
        requireAdminRole();

        return dynamicResourceManager.list( key, Kind.MACRO ).stream().map( node -> {
            final Resource resource = DynamicResourceManager.toResource( key, node );
            final MacroKey macroKey = MacroKey.from( key, SchemaNodeLayout.locate( node.name() ).name() );
            final MacroDescriptor descriptor = dynamicResourceParser.parseMacro( macroKey, resource.readString() );
            return new DynamicSchemaResult<>( wrapMacro( descriptor, resource.getTimestamp(), dynamicResourceManager.iconOf( node ) ), resource );
        } ).toList();
    }

    @Override
    public boolean deleteMacro( final MacroKey key )
    {
        requireAdminRole();

        final boolean deleted = dynamicResourceManager.delete( key.getApplicationKey(), Kind.MACRO, key.getName() );

        if ( deleted )
        {
            auditLogSupport.deleteMacro( key );
            publishDeleted( key.getApplicationKey(), Kind.MACRO, key.toString() );
        }

        return deleted;
    }

    @Override
    public Resource createOrUpdatePhrases( final CreateOrUpdatePhrasesParams params )
    {
        requireAdminRole();
        requireCmsDescriptor( params.getKey() );

        final String name = phrasesName( params.getName() );
        final DynamicResourceManager.Stored stored =
            dynamicResourceManager.upsert( params.getKey(), Kind.PHRASES, name, params.getResource(), IconUpdate.KEEP );
        final Resource resource = DynamicResourceManager.toResource( params.getKey(), stored.node() );

        if ( stored.created() )
        {
            auditLogSupport.createPhrases( params, resource );
        }
        else
        {
            auditLogSupport.updatePhrases( params, resource );
        }
        publishUpdated( params.getKey(), Kind.PHRASES, name, stored.created() );

        return resource;
    }

    @Override
    public Resource getPhrases( final GetDynamicPhrasesParams params )
    {
        requireAdminRole();

        final Node node = dynamicResourceManager.get( params.getKey(), Kind.PHRASES, phrasesName( params.getName() ) );
        return node != null ? DynamicResourceManager.toResource( params.getKey(), node ) : null;
    }

    @Override
    public List<Resource> listPhrases( final ApplicationKey key )
    {
        requireAdminRole();

        return dynamicResourceManager.list( key, Kind.PHRASES ).stream().map( node -> DynamicResourceManager.toResource( key, node ) ).toList();
    }

    @Override
    public boolean deletePhrases( final DeleteDynamicPhrasesParams params )
    {
        requireAdminRole();

        final String name = phrasesName( params.getName() );
        final boolean deleted = dynamicResourceManager.delete( params.getKey(), Kind.PHRASES, name );

        if ( deleted )
        {
            auditLogSupport.deletePhrases( params );
            publishDeleted( params.getKey(), Kind.PHRASES, name );
        }

        return deleted;
    }

    // the definition of the application changed: this node stops serving what it cached before the change, synchronously, so a
    // read following the write sees it; the other nodes of the cluster are told once per call, after the change is written
    private void publishUpdated( final ApplicationKey applicationKey, final Kind kind, final String name, final boolean created )
    {
        applicationGeneration.bump( applicationKey );
        eventPublisher.publish( ApplicationEvents.updated( applicationKey, kind.kindName(), name,
                                                           created ? ApplicationEvents.ACTION_CREATED : ApplicationEvents.ACTION_UPDATED ) );
    }

    private void publishDeleted( final ApplicationKey applicationKey, final Kind kind, final String name )
    {
        applicationGeneration.bump( applicationKey );
        eventPublisher.publish( ApplicationEvents.updated( applicationKey, kind.kindName(), name, ApplicationEvents.ACTION_DELETED ) );
    }

    private static Kind kind( final DynamicComponentType type )
    {
        return switch ( type )
        {
            case PART -> Kind.PART;
            case LAYOUT -> Kind.LAYOUT;
            case PAGE -> Kind.PAGE;
        };
    }

    private static Kind kind( final DynamicContentSchemaType type )
    {
        return switch ( type )
        {
            case CONTENT_TYPE -> Kind.CONTENT_TYPE;
            case FORM_FRAGMENT -> Kind.FORM_FRAGMENT;
            case MIXIN -> Kind.MIXIN;
        };
    }

    // a descriptor as stored, with the icon it has after the call
    private record Stored<D>(D descriptor, Resource resource, Icon icon, boolean created)
    {
    }

    /**
     * Stores a descriptor with its icon in one node operation. The descriptor is created when there is none, overwritten when a
     * resource is given and left as it is otherwise, so that the icon can be set or removed alone. The resource and the icon are
     * validated before anything is written.
     */
    private <D> Stored<D> store( final ApplicationKey applicationKey, final Kind kind, final String name, final String schemaId,
                                 final String resourceText, final IconUpdate iconUpdate, final Function<String, D> parser )
    {
        final Node existing = dynamicResourceManager.get( applicationKey, kind, name );

        if ( resourceText == null )
        {
            if ( existing == null )
            {
                throw new IllegalArgumentException( String.format( "resource is required to create [%s]", schemaId ) );
            }
            if ( iconUpdate instanceof IconUpdate.Keep )
            {
                throw new IllegalArgumentException( String.format( "resource or icon is required to update [%s]", schemaId ) );
            }
        }
        if ( iconUpdate instanceof IconUpdate.Replace replace )
        {
            validateIcon( replace.data(), replace.mimeType() );
        }

        final D descriptor =
            parser.apply( resourceText != null ? resourceText : existing.data().getString( SchemaNodePropertyNames.RESOURCE ) );

        final DynamicResourceManager.Stored stored = dynamicResourceManager.upsert( applicationKey, kind, name, resourceText, iconUpdate );
        final Icon icon = kind.hasIcon() ? dynamicResourceManager.iconOf( stored.node() ) : null;

        return new Stored<>( descriptor, DynamicResourceManager.toResource( applicationKey, stored.node() ), icon, stored.created() );
    }

    private static void validateIcon( final ByteSource data, final String mimeType )
    {
        if ( SchemaNodeLayout.iconReference( mimeType ) == null )
        {
            throw new IllegalArgumentException( String.format( "unsupported icon mime type: %s", mimeType ) );
        }

        final long size = iconSize( data );
        if ( size == 0 )
        {
            throw new IllegalArgumentException( "icon data is required" );
        }
        if ( size > MAX_ICON_SIZE )
        {
            throw new IllegalArgumentException( String.format( "icon size exceeds the maximum allowed %d bytes", MAX_ICON_SIZE ) );
        }
    }

    private static long iconSize( final ByteSource data )
    {
        try
        {
            return data.size();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    // the params carry a BaseSchemaName, so a name of another kind than the method implies is rejected up front
    private static void requireSchemaName( final BaseSchemaName name, final DynamicContentSchemaType type )
    {
        final Class<? extends BaseSchemaName> expected = switch ( type )
        {
            case CONTENT_TYPE -> ContentTypeName.class;
            case FORM_FRAGMENT -> FormFragmentName.class;
            case MIXIN -> MixinName.class;
        };
        if ( !expected.isInstance( name ) )
        {
            throw new IllegalArgumentException(
                String.format( "expected %s but got %s: %s", expected.getSimpleName(), name.getClass().getSimpleName(), name ) );
        }
    }

    private BaseSchemaName getSchemaName( final ApplicationKey applicationKey, final DynamicContentSchemaType type, final String name )
    {
        return switch ( type )
        {
            case CONTENT_TYPE -> ContentTypeName.from( applicationKey, name );
            case FORM_FRAGMENT -> FormFragmentName.from( applicationKey, name );
            case MIXIN -> MixinName.from( applicationKey, name );
        };
    }

    // the name of the phrases is the file name without the extension; the extension is accepted and dropped
    private static String phrasesName( final String name )
    {
        return name.endsWith( PROPERTIES_EXTENSION ) ? name.substring( 0, name.length() - PROPERTIES_EXTENSION.length() ) : name;
    }

    private static void requireAdminRole()
    {
        ApplicationHelper.requireSchemaAdminRole();
    }

    /**
     * Schemas are written for an application owning a schema only, the one whose cms descriptor ({@code cms/cms.yaml}) is persisted:
     * shipped by its bundle on install, or created through {@link #createOrUpdateCms}.
     */
    private void requireCmsDescriptor( final ApplicationKey key )
    {
        requireApplicationNode( key );
        if ( !dynamicResourceManager.nodeExists( ApplicationRepoServiceImpl.cmsDescriptorNodePath( key ) ) )
        {
            throw new IllegalArgumentException( String.format( "Application [%s] has no CMS descriptor", key ) );
        }
    }

    /**
     * The application node exists for a global application and for one created through the application descriptor service,
     * never for a local application.
     */
    private void requireApplicationNode( final ApplicationKey key )
    {
        if ( !dynamicResourceManager.nodeExists( ApplicationRepoServiceImpl.applicationNodePath( key ) ) )
        {
            throw new IllegalArgumentException( String.format( "Application [%s] is not installed", key ) );
        }
    }

    private ComponentDescriptor wrapDescriptor( final ComponentDescriptor componentDescriptor, final long modifiedTime, final Icon icon )
    {
        if ( componentDescriptor instanceof PageDescriptor )
        {
            return PageDescriptor.copyOf( (PageDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }
        if ( componentDescriptor instanceof PartDescriptor )
        {
            return PartDescriptor.copyOf( (PartDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .icon( icon )
                .build();
        }
        if ( componentDescriptor instanceof LayoutDescriptor )
        {
            return LayoutDescriptor.copyOf( (LayoutDescriptor) componentDescriptor )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .build();
        }

        throw new IllegalArgumentException( "unknown type of ComponentDescriptor: " + componentDescriptor.getKey() );
    }

    private BaseSchema<?> wrapSchema( final BaseSchema<?> baseSchema, final long modifiedTime, final Icon icon )
    {
        if ( baseSchema instanceof ContentType )
        {
            return ContentType.create( (ContentType) baseSchema ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).icon( icon ).build();
        }
        if ( baseSchema instanceof FormFragmentDescriptor )
        {
            return FormFragmentDescriptor.create( (FormFragmentDescriptor) baseSchema )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .icon( icon )
                .build();
        }
        if ( baseSchema instanceof MixinDescriptor )
        {
            return MixinDescriptor.create( (MixinDescriptor) baseSchema )
                .modifiedTime( Instant.ofEpochMilli( modifiedTime ) )
                .icon( icon )
                .build();
        }

        throw new IllegalArgumentException( "unknown type of BaseSchema: " + baseSchema.getName() );
    }

    private static MacroDescriptor wrapMacro( final MacroDescriptor descriptor, final long modifiedTime, final Icon icon )
    {
        return MacroDescriptor.copyOf( descriptor ).modifiedTime( Instant.ofEpochMilli( modifiedTime ) ).icon( icon ).build();
    }
}
