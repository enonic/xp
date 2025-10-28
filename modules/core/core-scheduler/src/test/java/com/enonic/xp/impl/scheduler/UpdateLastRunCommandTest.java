package com.enonic.xp.impl.scheduler;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateLastRunCommandTest
{
    @Mock
    private NodeService nodeService;

    @Captor
    private ArgumentCaptor<UpdateNodeParams> captor;

    @BeforeEach
    void setUp()
    {

    }

    @Test
    void testCreateJob()
    {
        final TaskId lastTaskId = TaskId.from( "task-id" );
        final Instant lastRun = Instant.parse( "2021-02-25T10:44:33.170079900Z" );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        UpdateLastRunCommand.create().
            name( ScheduledJobName.from( "job" ) ).
            lastTaskId( lastTaskId ).
            lastRun( lastRun ).
            nodeService( nodeService ).
            build().
            execute();

        verify( nodeService ).update( captor.capture() );

        final EditableNode editableNode = new EditableNode( node );

        captor.getValue().getEditor().edit( editableNode );

        assertEquals( lastRun, editableNode.data.getProperty( ScheduledJobPropertyNames.LAST_RUN ).getInstant() );
        assertEquals( lastTaskId.toString(), editableNode.data.getProperty( ScheduledJobPropertyNames.LAST_TASK_ID ).getString() );
    }

    private Node mockNode()
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = jobData.newSet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, "ONE_TIME" );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "2021-02-25T10:44:33.170079900Z" );

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, "app:key" );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, true );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, jobData.newSet() );
        jobData.setString( ScheduledJobPropertyNames.CREATOR, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIER, "user:system:modifier" );
        jobData.setString( ScheduledJobPropertyNames.CREATED_TIME, "2021-02-26T10:44:33.170079900Z" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIED_TIME, "2021-03-26T10:44:33.170079900Z" );

        return Node.create().
            id( NodeId.from( "abc" ) ).
            name( "test" ).
            parentPath( NodePath.ROOT ).
            data( jobData ).
            build();

    }
}
