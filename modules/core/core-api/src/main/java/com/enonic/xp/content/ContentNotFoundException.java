package com.enonic.xp.content;

import java.text.MessageFormat;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public final class ContentNotFoundException
    extends NotFoundException
{
    private ContentNotFoundException( final ContentId contentId, final ContentPath contentPath, final RepositoryId repositoryId,
                                      final Branch branch, final NodePath contentRoot, final Throwable cause )
    {
        super( cause, buildMessage( contentPath, contentId, repositoryId, branch, contentRoot ) );
    }

    private static String buildMessage( final ContentPath path, final ContentId contentId, final RepositoryId repositoryId,
                                        final Branch branch, final NodePath contentRoot )
    {
        return Stream.of( "Content", path != null ? MessageFormat.format( "with path [{0}]", path ) : "",
                          MessageFormat.format( "with id [{0}]", contentId ),
                          repositoryId != null ? MessageFormat.format( "in repository [{0}]", repositoryId ) : "",
                          branch != null ? MessageFormat.format( "in branch [{0}]", branch ) : "",
                          contentRoot != null ? MessageFormat.format( "with root [{0}]", contentRoot ) : "", "not found" )
            .filter( Predicate.not( String::isEmpty ) )
            .collect( Collectors.joining( " " ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RepositoryId repositoryId;

        private Branch branch;

        private ContentPath contentPath;

        private ContentId contentId;

        private NodePath contentRoot;

        private Throwable cause;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder contentPath( final ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder contentRoot( final NodePath contentRoot )
        {
            this.contentRoot = contentRoot;
            return this;
        }

        public Builder cause( final Throwable cause )
        {
            this.cause = cause;
            return this;
        }

        public ContentNotFoundException build()
        {
            return new ContentNotFoundException( contentId, contentPath, repositoryId, branch, contentRoot, cause );
        }
    }
}
