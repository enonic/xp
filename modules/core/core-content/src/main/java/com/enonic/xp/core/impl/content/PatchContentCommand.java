package com.enonic.xp.core.impl.content;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPatcher;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

import static com.enonic.xp.core.impl.content.Constants.CONTENT_SKIP_SYNC;

public class PatchContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final PatchContentParams params;

    private PatchContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PatchContentParams params )
    {
        return create().params( params );
    }

    public static Builder create( final AbstractCreatingOrUpdatingContentCommand source )
    {
        return new Builder( source );
    }

    PatchContentResult execute()
    {
        validateCreateAttachments( params.getCreateAttachments() );
        return doExecute();
    }

    private PatchContentResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        Content patchedContent = patchContent( params.getPatcher(), contentBeforeChange );
        if ( !params.getCreateAttachments().isEmpty() )
        {
            patchedContent = Content.create( patchedContent )
                .attachments( mergeExistingAndUpdatedAttachments( patchedContent.getAttachments() ) )
                .build();
        }

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create().editedContent( patchedContent )
            .createAttachments( params.getCreateAttachments() )
            .branches( params.getBranches() )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .contentDataSerializer( this.translator.getContentDataSerializer() )
            .siteService( this.siteService )
            .build()
            .produce();

        final ContextBuilder context = ContextBuilder.from( ContextAccessor.current() );

        if ( params.isSkipSync() )
        {
            context.attribute( "eventMetadata", Map.of( CONTENT_SKIP_SYNC, "true" ) );
        }

        final PatchNodeResult result = context.build().callWith( () -> nodeService.patch( patchNodeParams ) );

        final PatchContentResult.Builder builder = PatchContentResult.create().contentId( ContentId.from( result.getNodeId() ) );

        result.getResults()
            .forEach( branchResult -> builder.addResult( branchResult.branch(), branchResult.node() != null
                ? translator.fromNode( branchResult.node(), true )
                : null ) );

        return builder.build();
    }

    private Attachments mergeExistingAndUpdatedAttachments( final Attachments existingAttachments )
    {
        final Attachments.Builder result = Attachments.create();

        final Map<String, CreateAttachment> createAttachmentMap = params.getCreateAttachments()
            .stream()
            .collect( Collectors.toMap( CreateAttachment::getName, Function.identity(), ( a, b ) -> b ) );

        final Set<String> processedNames = new HashSet<>();

        for ( Attachment existing : existingAttachments )
        {
            final CreateAttachment update = createAttachmentMap.get( existing.getName() );

            if ( update == null )
            {
                result.add( existing );
            }
            else
            {
                result.add( buildAttachment( update ) );
                processedNames.add( update.getName() );
            }
        }

        for ( CreateAttachment createAttachment : createAttachmentMap.values() )
        {
            if ( !processedNames.contains( createAttachment.getName() ) )
            {
                result.add( buildAttachment( createAttachment ) );
            }
        }

        return result.build();
    }

    private Attachment buildAttachment( CreateAttachment createAttachment )
    {
        final Attachment.Builder builder = Attachment.create()
            .name( createAttachment.getName() )
            .label( createAttachment.getLabel() )
            .mimeType( createAttachment.getMimeType() )
            .textContent( createAttachment.getTextContent() );

        populateByteSourceProperties( createAttachment.getByteSource(), builder );
        return builder.build();
    }

    private Content patchContent( final ContentPatcher patcher, final Content original )
    {
        final PatchableContent patchableContent = new PatchableContent( original );

        if ( patcher != null )
        {
            patcher.patch( patchableContent );
        }

        return patchableContent.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private PatchContentParams params;

        private Builder()
        {
        }

        private Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
        }

        public Builder params( final PatchContentParams params )
        {
            this.params = params;
            return this;
        }


        public PatchContentCommand build()
        {
            this.validate();
            return new PatchContentCommand( this );
        }
    }
}
