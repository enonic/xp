package com.enonic.xp.core.impl.content;

import java.util.Objects;
import java.util.function.Predicate;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;

public final class FindNearestContentByPathCommand
    extends AbstractContentCommand
{
    private final ContentPath contentPath;

    private final Predicate<Content> predicate;

    private FindNearestContentByPathCommand( final Builder builder )
    {
        super( builder );
        this.contentPath = builder.contentPath;
        this.predicate = builder.predicate;
    }

    public static Builder create()
    {
        return new Builder();
    }

    Content execute()
    {
        final Content content = executeGetByPath( contentPath );
        if ( content != null && predicate.test( content ) )
        {
            return content;
        }

        //Resolves the closest content, starting from the root.
        Content foundContent = null;
        ContentPath nextContentPath = ContentPath.ROOT;
        for ( final ContentName contentName : contentPath )
        {
            final ContentPath currentContentPath = ContentPath.from( nextContentPath, contentName );

            final Content childContent = executeGetByPath( currentContentPath );
            if ( childContent == null )
            {
                break;
            }
            if ( predicate.test( childContent ) )
            {
                foundContent = childContent;
            }
            nextContentPath = currentContentPath;
        }

        return foundContent;
    }

    private Content executeGetByPath( final ContentPath path )
    {
        return GetContentByPathCommand.create( path )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentPath contentPath;

        private Predicate<Content> predicate;

        Builder()
        {
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder predicate( final Predicate<Content> predicate )
        {
            this.predicate = predicate;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( contentPath, "contentPath must be set" );
            Objects.requireNonNull( predicate, "predicate must be set" );
        }

        public FindNearestContentByPathCommand build()
        {
            validate();
            return new FindNearestContentByPathCommand( this );
        }
    }

}
