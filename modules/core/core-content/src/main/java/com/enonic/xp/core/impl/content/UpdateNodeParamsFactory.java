package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.UpdateContentTranslatorParams;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteService;

public class UpdateNodeParamsFactory
{
    private final UpdateContentTranslatorParams params;

    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final SiteService siteService;

    private static final ContentDataSerializer CONTENT_DATA_SERIALIZER = new ContentDataSerializer();

    public UpdateNodeParamsFactory( final Builder builder )
    {
        this.params = builder.params;
        this.contentTypeService = builder.contentTypeService;
        this.mixinService = builder.mixinService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.siteService = builder.siteService;
    }

    public UpdateNodeParams produce()
    {
        final Content editedContent = params.getEditedContent();
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final NodeEditor nodeEditor = toNodeEditor( params );

        final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
            id( NodeId.from( editedContent.getId() ) ).
            editor( nodeEditor );

        if ( createAttachments != null )
        {
            for ( final CreateAttachment createAttachment : createAttachments )
            {
                builder.attachBinary( createAttachment.getBinaryReference(), createAttachment.getByteSource() );
            }
        }
        return builder.build();
    }

    public static Builder create( final UpdateContentTranslatorParams params )
    {
        return new Builder( params );
    }

    private NodeEditor toNodeEditor( final UpdateContentTranslatorParams params )
    {
        final Content content = params.getEditedContent();

        final PropertyTree nodeData = CONTENT_DATA_SERIALIZER.toUpdateNodeData( params );

        final ContentIndexConfigFactory indexConfigFactory = ContentIndexConfigFactory.create().
            contentTypeService( contentTypeService ).
            pageDescriptorService( pageDescriptorService ).
            partDescriptorService( partDescriptorService ).
            layoutDescriptorService( layoutDescriptorService ).
            siteService( this.siteService ).
            mixinService( this.mixinService ).
            contentTypeName( content.getType() ).
            page( content.getPage() != null ? content.getPage() : null ).
            siteConfigs( content.isSite() ? ( (Site) content ).getSiteConfigs() : null ).
            extraDatas( content.getAllExtraData()).
            build();

        return editableNode -> {
            editableNode.indexConfigDocument = indexConfigFactory.produce();
            editableNode.data = nodeData;
            editableNode.permissions = content.getPermissions();
            editableNode.inheritPermissions = content.inheritsPermissions();
        };
    }

    public static class Builder
    {
        private UpdateContentTranslatorParams params;

        private ContentTypeService contentTypeService;

        private MixinService mixinService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private SiteService siteService;

        Builder( final UpdateContentTranslatorParams params )
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

        Builder siteService( final SiteService value )
        {
            this.siteService = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( mixinService );
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( partDescriptorService );
            Preconditions.checkNotNull( layoutDescriptorService );
        }

        public UpdateNodeParamsFactory build()
        {
            validate();
            return new UpdateNodeParamsFactory( this );
        }
    }
}


