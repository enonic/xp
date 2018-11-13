package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;

public class CreateNodeParamsFactory
{
    private static final String COMPONENTS = "components";

    private final CreateContentTranslatorParams params;

    private final ContentTypeService contentTypeService;

    private final XDataService xDataService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final SiteService siteService;

    private final ContentDataSerializer contentDataSerializer;

    public CreateNodeParamsFactory( final Builder builder )
    {
        this.params = builder.params;
        this.contentTypeService = builder.contentTypeService;
        this.xDataService = builder.xDataService;
        this.siteService = builder.siteService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.contentDataSerializer = builder.contentDataSerializer;
    }

    public CreateNodeParams produce()
    {
        final PropertyTree contentAsData = contentDataSerializer.toCreateNodeData( params );

        final PropertySet extraDataSet = contentAsData.getPropertySet( PropertyPath.from( ContentPropertyNames.EXTRA_DATA ) );

        final SiteConfigs siteConfigs = new SiteConfigsDataSerializer().fromProperties(
            contentAsData.getPropertySet( PropertyPath.from( ContentPropertyNames.DATA ) ) ).build();

        final Page page = contentAsData.hasProperty( COMPONENTS ) ? contentDataSerializer.fromPageData( contentAsData.getRoot() ) : null;

        final ExtraDatas extraData = extraDataSet != null ? contentDataSerializer.fromExtraData( extraDataSet ) : null;

        final ContentIndexConfigFactory.Builder indexConfigFactoryBuilder = ContentIndexConfigFactory.create().
            contentTypeName( params.getType() ).
            siteConfigs( siteConfigs ).
            siteService( siteService ).
            xDataService( xDataService ).
            contentTypeService( contentTypeService );

        if ( page != null )
        {
            indexConfigFactoryBuilder.
                page( page ).
                pageDescriptorService( pageDescriptorService ).
                partDescriptorService( partDescriptorService ).
                layoutDescriptorService( layoutDescriptorService );
        }

        if ( extraData != null )
        {
            indexConfigFactoryBuilder.extraDatas( extraData );
        }

        final IndexConfigDocument indexConfigDocument = indexConfigFactoryBuilder.build().produce();

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( ContentNodeHelper.translateContentParentToNodeParentPath( params.getParent() ) ).
            data( contentAsData ).
            indexConfigDocument( indexConfigDocument ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getChildOrder() ).
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        return builder.build();
    }

    private static String resolveNodeName( final ContentName name )
    {
        if ( name.isUnnamed() && !name.hasUniqueness() )
        {
            return ContentName.uniqueUnnamed().toString();
        }

        return name.toString();
    }

    public static Builder create( final CreateContentTranslatorParams params )
    {
        return new Builder( params );
    }

    public static class Builder
    {
        private CreateContentTranslatorParams params;

        private ContentTypeService contentTypeService;

        private XDataService xDataService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private ContentDataSerializer contentDataSerializer;

        private SiteService siteService;

        Builder( final CreateContentTranslatorParams params )
        {
            this.params = params;
        }

        Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        Builder xDataService( final XDataService value )
        {
            this.xDataService = value;
            return this;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        Builder siteService( final SiteService value )
        {
            this.siteService = value;
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
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( siteService );
            Preconditions.checkNotNull( xDataService );
            Preconditions.checkNotNull( contentDataSerializer );
        }

        public CreateNodeParamsFactory build()
        {
            validate();
            return new CreateNodeParamsFactory( this );
        }
    }

}
