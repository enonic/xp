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

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.core.impl.content.serializer.PublishInfoSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodesResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

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
}
