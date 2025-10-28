package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.MixinService;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

import static com.google.common.base.Strings.nullToEmpty;

public class CreateNodeParamsFactory
{
    private static final String COMPONENTS = "components";

    private final CreateContentTranslatorParams params;

    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final CmsService cmsService;

    private final ContentDataSerializer contentDataSerializer;

    public CreateNodeParamsFactory( final Builder builder )
    {
        this.params = builder.params;
        this.contentTypeService = builder.contentTypeService;
        this.mixinService = builder.mixinService;
        this.cmsService = builder.cmsService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.contentDataSerializer = builder.contentDataSerializer;
    }

    public CreateNodeParams.Builder produce()
    {
        final PropertyTree contentAsData = contentDataSerializer.toCreateNodeData( params );

        final PropertySet extraDataSet = contentAsData.getPropertySet( PropertyPath.from( ContentPropertyNames.MIXIN_DATA ) );

        final String language = contentAsData.getString( PropertyPath.from( ContentPropertyNames.LANGUAGE ) );

        final SiteConfigs siteConfigs =
            SiteConfigsDataSerializer.fromData( contentAsData.getPropertySet( PropertyPath.from( ContentPropertyNames.DATA ) ) );

        final Page page = contentAsData.hasProperty( COMPONENTS ) ? contentDataSerializer.fromPageData( contentAsData.getRoot() ) : null;

        final Mixins extraData = extraDataSet != null ? contentDataSerializer.fromExtraData( extraDataSet ) : null;

        final ContentIndexConfigFactory.Builder indexConfigFactoryBuilder = ContentIndexConfigFactory.create()
            .contentTypeName( params.getType() )
            .siteConfigs( siteConfigs )
            .cmsService( cmsService )
            .mixinService( mixinService )
            .contentTypeService( contentTypeService );

        if ( page != null )
        {
            indexConfigFactoryBuilder.page( page )
                .pageDescriptorService( pageDescriptorService )
                .partDescriptorService( partDescriptorService )
                .layoutDescriptorService( layoutDescriptorService );
        }

        if ( extraData != null )
        {
            indexConfigFactoryBuilder.extraDatas( extraData );
        }

        if ( !nullToEmpty( language ).isBlank() )
        {
            indexConfigFactoryBuilder.language( language );
        }

        final IndexConfigDocument indexConfigDocument = indexConfigFactoryBuilder.build().produce();

        final CreateNodeParams.Builder builder = CreateNodeParams.create()
            .name( resolveNodeName( params.getName() ) )
            .parent( ContentNodeHelper.translateContentPathToNodePath( params.getParent() ) )
            .data( contentAsData )
            .indexConfigDocument( indexConfigDocument )
            .permissions( params.getPermissions() )
            .inheritPermissions( params.isInheritPermissions() )
            .childOrder( params.getChildOrder() )
            .nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        return builder;
    }

    private static NodeName resolveNodeName( final ContentName name )
    {
        if ( name.isUnnamed() && !name.hasUniqueness() )
        {
            return NodeName.from( ContentName.uniqueUnnamed() );
        }

        return NodeName.from( name );
    }

    public static Builder create( final CreateContentTranslatorParams params )
    {
        return new Builder( params );
    }

    public static class Builder
    {
        private final CreateContentTranslatorParams params;

        private ContentTypeService contentTypeService;

        private MixinService mixinService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private ContentDataSerializer contentDataSerializer;

        private CmsService cmsService;

        Builder( final CreateContentTranslatorParams params )
        {
            this.params = params;
        }

        Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        Builder mixinService( final MixinService value )
        {
            this.mixinService = value;
            return this;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        Builder cmsService( final CmsService value )
        {
            this.cmsService = value;
            return this;
        }

        Builder partDescriptorService( final PartDescriptorService value )
        {
            this.partDescriptorService = value;
            return this;
        }

        Builder layoutDescriptorService( final LayoutDescriptorService value )
        {
            this.layoutDescriptorService = value;
            return this;
        }

        Builder contentDataSerializer( final ContentDataSerializer value )
        {
            this.contentDataSerializer = value;
            return this;
        }

        void validate()
        {
            Objects.requireNonNull( params, "params cannot be null" );
            Objects.requireNonNull( contentTypeService );
            Objects.requireNonNull( pageDescriptorService );
            Objects.requireNonNull( cmsService );
            Objects.requireNonNull( mixinService );
            Objects.requireNonNull( contentDataSerializer );
        }

        public CreateNodeParamsFactory build()
        {
            validate();
            return new CreateNodeParamsFactory( this );
        }
    }

}
