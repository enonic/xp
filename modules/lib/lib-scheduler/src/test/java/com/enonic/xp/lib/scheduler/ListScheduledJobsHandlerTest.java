package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ListScheduledJobsHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    public void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runScript( "/lib/xp/examples/scheduler/list.js" );
    }


    @Test
    public void listJobs()
        throws Exception
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/ListScheduledJobsHandlerTest.js", "listJobs" );
    }

}
