package com.enonic.xp.core.impl.content.event;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodesResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContentEventProducerImplTest
{

    @Mock
    private EventPublisher eventPublisher;

//    @Mock
//    private SimpleRecurringJobScheduler jobScheduler;

    @Mock
    private NodeService nodeService;

    @Mock
    private PriorityBlockingQueue<ContentEvent> eventQueue;

    @Mock
    private PublishInfoSerializer publishInfoSerializer;

    private ContentEventProducerImpl producer;

    private Random random;


    @BeforeEach
    public void initialize()
    {
        producer = new ContentEventProducerImpl( eventPublisher, nodeService );

        ContentConfig config = mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.event_period() ).thenReturn( "PT0.001S" );
        producer.activate( config );
        random = new Random();
    }

    @AfterEach
    public void deactivate()
    {
        producer.deactivate();
    }

    @Test
    public void testBatchOnline()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successPublished1 = createNodeBranchEntry( now, null, now );
        final NodeBranchEntry successPublished2 = createNodeBranchEntry( now, now.plus( Duration.ofDays( 1 ) ), now );

        final PushNodesResult result = PushNodesResult.create()
            .addSuccess( successPublished1, successPublished1.getNodePath() )
            .addSuccess( successPublished2, successPublished2.getNodePath() )
            .build();

        producer.published( result );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 1 ) ).publish( argumentCaptor.capture() );

        Assertions.assertEquals( "content.online", argumentCaptor.getValue().getType() );

        final List<Map<String, String>> contents = (List<Map<String, String>>) argumentCaptor.getValue().getData().get( "contents" );

        assertThat( contents ).hasSize( 2 )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successPublished1.getNodeId().toString() ) )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successPublished2.getNodeId().toString() ) );
    }

    @Test
    public void testTwoBatchesOnlineAndOffline()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successPublished1 = createNodeBranchEntry( now, null, now );
        final NodeBranchEntry successPublished2 = createNodeBranchEntry( now, now.plus( Duration.ofMillis( 1 ) ), now );

        final PushNodesResult result = PushNodesResult.create()
            .addSuccess( successPublished1, successPublished1.getNodePath() )
            .addSuccess( successPublished2, successPublished2.getNodePath() )
            .build();

        producer.published( result );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 2 ) ).publish( argumentCaptor.capture() );

        assertThat( argumentCaptor.getAllValues() ).hasSize( 2 )
            .anyMatch( event -> event.isType( "content.online" ) )
            .anyMatch( event -> event.isType( "content.offline" ) );

        final List<Map<String, String>> online =
            (List<Map<String, String>>) argumentCaptor.getAllValues().get( 0 ).getData().get( "contents" );
        final List<Map<String, String>> offline =
            (List<Map<String, String>>) argumentCaptor.getAllValues().get( 1 ).getData().get( "contents" );

        assertThat( online ).hasSize( 2 )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successPublished1.getNodeId().toString() ) )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successPublished2.getNodeId().toString() ) );

        assertThat( offline ).hasSize( 1 )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successPublished2.getNodeId().toString() ) );
    }

    @Test
    public void movedAndRepublished()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry published = createNodeBranchEntry( now, null, now );

        final PushNodesResult result = PushNodesResult.create().addSuccess( published, published.getNodePath() ).build();

        producer.published( result );

        Thread.sleep( 100 );

        final Instant newNow = Instant.now();

        final NodeBranchEntry republishedToTheFuture =
            copyNodeBranchEntry( published, NodePath.create( "/a" ).build(), newNow, newNow.plus( Duration.ofDays( 2 ) ), newNow );

        producer.published( PushNodesResult.create().addSuccess( republishedToTheFuture, published.getNodePath() ).build() );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );
        verify( eventPublisher, times( 3 ) ).publish( argumentCaptor.capture() );

        assertThat( argumentCaptor.getAllValues() ).hasSize( 3 );

        final Event online1 = argumentCaptor.getAllValues().get( 0 );
        final Event offline1 = argumentCaptor.getAllValues().get( 1 );
        final Event online2 = argumentCaptor.getAllValues().get( 2 );

        assertThat( online1.getType() ).isEqualTo( "content.online" );
        assertThat( offline1.getType() ).isEqualTo( "content.offline" );
        assertThat( online2.getType() ).isEqualTo( "content.online" );

        final List<Map<String, String>> online1Fields = (List<Map<String, String>>) online1.getData().get( "contents" );

        assertThat( online1Fields ).hasSize( 1 )
            .anyMatch( stringStringMap -> stringStringMap.get( "path" ).equals( published.getNodePath().toString() ) );

        final List<Map<String, String>> offline1Fields = (List<Map<String, String>>) offline1.getData().get( "contents" );

        assertThat( offline1Fields ).hasSize( 1 )
            .anyMatch( stringStringMap -> stringStringMap.get( "path" ).equals( published.getNodePath().toString() ) );

        final List<Map<String, String>> online2Fields = (List<Map<String, String>>) online2.getData().get( "contents" );

        assertThat( online2Fields ).hasSize( 1 )
            .anyMatch( stringStringMap -> stringStringMap.get( "path" ).equals( republishedToTheFuture.getNodePath().toString() ) );

    }

    @Test
    public void invalidatedEvents()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successPublished1 =
            createNodeBranchEntry( now.plus( Duration.ofMillis( 100 ) ), now.plus( Duration.ofDays( 1 ) ), now );
        final NodeBranchEntry successPublished2 =
            copyNodeBranchEntry( successPublished1, NodePath.create().elements( "/new" ).build(), now, now.plus( Duration.ofDays( 2 ) ),
                                 now );

        producer.published( PushNodesResult.create().addSuccess( successPublished1, successPublished1.getNodePath() ).build() );

        producer.published( PushNodesResult.create().addSuccess( successPublished2, successPublished2.getNodePath() ).build() );

        Thread.sleep( 600 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 1 ) ).publish( argumentCaptor.capture() );

        Assertions.assertEquals( "content.online", argumentCaptor.getValue().getType() );

        final List<Map<String, String>> contents = (List<Map<String, String>>) argumentCaptor.getValue().getData().get( "contents" );

        assertThat( contents ).hasSize( 1 ).anyMatch( stringStringMap -> stringStringMap.get( "path" ).equals( "/new" ) );
    }

    @Test
    public void failWithException()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successPublished1 = createNodeBranchEntry( now, now.plus( Duration.ofMillis( 1 ) ), now );

        final PushNodesResult result = PushNodesResult.create().addSuccess( successPublished1, successPublished1.getNodePath() ).build();

        doAnswer( a -> {
            if ( ( (Event) a.getArgument( 0 ) ).isType( "content.online" ) )
            {
                throw new RuntimeException();
            }
            return null;
        } ).when( eventPublisher ).publish( isA( Event.class ) );

        producer.published( result );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 2 ) ).publish( argumentCaptor.capture() );

    }

    @Test
    public void failWithError()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successPublished1 = createNodeBranchEntry( now, now.plus( Duration.ofMillis( 1 ) ), now );

        final PushNodesResult result = PushNodesResult.create().addSuccess( successPublished1, successPublished1.getNodePath() ).build();

        doAnswer( a -> {
            if ( ( (Event) a.getArgument( 0 ) ).isType( "content.online" ) )
            {
                throw new UnknownError();
            }
            return null;
        } ).when( eventPublisher ).publish( isA( Event.class ) );

        producer.published( result );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 1 ) ).publish( argumentCaptor.capture() );

    }

    @Test
    public void testBatchOffline()
        throws Exception
    {
        final Instant now = Instant.now();
        final NodeBranchEntry successUnpublished1 = createNodeBranchEntry( null, null, now );
        final NodeBranchEntry successUnpublished2 = createNodeBranchEntry( null, null, now );

        producer.unpublished( NodeBranchEntries.create().add( successUnpublished1 ).add( successUnpublished2 ).build() );

        Thread.sleep( 100 );

        final ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );

        verify( eventPublisher, times( 1 ) ).publish( argumentCaptor.capture() );

        Assertions.assertEquals( "content.offline", argumentCaptor.getValue().getType() );

        final List<Map<String, String>> contents = (List<Map<String, String>>) argumentCaptor.getValue().getData().get( "contents" );

        assertThat( contents ).hasSize( 2 )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successUnpublished1.getNodeId().toString() ) )
            .anyMatch( stringStringMap -> stringStringMap.get( "id" ).equals( successUnpublished2.getNodeId().toString() ) );
    }

    private NodeBranchEntry createNodeBranchEntry( final Instant from, final Instant to, final Instant timestamp )
    {

        final NodeVersionKey versionKey = NodeVersionKey.from( String.valueOf( random.nextInt() ), String.valueOf( random.nextInt() ),
                                                               String.valueOf( random.nextInt() ) );
        final NodeVersionId versionId = NodeVersionId.from( random.nextInt() );
        final NodeVersion nodeVersion = Mockito.mock( NodeVersion.class );

        final PropertyTree data = new PropertyTree();
        final PropertySet set = new PropertySet();

        if ( from != null )
        {
            set.addProperty( ContentPropertyNames.PUBLISH_FROM, ValueFactory.newDateTime( from ) );
        }
        if ( to != null )
        {
            set.addProperty( ContentPropertyNames.PUBLISH_TO, ValueFactory.newDateTime( to ) );
        }

        data.addSet( ContentPropertyNames.PUBLISH_INFO, set );

        Mockito.when( nodeVersion.getData() ).thenReturn( data );
        Mockito.when( nodeService.getByNodeVersionKey( versionKey ) ).thenReturn( nodeVersion );

        final int id = random.nextInt();

        return NodeBranchEntry.create()
            .nodeId( NodeId.from( id ) )
            .nodePath( NodePath.create( "/" + id ).build() )
            .timestamp( timestamp )
            .nodeVersionKey( versionKey )
            .nodeVersionId( versionId )
            .build();
    }

    private NodeBranchEntry copyNodeBranchEntry( final NodeBranchEntry source, final NodePath newNodePath, final Instant from,
                                                 final Instant to, final Instant timestamp )
    {

        final NodeVersionKey versionKey = NodeVersionKey.from( String.valueOf( random.nextInt() ), String.valueOf( random.nextInt() ),
                                                               String.valueOf( random.nextInt() ) );
        final NodeVersionId versionId = NodeVersionId.from( random.nextInt() );
        final NodeVersion nodeVersion = Mockito.mock( NodeVersion.class );

        final PropertyTree data = new PropertyTree();
        final PropertySet set = new PropertySet();

        if ( from != null )
        {
            set.addProperty( ContentPropertyNames.PUBLISH_FROM, ValueFactory.newDateTime( from ) );
        }
        if ( to != null )
        {
            set.addProperty( ContentPropertyNames.PUBLISH_TO, ValueFactory.newDateTime( to ) );
        }

        data.addSet( ContentPropertyNames.PUBLISH_INFO, set );

        Mockito.when( nodeVersion.getData() ).thenReturn( data );
        Mockito.when( nodeService.getByNodeVersionKey( versionKey ) ).thenReturn( nodeVersion );

        return NodeBranchEntry.create()
            .nodeId( source.getNodeId() )
            .nodePath( newNodePath != null ? newNodePath : source.getNodePath() )
            .timestamp( timestamp )
            .nodeVersionKey( versionKey )
            .nodeVersionId( versionId )
            .build();
    }
}
