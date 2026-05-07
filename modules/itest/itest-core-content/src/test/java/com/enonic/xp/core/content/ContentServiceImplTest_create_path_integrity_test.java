package com.enonic.xp.core.content;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentServiceImplTest_create_path_integrity_test
    extends AbstractContentServiceTest
{

    @Test
    void create()
    {
        final int concurrentAttempts = 5;
        final int expectedSuccessfulAttempts = 1;
        final AtomicInteger exceptionCounter = new AtomicInteger();

        final Context context = ContextAccessor.current();
        final CreateContentParams params = CreateContentParams.create()
            .parent( ContentPath.ROOT )
            .name( "myContent" )
            .displayName( "myContent" )
            .contentData( new PropertyTree() )
            .type( ContentTypeName.folder() )
            .build();

        CompletableFuture.allOf( IntStream.range( 0, concurrentAttempts )
                                     .mapToObj( i -> CompletableFuture.runAsync(
                                             () -> context.callWith( () -> this.contentService.create( params ) ) )
                                         .exceptionally( throwable -> {
                                             exceptionCounter.incrementAndGet();
                                             return null;
                                         } ) )
                                     .toArray( CompletableFuture[]::new ) ).join();

        final FindContentIdsByQueryResult result =
            this.contentService.find( ContentQuery.create().queryExpr( QueryParser.parse( "_path = '/content/mycontent'" ) ).build() );
        assertEquals( expectedSuccessfulAttempts, result.getTotalHits() );
    }

}
