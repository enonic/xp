package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListScheduledJobsCommandTest
{
    @Mock
    private NodeService nodeService;

    @Test
    void listEntries()
    {
        final Node cronNode = jobNode( "cron-job", ScheduleCalendarType.CRON, null );
        final Node oneTimeNode = jobNode( "one-time-job", ScheduleCalendarType.ONE_TIME, Instant.parse( "2026-01-01T10:00:00Z" ) );

        when( nodeService.list( isA( ListNodesParams.class ) ) ).thenAnswer(
            invocation -> Stream.of( listEntry( cronNode ), listEntry( oneTimeNode ) ) );
        when( nodeService.getByIds( isA( NodeIds.class ) ) ).thenReturn( Nodes.from( cronNode, oneTimeNode ) );

        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( nodeService, mock( SchedulingCoordinator.class ), mock( ScheduleAuditLogSupport.class ) );

        final List<ScheduledJobEntry> entries = schedulerService.listEntries();

        assertEquals( 2, entries.size() );

        final ScheduledJobEntry cronEntry = entries.get( 0 );
        assertEquals( "cron-job", cronEntry.job().getName().getValue() );
        assertEquals( cronNode.getNodeVersionId(), cronEntry.versionId() );
        // run state is not fetched from node versions - only node data is read
        assertNull( cronEntry.job().getLastRun() );

        final ScheduledJobEntry oneTimeEntry = entries.get( 1 );
        assertEquals( "one-time-job", oneTimeEntry.job().getName().getValue() );
        assertEquals( oneTimeNode.getNodeVersionId(), oneTimeEntry.versionId() );
        // a one-time job's lastRun tombstone lives in node data and is part of the listing
        assertEquals( Instant.parse( "2026-01-01T10:00:00Z" ), oneTimeEntry.job().getLastRun() );
    }

    @Test
    void listWithRunState()
    {
        final Node cronNode = jobNode( "cron-job", ScheduleCalendarType.CRON, null );

        when( nodeService.list( isA( ListNodesParams.class ) ) ).thenAnswer( invocation -> Stream.of( listEntry( cronNode ) ) );
        when( nodeService.getByIds( isA( NodeIds.class ) ) ).thenReturn( Nodes.from( cronNode ) );

        final NodeVersion version = mock( NodeVersion.class );
        when( version.getAttributes() ).thenReturn(
            SchedulerSerializer.toLastRunAttributes( Instant.parse( "2026-01-01T11:00:00Z" ), TaskId.from( "task-1" ) ) );
        when( nodeService.getVersion( cronNode.id(), cronNode.getNodeVersionId() ) ).thenReturn( version );

        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( nodeService, mock( SchedulingCoordinator.class ), mock( ScheduleAuditLogSupport.class ) );

        final List<ScheduledJob> jobs = schedulerService.list();

        // the same listing, one version read per job richer: run state a cron job keeps in attributes
        assertEquals( 1, jobs.size() );
        assertEquals( Instant.parse( "2026-01-01T11:00:00Z" ), jobs.get( 0 ).getLastRun() );
        assertEquals( TaskId.from( "task-1" ), jobs.get( 0 ).getLastTaskId() );
    }

    private static NodeListEntry listEntry( final Node node )
    {
        return new NodeListEntry( node.id(), node.path(), Instant.parse( "2026-01-01T10:00:00Z" ) );
    }

    private static Node jobNode( final String name, final ScheduleCalendarType calendarType, final Instant lastRun )
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = jobData.newSet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, calendarType.name() );
        if ( calendarType == ScheduleCalendarType.CRON )
        {
            calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "* * * * *" );
            calendar.addString( ScheduledJobPropertyNames.CALENDAR_TIMEZONE, "UTC" );
        }
        else
        {
            calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "2026-01-01T09:00:00Z" );
        }

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, "app:key" );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, true );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, jobData.newSet() );
        if ( lastRun != null )
        {
            jobData.setInstant( ScheduledJobPropertyNames.LAST_RUN, lastRun );
        }

        return Node.create()
            .id( NodeId.from( name ) )
            .name( name )
            .parentPath( NodePath.ROOT )
            .data( jobData )
            .nodeVersionId( new NodeVersionId() )
            .build();
    }
}
