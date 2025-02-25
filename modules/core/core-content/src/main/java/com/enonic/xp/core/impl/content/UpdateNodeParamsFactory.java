package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteService;

public class UpdateNodeParamsFactory
{
    private final Content editedContent;

    private final CreateAttachments createAttachments;

    private final ContentTypeService contentTypeService;

    private final XDataService xDataService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final SiteService siteService;

    private final ContentDataSerializer contentDataSerializer;

    private final Branches branches;

    public UpdateNodeParamsFactory( final Builder builder )
    {
        this.editedContent = builder.editedContent;
        this.createAttachments = builder.createAttachments;
        this.contentTypeService = builder.contentTypeService;
        this.xDataService = builder.xDataService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.contentDataSerializer = builder.contentDataSerializer;
        this.siteService = builder.siteService;
        branches = Branches.from( builder.branches.build() );
    }

    public UpdateNodeParams produce()
    {
        final NodeEditor nodeEditor = toNodeEditor();

        final UpdateNodeParams.Builder builder = UpdateNodeParams.create()
            .id( NodeId.from( editedContent.getId() ) )
            .editor( nodeEditor )
            .addBranches( branches )
            .refresh( RefreshMode.ALL );

        for ( final CreateAttachment createAttachment : createAttachments )
        {
            builder.attachBinary( createAttachment.getBinaryReference(), createAttachment.getByteSource() );
        }
        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    private NodeEditor toNodeEditor()
    {
        final PropertyTree nodeData = contentDataSerializer.toNodeData( editedContent );

        final ContentIndexConfigFactory indexConfigFactory = ContentIndexConfigFactory.create().
            contentTypeService( contentTypeService ).
            pageDescriptorService( pageDescriptorService ).
            partDescriptorService( partDescriptorService ).
            layoutDescriptorService( layoutDescriptorService ).
            siteService( this.siteService ).
            xDataService( this.xDataService ).
            contentTypeName( editedContent.getType() ).
            page( editedContent.getPage() ).
            siteConfigs( editedContent.isSite() ? ( (Site) editedContent ).getSiteConfigs() : null ).
            extraDatas( editedContent.getAllExtraData() ).
            language( editedContent.getLanguage() != null ? editedContent.getLanguage().getLanguage() : null ).
            build();

        return editableNode -> {
            editableNode.indexConfigDocument = indexConfigFactory.produce();
            editableNode.data = nodeData;
            editableNode.manualOrderValue = editedContent.getManualOrderValue();
        };
    }

    public static class Builder
    {
        private Content editedContent;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private ContentTypeService contentTypeService;

        private XDataService xDataService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private ContentDataSerializer contentDataSerializer;

        private SiteService siteService;

        Builder editedContent( final Content editedContent )
        {
            this.editedContent = editedContent;
            return this;
        }

        Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        Builder branches( final Branches branches )
        {
            this.branches.addAll( branches );
            return this;
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

        Builder contentDataSerializer( final ContentDataSerializer contentDataSerializer )
        {
            this.contentDataSerializer = contentDataSerializer;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( editedContent, "editedContent cannot be null" );
            Preconditions.checkNotNull( editedContent.getModifier(), "modifier cannot be null" );
            Preconditions.checkNotNull( editedContent.getAttachments(), "attachments cannot be null" );
            Preconditions.checkNotNull( createAttachments, "createAttachments cannot be null" );

            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( xDataService );
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( partDescriptorService );
            Preconditions.checkNotNull( layoutDescriptorService );
            Preconditions.checkNotNull( contentDataSerializer );
        }

        public UpdateNodeParamsFactory build()
        {
            validate();
            return new UpdateNodeParamsFactory( this );
        }
    }
}


