package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;

public abstract class AbstractContentEventSyncCommand
{
    final ContentService contentService;

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

    protected ContentPath buildNewPath( final ContentPath parentPath, final ContentName name )
    {
        String newName = name.toString();

        while ( contentService.contentExists( ContentPath.from( parentPath, newName ) ) )
        {
            newName = NameValueResolver.name( newName );
        }

        return ContentPath.from( parentPath, newName );
    }

    public abstract static class Builder<B extends Builder<B>>
    {
        protected List<ContentToSync> contentToSync;

        private ContentService contentService;

        @SuppressWarnings("unchecked")
        public B contentService( final ContentService contentService )
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
