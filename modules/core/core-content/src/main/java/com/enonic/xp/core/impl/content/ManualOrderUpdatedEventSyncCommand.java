package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.security.PrincipalKey;

final class ManualOrderUpdatedEventSyncCommand
    extends AbstractContentEventSyncCommand
{
    ManualOrderUpdatedEventSyncCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    protected void doSync()
    {
        if ( needToUpdateManualOrderValue( params.getSourceContent(), params.getTargetContent() ) )
        {
            if ( isToSyncManualOrderUpdated( params ) )
            {
                contentService.update( updateManualOrderValueParams( params.getSourceContent() ) );
            }
        }
    }

    private boolean needToUpdateManualOrderValue( final Content sourceContent, final Content targetContent )
    {
        return !Objects.equals( targetContent.getManualOrderValue(), sourceContent.getManualOrderValue() );
    }

    private boolean isToSyncManualOrderUpdated( final ContentEventSyncCommandParams params )
    {
        if ( contentService.contentExists( params.getTargetContent().getParentPath() ) )
        {
            final Content targetParent = contentService.getByPath( params.getTargetContent().getParentPath() );
            if ( targetParent.getChildOrder().isManualOrder() )
            {
                final Content sourceParent =
                    params.getSourceContext().callWith( () -> contentService.getByPath( params.getSourceContent().getParentPath() ) );

                return targetParent.getId().equals( sourceParent.getId() ) && targetParent.getInherit().contains( ContentInheritType.SORT );
            }
        }
        return false;
    }

    private UpdateContentParams updateManualOrderValueParams( final Content source )
    {
        return new UpdateContentParams().
            contentId( source.getId() ).
            modifier( PrincipalKey.ofAnonymous() ).
            requireValid( false ).
            stopInherit( false ).
            editor( edit -> edit.manualOrderValue = source.getManualOrderValue() );
    }

    public static class Builder
        extends AbstractContentEventSyncCommand.Builder<Builder>
    {
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params.getSourceContent(), "sourceContent must be set." );
            Preconditions.checkNotNull( params.getTargetContent(), "targetContent must be set." );
        }

        public ManualOrderUpdatedEventSyncCommand build()
        {
            validate();
            return new ManualOrderUpdatedEventSyncCommand( this );
        }
    }
}
