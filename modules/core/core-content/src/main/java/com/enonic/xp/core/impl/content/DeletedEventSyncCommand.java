package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.schema.content.ContentTypeName;

final class DeletedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    public DeletedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    protected void doSync()
    {
        if ( isToSyncDelete( params.getTargetContent() ) )
        {
            if ( needToDelete( params ) )
            {
                final DeleteContentParams deleteParams = DeleteContentParams.create().
                    contentPath( params.getTargetContent().getPath() ).
                    deleteOnline( true ).
                    build();

                contentService.deleteWithoutFetch( deleteParams );
            }
        }
    }

    private boolean isToSyncDelete( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.CONTENT ) &&
            !ContentTypeName.templateFolder().equals( targetContent.getType() );
    }

    private boolean needToDelete( final ContentEventSyncCommandParams params )
    {
        return params.getSourceContext().callWith( () -> !contentService.contentExists( params.getTargetContent().getId() ) ) &&
            contentService.getDependencies( params.getTargetContent().getId() ).getInbound().isEmpty() &&
            !params.getTargetContent().hasChildren();
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params.getTargetContent(), "targetContent must be set." );
            Preconditions.checkArgument( params.getSourceContent() == null, "sourceContent must be null." );
        }

        public DeletedEventSyncCommand build()
        {
            validate();
            return new DeletedEventSyncCommand( this );
        }
    }
}
