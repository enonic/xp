package com.enonic.xp.repo.impl.node;

import java.util.concurrent.Callable;

import org.elasticsearch.index.IndexNotFoundException;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;

public class NodeHelper
{
    public static void runAsAdmin( final Runnable runnable )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
            build().
            runWith( runnable );
    }

    public static <T> T runAsAdmin( final Callable<T> callable )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( NodeConstants.NODE_SU_AUTH_INFO ).
            build().
            callWith( callable );
    }

    /**
     * A branch holds the root node from the moment it is created, so the root node is what makes the branch exist and its absence is
     * reported as a missing branch. The lookup asks for no field of the entry, which lets the storage answer without reading the
     * document.
     */
    static void verifyBranchExists( final NodeStorageService nodeStorageService, final RepositoryId repositoryId, final Branch branch )
    {
        final boolean rootExists;
        try
        {
            rootExists = nodeStorageService.exists( NodeId.ROOT, InternalContext.create( ContextAccessor.current() )
                .repositoryId( repositoryId )
                .branch( branch )
                .build() );
        }
        catch ( IndexNotFoundException e )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }
        if ( !rootExists )
        {
            throw new BranchNotFoundException( branch );
        }
    }
}
