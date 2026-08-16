package com.enonic.xp.impl.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.DeleteNodeResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.ScheduledJobName;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceImplTest
{
    private static final ScheduledJobName JOB = ScheduledJobName.from( "job1" );

    @Mock
    private NodeService nodeService;

    @Mock
    private SchedulingCoordinator schedulingCoordinator;

    @Mock
    private ScheduleAuditLogSupport auditLogSupport;

    @Test
    void deleteSucceedsWhenThePlanCannotBeDiscarded()
    {
        when( nodeService.delete( isA( DeleteNodeParams.class ) ) ).thenReturn(
            DeleteNodeResult.create().add( new DeleteNodeResult.Result( NodeId.from( "abc" ), new NodeVersionId() ) ).build() );
        doThrow( new IllegalStateException( "no hazelcast" ) ).when( schedulingCoordinator ).forget( JOB );

        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( nodeService, schedulingCoordinator, auditLogSupport );

        // the job is gone from storage either way, so the caller is not told it failed - and the
        // plan left behind is dropped by the next tick that lists jobs
        assertTrue( schedulerService.delete( JOB ) );
        verify( auditLogSupport, times( 1 ) ).delete( JOB, true );
    }
}
