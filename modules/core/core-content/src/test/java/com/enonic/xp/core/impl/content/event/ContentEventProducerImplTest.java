package com.enonic.xp.core.impl.content.event;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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


    @BeforeEach
    public void initialize()
    {
        producer = new ContentEventProducerImpl( eventPublisher, nodeService );
        producer.activate( mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() ) );
    }

    @AfterEach
    public void deactivate()
    {
        producer.deactivate();
    }

    @Test
    public void test()
        throws Exception
    {
        final NodeVersionKey versionKey = NodeVersionKey.from( "1", "2", "3" );
        final NodeVersionId versionId = NodeVersionId.from( "v-id-1" );
        final NodeVersion nodeVersion = Mockito.mock( NodeVersion.class );

        final PropertyTree data = new PropertyTree();
        final PropertySet set = new PropertySet();

        set.addProperty( ContentPropertyNames.PUBLISH_FROM, ValueFactory.newDateTime( Instant.now() ) );
        set.addProperty( ContentPropertyNames.PUBLISH_TO, ValueFactory.newDateTime( Instant.now().plus( Duration.ofDays( 1 ) ) ) );

        data.addSet( ContentPropertyNames.PUBLISH_INFO, set );

        Mockito.when( nodeVersion.getData() ).thenReturn( data );
        Mockito.when( nodeService.getByNodeVersionKey( versionKey ) ).thenReturn( nodeVersion );

        final NodeBranchEntry successPublished1 = NodeBranchEntry.create()
            .nodeId( NodeId.from( "1" ) )
            .nodePath( NodePath.create( "/1" ).build() )
            .timestamp( Instant.now() )
            .nodeVersionKey( versionKey )
            .nodeVersionId( versionId )
            .build();

        final PushNodesResult result = PushNodesResult.create().addSuccess( successPublished1, successPublished1.getNodePath() ).build();

        producer.published( result );

        Thread.sleep( 5000 );

        verify( eventPublisher, times( 1 ) ).publish( isA( Event.class ) );
    }
}
