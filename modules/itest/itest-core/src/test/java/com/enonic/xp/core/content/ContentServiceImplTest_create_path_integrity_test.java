package com.enonic.xp.core.content;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_create_path_integrity_test
    extends AbstractContentServiceTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void create()
        throws Exception
    {
        List<Future> threads = Lists.newArrayList();

        final ExecutorService executor = Executors.newFixedThreadPool( 5 );

        IntStream.range( 0, 5 ).forEach( i -> {
            threads.add( executor.submit( new CreateContentTask( this.contentService, ContextAccessor.current() ) ) );
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

        final FindContentIdsByQueryResult result =
            this.contentService.find( ContentQuery.create().queryExpr( QueryParser.parse( "_path = '/content/mycontent'" ) ).build() );
        assertEquals( 1, result.getTotalHits() );
    }

    private class CreateContentTask
        implements Runnable
    {
        private final ContentService contentService;

        private final Context context;

        public CreateContentTask( final ContentService contentService, final Context context )
        {
            this.contentService = contentService;
            this.context = context;
        }

        @Override
        public void run()
        {
            context.callWith( () -> this.contentService.create( CreateContentParams.create().
                parent( ContentPath.ROOT ).
                name( "myContent" ).
                displayName( "myContent" ).
                contentData( new PropertyTree() ).
                type( ContentTypeName.folder() ).
                build() ) );

        }
    }


}
