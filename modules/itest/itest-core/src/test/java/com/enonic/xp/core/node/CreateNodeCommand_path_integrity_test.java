package com.enonic.xp.core.node;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.node.CreateNodeCommand;

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
        final AtomicInteger exceptionCounter = new AtomicInteger();

        final Context context = ContextAccessor.current();

        final CreateNodeCommand command = CreateNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .binaryService( this.binaryService )
            .params( CreateNodeParams.create().parent( NodePath.ROOT ).name( "myNode" ).build() )
            .build();

        CompletableFuture.allOf( IntStream.range( 0, concurrentAttempts )
                                     .mapToObj( i -> CompletableFuture.runAsync( () -> context.callWith( command::execute ) )
                                         .exceptionally( throwable -> {
                                             exceptionCounter.incrementAndGet();
                                             return null;
                                         } ) )
                                     .toArray( CompletableFuture[]::new ) ).join();

        assertEquals( concurrentAttempts - 1, exceptionCounter.get(), "Expecting only one successful attempt" );

        refresh();

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .withPath( true )
                                                                 .addQueryFilter( ValueFilter.create()
                                                                                      .fieldName( NodeIndexPath.PATH.getPath() )
                                                                                      .addValue( ValueFactory.newString(
                                                                                          NodePath.create( NodePath.ROOT )
                                                                                              .addElement( "myNode" )
                                                                                              .build()
                                                                                              .toString() ) )
                                                                                      .build() )
                                                                 .build() );

        assertEquals( 1, result.getTotalHits() );
    }
}
