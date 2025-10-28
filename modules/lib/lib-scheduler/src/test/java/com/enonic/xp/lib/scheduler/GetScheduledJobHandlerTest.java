package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.scheduler.ScheduledJobName;

@ExtendWith(MockitoExtension.class)
class GetScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    void testExample()
    {
        mockOneTimeCalendar();
        runScript( "/lib/xp/examples/scheduler/get.js" );
    }

    @Test
    void getJob()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/GetScheduledJobHandlerTest.js", "createJob" );

        updateLastRun( ScheduledJobName.from( "myjob" ) );

        runFunction( "/test/GetScheduledJobHandlerTest.js", "getJob" );
    }

    @Test
    void getNotExist()
    {
        runFunction( "/test/GetScheduledJobHandlerTest.js", "getNotExist" );
    }

    @Test
    void getNull()
    {
        runFunction( "/test/GetScheduledJobHandlerTest.js", "getNull" );
    }
}
