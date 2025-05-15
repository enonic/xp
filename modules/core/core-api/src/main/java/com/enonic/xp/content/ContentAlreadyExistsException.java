package com.enonic.xp.content;

import java.text.MessageFormat;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public final class ContentAlreadyExistsException
    extends NotFoundException
{
    private final ContentPath path;

    private final RepositoryId repositoryId;

    private final Branch branch;

    public ContentAlreadyExistsException( final ContentPath path, final RepositoryId repositoryId, final Branch branch )
    {
        super( buildMessage( path, repositoryId, branch ) );
        this.path = path;
        this.repositoryId = repositoryId;
        this.branch = branch;
    }

    public ContentPath getContentPath()
    {
        return path;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public String getCode()
    {
        return "contentAlreadyExists";
    }

    private static String buildMessage( final ContentPath path, final RepositoryId repositoryId, final Branch branch )
    {
        return Stream.of( MessageFormat.format( "Content at path [{0}]", path ), repositoryId != null ? MessageFormat.format(
                              "in repository [{0}]", repositoryId ) : "",
                          branch != null ? MessageFormat.format( "in branch [{0}]", branch ) : "", "already exists" ).
            filter( Predicate.not( String::isEmpty ) ).
            collect( Collectors.joining( " " ) );
    }
}
