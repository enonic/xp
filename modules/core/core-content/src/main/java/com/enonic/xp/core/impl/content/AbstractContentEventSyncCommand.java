package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;

public abstract class AbstractContentEventSyncCommand
{
    final InternalContentService contentService;

    final List<ContentToSync> contentToSync;

    AbstractContentEventSyncCommand( final Builder<?> builder )
    {
        this.contentService = builder.contentService;
        this.contentToSync = builder.contentToSync;
    }

    public void sync()
    {
        this.doSync();
    }

    protected abstract void doSync();

    protected ContentPath buildNewPath( final ContentPath parentPath, final ContentName name, final Content targetContent )
    {
        final ContentPath newParentPath;
        if ( targetContent == null || targetContent.getInherit().contains( ContentInheritType.PARENT ) )
        {
            newParentPath = parentPath;
        }
        else
        {
            newParentPath = targetContent.getParentPath();
        }

        String newName;
        if ( targetContent == null || targetContent.getInherit().contains( ContentInheritType.NAME ) )
        {
            newName = name.toString();
            while ( contentService.getByPathOptional( ContentPath.from( newParentPath, newName ) ).isPresent() )
            {
                newName = NameValueResolver.name( newName );
            }
        }
        else
        {
            newName = targetContent.getName().toString();
        }

        return ContentPath.from( newParentPath, newName );
    }

    public abstract static class Builder<B extends Builder<B>>
    {
        protected List<ContentToSync> contentToSync;

        private InternalContentService contentService;

        public B contentService( final InternalContentService contentService )
        {
            this.contentService = contentService;
            return (B) this;
        }

        public B contentToSync( final List<ContentToSync> params )
        {
            this.contentToSync = params;
            return (B) this;
        }

        void validate()
        {
            Objects.requireNonNull( contentService );
            Objects.requireNonNull( contentToSync, "contentToSync cannot be null" );
        }

        public abstract <T extends AbstractContentEventSyncCommand> T build();
    }
}
