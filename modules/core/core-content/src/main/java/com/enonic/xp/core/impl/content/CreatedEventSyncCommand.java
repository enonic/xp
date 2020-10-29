package com.enonic.xp.core.impl.content;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.InsertManualStrategy;

final class CreatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( CreatedEventSyncCommand.class );

    public CreatedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    protected void doSync()
    {
        try
        {
            params.getSourceContext().runWith( () -> {
                if ( contentService.contentExists( params.getSourceContent().getParentPath() ) )
                {
                    final ContentId parentId = contentService.getByPath( params.getSourceContent().getParentPath() ).getId();
                    params.getTargetContext().runWith( () -> {

                        if ( params.getSourceContent().getParentPath().isRoot() )
                        {
                            contentService.importContent(
                                createImportParams( params, buildNewPath( ContentPath.ROOT, params.getSourceContent().getName() ), null ) );
                            return;
                        }

                        if ( contentService.contentExists( parentId ) )
                        {
                            final Content targetParent = contentService.getById( parentId );

                            contentService.importContent(
                                createImportParams( params, buildNewPath( targetParent.getPath(), params.getSourceContent().getName() ),
                                                    targetParent.getChildOrder().isManualOrder() ? InsertManualStrategy.MANUAL : null ) );
                        }
                    } );
                }
            } );

        }
        catch ( ContentAlreadyExistsException e )
        {
            LOG.warn( "content [{}] already exists.", params.getSourceContent().getId() );
        }
    }

    private ImportContentParams createImportParams( final ContentEventSyncCommandParams params, final ContentPath targetPath,
                                                    final InsertManualStrategy insertManualStrategy )
    {
        final BinaryAttachments.Builder builder = BinaryAttachments.create();

        params.getSourceContent().getAttachments().
            forEach( attachment -> {
                final ByteSource binary = params.getSourceContext().callWith(
                    () -> contentService.getBinary( params.getSourceContent().getId(), attachment.getBinaryReference() ) );
                builder.add( new BinaryAttachment( attachment.getBinaryReference(), binary ) );
            } );

        final EnumSet<ContentInheritType> inheritTypes = EnumSet.allOf( ContentInheritType.class );

        return ImportContentParams.create().
            importContent( params.getSourceContent() ).
            targetPath( targetPath ).
            binaryAttachments( builder.build() ).
            inherit( inheritTypes ).
            importPermissions( false ).
            dryRun( false ).
            insertManualStrategy( insertManualStrategy ).
            build();
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {

        void validate()
        {
            Preconditions.checkNotNull( params.getSourceContent(), "sourceContent must be set." );
            Preconditions.checkArgument( params.getTargetContent() == null, "targetContent must be null." );
        }

        public CreatedEventSyncCommand build()
        {
            validate();
            return new CreatedEventSyncCommand( this );
        }
    }
}
