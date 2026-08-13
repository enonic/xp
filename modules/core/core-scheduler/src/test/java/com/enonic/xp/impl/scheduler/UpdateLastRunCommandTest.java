package com.enonic.xp.impl.scheduler;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateLastRunCommandTest
{
    @Mock
    private NodeService nodeService;

    @Captor
    private ArgumentCaptor<ApplyVersionAttributesParams> captor;

    @Test
    void updateLastRunAttributes()
    {
        final TaskId lastTaskId = TaskId.from( "task-id" );
        final Instant lastRun = Instant.parse( "2021-02-25T10:44:33.170079900Z" );

        final Node node = mockNode();
        node.data().setInstant( ScheduledJobPropertyNames.LAST_RUN, lastRun.minusSeconds( 1 ) );
        node.data().setString( ScheduledJobPropertyNames.LAST_TASK_ID, "old-task-id" );
        when( nodeService.getByPath( isA( NodePath.class ) ) ).thenReturn( node );
        when( nodeService.applyVersionAttributes( isA( ApplyVersionAttributesParams.class ) ) ).thenAnswer(
            invocation -> invocation.<ApplyVersionAttributesParams>getArgument( 0 ).getAddAttributes() );

        final ScheduledJob scheduledJob = UpdateLastRunCommand.create().
            name( ScheduledJobName.from( "job" ) ).
            lastTaskId( lastTaskId ).
            lastRun( lastRun ).
            nodeService( nodeService ).
            build().
            execute();

        verify( nodeService ).applyVersionAttributes( captor.capture() );

        final ApplyVersionAttributesParams params = captor.getValue();
        assertEquals( node.getNodeVersionId(), params.getNodeVersionId() );
        final GenericValue lastRunAttribute = params.getAddAttributes().get( ScheduledJobPropertyNames.LAST_RUN_ATTRIBUTE );
        assertEquals( lastRun.toString(),
                      lastRunAttribute.property( ScheduledJobPropertyNames.LAST_RUN_TIME_PROPERTY ).asString() );
        assertEquals( lastTaskId.toString(),
                      lastRunAttribute.property( ScheduledJobPropertyNames.LAST_RUN_TASK_ID_PROPERTY ).asString() );
        assertTrue( params.getRemoveAttributes().isEmpty() );
        assertEquals( lastRun, scheduledJob.getLastRun() );
        assertEquals( lastTaskId, scheduledJob.getLastTaskId() );
    }

    @Test
    void removeLastTaskIdWhenMissing()
    {
        final Instant lastRun = Instant.parse( "2021-02-25T10:44:33.170079900Z" );

        final Node node = mockNode();
        node.data().setString( ScheduledJobPropertyNames.LAST_TASK_ID, "old-task-id" );
        when( nodeService.getByPath( isA( NodePath.class ) ) ).thenReturn( node );
        when( nodeService.applyVersionAttributes( isA( ApplyVersionAttributesParams.class ) ) ).thenAnswer(
            invocation -> invocation.<ApplyVersionAttributesParams>getArgument( 0 ).getAddAttributes() );

        final ScheduledJob scheduledJob = UpdateLastRunCommand.create().
            name( ScheduledJobName.from( "job" ) ).
            lastRun( lastRun ).
            nodeService( nodeService ).
            build().
            execute();

        verify( nodeService ).applyVersionAttributes( captor.capture() );

        final ApplyVersionAttributesParams params = captor.getValue();
        final GenericValue lastRunAttribute = params.getAddAttributes().get( ScheduledJobPropertyNames.LAST_RUN_ATTRIBUTE );
        assertEquals( lastRun.toString(),
                      lastRunAttribute.property( ScheduledJobPropertyNames.LAST_RUN_TIME_PROPERTY ).asString() );
        assertTrue( lastRunAttribute.optional( ScheduledJobPropertyNames.LAST_RUN_TASK_ID_PROPERTY ).isEmpty() );
        assertTrue( params.getRemoveAttributes().isEmpty() );
        assertEquals( lastRun, scheduledJob.getLastRun() );
        assertNull( scheduledJob.getLastTaskId() );
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
            nodeVersionId( new NodeVersionId() ).
            build();

    }
}
