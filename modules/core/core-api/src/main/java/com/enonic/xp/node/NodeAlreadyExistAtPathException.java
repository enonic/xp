package com.enonic.xp.node;


import java.text.MessageFormat;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.exception.DuplicateElementException;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public class NodeAlreadyExistAtPathException
    extends DuplicateElementException
{
    private final NodePath node;

    private final RepositoryId repositoryId;

    private final Branch branch;

    public NodeAlreadyExistAtPathException( final NodePath nodePath, final RepositoryId repositoryId, final Branch branch )
    {
        super( buildMessage( nodePath, repositoryId, branch ) );
        this.node = nodePath;
        this.repositoryId = repositoryId;
        this.branch = branch;
    }

    public NodePath getNode()
    {
        return node;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    private static String buildMessage( final NodePath nodePath, final RepositoryId repositoryId, final Branch branch )
    {
        return Stream.of( MessageFormat.format( "Node already exists at {0}", nodePath ),
                          repositoryId != null ? MessageFormat.format( "repository: {0}", repositoryId ) : "",
                          branch != null ? MessageFormat.format( "branch: {0}", branch ) : "" ).
            filter( Predicate.not( String::isEmpty ) ).
            collect( Collectors.joining( " " ) );
    }
}
