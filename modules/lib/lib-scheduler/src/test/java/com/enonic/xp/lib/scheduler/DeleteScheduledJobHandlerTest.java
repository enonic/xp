package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    public void testExample()
    {
        mockOneTimeCalendar();

        runScript( "/lib/xp/examples/scheduler/delete.js" );
    }

    @Test
    public void deleteJob()
        throws Exception
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteJob" );
    }

    @Test
    public void deleteNotExist()
        throws Exception
    {
        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteNotExist" );
    }

    @Test
    public void deleteNull()
        throws Exception
    {
        runFunction( "/test/DeleteScheduledJobHandlerTest.js", "deleteNull" );
    }

}
