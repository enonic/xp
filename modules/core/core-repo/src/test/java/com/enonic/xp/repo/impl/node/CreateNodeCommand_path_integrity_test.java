package com.enonic.xp.repo.impl.node;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateNodeCommand_path_integrity_test
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void create()
    {
        final int concurrentAttempts = 5;
        final int expectedSuccessfulAttempts = 1;
        final AtomicInteger exceptionCounter = new AtomicInteger();

        CompletableFuture.allOf(
            IntStream.range( 0, concurrentAttempts ).mapToObj( i -> CompletableFuture.runAsync( CreateNodeTask.create().
                binaryService( this.binaryService ).
                storageService( this.storageService ).
                nodeSearchService( this.searchService ).
                indexServiceInternal( this.indexServiceInternal ).
                context( ContextAccessor.current() ).
                build() ).
                exceptionally( throwable -> {
                    exceptionCounter.incrementAndGet();
                    return null;
                } ) ).toArray( CompletableFuture[]::new ) ).join();

        assertEquals( concurrentAttempts - expectedSuccessfulAttempts, exceptionCounter.get() );

        refresh();

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create().
            path( NodePath.create( NodePath.ROOT, "myNode" ).build() ).
            build() );

        assertEquals( expectedSuccessfulAttempts, result.getTotalHits() );
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
