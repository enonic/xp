package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    void testExample()
    {
        mockOneTimeCalendar();

        runScript( "/lib/xp/examples/scheduler/delete.js" );
    }

    @Test
    void deleteJob()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteJob" );
    }

    @Test
    void deleteNotExist()
    {
        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteNotExist" );
    }

    @Test
    void deleteNull()
    {
        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteNull" );
    }

}
