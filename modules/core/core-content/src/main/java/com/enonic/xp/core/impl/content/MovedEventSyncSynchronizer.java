package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentInheritType;

abstract class MovedEventSyncSynchronizer
{
    protected final LayersContentService layersContentService;

    protected final List<ContentToSync> contents;

    MovedEventSyncSynchronizer( final Builder<?> builder )
    {
        this.layersContentService = builder.layersContentService;
        this.contents = builder.contents.build();
    }

    boolean isToSyncContent( final Content targetContent )
    {
        return targetContent.getInherit().contains( ContentInheritType.PARENT );
    }

    abstract void execute();

    abstract static class Builder<T extends Builder<T>>
    {
        private final ImmutableList.Builder<ContentToSync> contents = ImmutableList.builder();

        private LayersContentService layersContentService;

        T contentService( final LayersContentService layersContentService )
        {
            this.layersContentService = layersContentService;
            return (T) this;
        }

        T addContents( final Collection<ContentToSync> contents )
        {
            this.contents.addAll( contents );
            return (T) this;
        }

        abstract MovedEventSyncSynchronizer build();
    }
}
