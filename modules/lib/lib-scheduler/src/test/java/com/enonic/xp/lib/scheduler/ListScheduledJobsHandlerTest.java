package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListScheduledJobsHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runScript( "/lib/xp/examples/scheduler/list.js" );
    }


    @Test
    void listJobs()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/ListScheduledJobsHandlerTest.js", "listJobs" );
    }

}
