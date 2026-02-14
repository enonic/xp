package com.enonic.xp.core.impl.content;

import java.util.Objects;

import org.osgi.util.function.Function;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.SiteConfigsDataSerializer;

public class PatchNodeParamsFactory
{
    private final ContentId contentId;

    private final Function<Content, Content> contentEditor;

    private final CreateAttachments createAttachments;

    private final Attributes versionAttributes;

    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final CmsService cmsService;

    private final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

    private final Branches branches;

    public PatchNodeParamsFactory( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.contentEditor = builder.contentEditor;
        this.createAttachments = builder.createAttachments;
        this.contentTypeService = builder.contentTypeService;
        this.mixinService = builder.mixinService;
        this.pageDescriptorService = builder.pageDescriptorService;
        this.partDescriptorService = builder.partDescriptorService;
        this.layoutDescriptorService = builder.layoutDescriptorService;
        this.cmsService = builder.cmsService;
        this.versionAttributes = builder.versionAttributes;
        branches = Branches.from( builder.branches.build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PatchNodeParams produce()
    {
        final NodeEditor nodeEditor = toNodeEditor();

        final PatchNodeParams.Builder builder = PatchNodeParams.create()
            .id( NodeId.from( contentId ) )
            .editor( nodeEditor )
            .branches( branches )
            .versionAttributes( versionAttributes )
            .refresh( RefreshMode.ALL );

        for ( final CreateAttachment createAttachment : createAttachments )
        {
            builder.attachBinary( createAttachment.getBinaryReference(), createAttachment.getByteSource() );
        }
        return builder.build();
    }

    private NodeEditor toNodeEditor()
    {
        return editableNode -> {
            final Content editedContent = contentEditor.apply( ContentNodeTranslator.fromNode( editableNode.source ) );

            final PropertyTree nodeData = contentDataSerializer.toNodeData( editedContent );

            final ContentIndexConfigFactory indexConfigFactory = ContentIndexConfigFactory.create()
                .contentTypeService( contentTypeService )
                .pageDescriptorService( pageDescriptorService )
                .partDescriptorService( partDescriptorService )
                .layoutDescriptorService( layoutDescriptorService )
                .cmsService( this.cmsService )
                .mixinService( this.mixinService )
                .contentTypeName( editedContent.getType() )
                .page( editedContent.getPage() )
                .siteConfigs( editedContent.isSite() ? SiteConfigsDataSerializer.fromData( editedContent.getData().getRoot() ) : null )
                .mixins( editedContent.getMixins() )
                .language( editedContent.getLanguage() != null ? editedContent.getLanguage().getLanguage() : null )
                .build();

            editableNode.indexConfigDocument = indexConfigFactory.produce();
            editableNode.data = nodeData;
            editableNode.childOrder = editedContent.getChildOrder();
            editableNode.manualOrderValue = editedContent.getManualOrderValue();
        };
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();

        private ContentId contentId;

        private Function<Content, Content> contentEditor;

        private CreateAttachments createAttachments = CreateAttachments.empty();

        private Attributes versionAttributes;

        private ContentTypeService contentTypeService;

        private MixinService mixinService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private CmsService cmsService;

        Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        Builder editor( final Function<Content, Content> contentSupplier )
        {
            this.contentEditor = contentSupplier;
            return this;
        }

        Builder createAttachments( final CreateAttachments createAttachments )
        {
            this.createAttachments = createAttachments;
            return this;
        }

        Builder versionAttributes( final Attributes versionAttributes )
        {
            this.versionAttributes = versionAttributes;
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

        Builder cmsService( final CmsService value )
        {
            this.cmsService = value;
            return this;
        }

        void validate()
        {
            Objects.requireNonNull( contentEditor, "contentSupplier cannot be null" );
            Objects.requireNonNull( createAttachments, "createAttachments cannot be null" );

            Objects.requireNonNull( contentTypeService );
            Objects.requireNonNull( mixinService );
            Objects.requireNonNull( pageDescriptorService );
            Objects.requireNonNull( partDescriptorService );
            Objects.requireNonNull( layoutDescriptorService );
        }

        public PatchNodeParamsFactory build()
        {
            validate();
            return new PatchNodeParamsFactory( this );
        }
    }
}


