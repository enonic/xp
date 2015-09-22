package com.enonic.xp.content;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.event.Event;

@Beta
public final class ContentChangeEvent
    implements Event
{
    private final ImmutableList<ContentChange> contentChanges;

    private ContentChangeEvent( final ImmutableList<ContentChange> changes )
    {
        this.contentChanges = changes;
    }

    public List<ContentChange> getChanges()
    {
        return contentChanges;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "changes", contentChanges ).
            omitNullValues().
            toString();
    }

    public static ContentChangeEvent from( final ContentChangeType type, final ContentPaths contentPaths )
    {
        final ImmutableList<ContentChange> changes = ImmutableList.of( new ContentChange( type, contentPaths ) );
        return new ContentChangeEvent( changes );
    }

    public static ContentChangeEvent from( final ContentChangeType type, final ContentPath contentPath )
    {
        final ImmutableList<ContentChange> changes = ImmutableList.of( new ContentChange( type, ContentPaths.from( contentPath ) ) );
        return new ContentChangeEvent( changes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<ContentChange> changes;

        private Builder()
        {
            changes = ImmutableList.builder();
        }

        public Builder change( final ContentChangeType type, final ContentPaths contentPaths )
        {
            changes.add( new ContentChange( type, contentPaths ) );
            return this;
        }

        public Builder change( final ContentChangeType type, final ContentPath contentPath )
        {
            changes.add( new ContentChange( type, ContentPaths.from( contentPath ) ) );
            return this;
        }

        public ContentChangeEvent build()
        {
            return new ContentChangeEvent( changes.build() );
        }
    }


    public enum ContentChangeType
    {
        UNKNOWN( "" ),
        PUBLISH( "P" ),
        DUPLICATE( "D" ),
        CREATE( "C" ),
        UPDATE( "U" ),
        DELETE( "X" ),
        PENDING( "A" ),
        RENAME( "R" ),
        SORT( "S" );

        private final String id;

        ContentChangeType( final String id )
        {
            this.id = id;
        }

        public String id()
        {
            return id;
        }
    }

    public static final class ContentChange
    {
        private final ContentPaths contentPaths;

        private final ContentChangeType type;

        ContentChange( final ContentChangeType type, final ContentPaths contentPaths )
        {
            this.contentPaths = contentPaths;
            this.type = type;
        }

        public ContentPaths getContentPaths()
        {
            return contentPaths;
        }

        public ContentChangeType getType()
        {
            return type;
        }

        @Override
        public boolean equals( final Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            final ContentChange that = (ContentChange) o;

            if ( contentPaths != null ? !contentPaths.equals( that.contentPaths ) : that.contentPaths != null )
            {
                return false;
            }
            if ( type != null ? !( type == that.type ) : that.type != null )
            {
                return false;
            }
            return true;
        }
    }
}
