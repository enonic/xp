package com.enonic.xp.repo.impl.node;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

import static org.junit.Assert.*;

public class CreateNodeCommand_path_integrity_test
    extends AbstractNodeTest
{


    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void create()
        throws Exception
    {
        List<Future> threads = Lists.newArrayList();

        final ExecutorService executor = Executors.newFixedThreadPool( 5 );

        IntStream.range( 0, 5 ).forEach( i -> {
            threads.add( executor.submit( CreateNodeTask.create().
                binaryService( this.binaryService ).
                storageService( this.storageService ).
                nodeSearchService( this.searchService ).
                indexServiceInternal( this.indexServiceInternal ).
                context( ContextAccessor.current() ).
                build() ) );
        } );

        threads.forEach( entry -> {
            while ( !entry.isDone() )
            {
                try
                {
                    Thread.sleep( 5 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        } );

        refresh();

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create().
            path( NodePath.create( NodePath.ROOT, "myNode" ).build() ).
            build() );

        assertEquals( 1, result.getTotalHits() );
    }

    private static class CreateNodeTask
        implements Runnable
    {
        private final NodeStorageService storageService;

        private final NodeSearchService nodeSearchService;

        private final IndexServiceInternal indexServiceInternal;

        private final BinaryService binaryService;

        private final Context context;

        private CreateNodeTask( final Builder builder )
        {
            storageService = builder.storageService;
            nodeSearchService = builder.nodeSearchService;
            indexServiceInternal = builder.indexServiceInternal;
            this.binaryService = builder.binaryService;
            context = builder.context;
        }

        public static Builder create()
        {
            return new Builder();
        }

        @Override
        public void run()
        {
            context.callWith( () -> CreateNodeCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                storageService( this.storageService ).
                searchService( this.nodeSearchService ).
                binaryService( this.binaryService ).
                params( CreateNodeParams.create().
                    parent( NodePath.ROOT ).
                    name( "myNode" ).
                    build() ).
                build().
                execute() );

        }

        private static final class Builder
        {
            private NodeStorageService storageService;

            private NodeSearchService nodeSearchService;

            private IndexServiceInternal indexServiceInternal;

            private BinaryService binaryService;

            private Context context;

            private Builder()
            {
            }

            public Builder storageService( final NodeStorageService val )
            {
                storageService = val;
                return this;
            }

            public Builder nodeSearchService( final NodeSearchService val )
            {
                nodeSearchService = val;
                return this;
            }

            public Builder indexServiceInternal( final IndexServiceInternal val )
            {
                indexServiceInternal = val;
                return this;
            }

            public Builder binaryService( final BinaryService val )
            {
                this.binaryService = val;
                return this;
            }

            public Builder context( final Context val )
            {
                context = val;
                return this;
            }

            public CreateNodeTask build()
            {
                return new CreateNodeTask( this );
            }
        }
    }


}
