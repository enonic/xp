package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentService;

abstract class MovedEventSyncSynchronizer
{
    protected final ContentService contentService;

    protected final List<ContentToSync> contents;

    MovedEventSyncSynchronizer( final Builder builder )
    {
        this.contentService = builder.contentService;
        this.contents = builder.contents.build();
    }

    boolean isToSyncContent( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    abstract void execute();

    abstract static class Builder<T extends Builder>
    {
        private final ImmutableList.Builder<ContentToSync> contents = ImmutableList.builder();

        private ContentService contentService;

        T contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return (T) this;
        }

        T addContents( final Collection<ContentToSync> contents )
        {
            this.contents.addAll( contents );
            return (T) this;
        }

        void validate()
        {
            Objects.requireNonNull( contentService );
        }

        abstract MovedEventSyncSynchronizer build();
    }
}
