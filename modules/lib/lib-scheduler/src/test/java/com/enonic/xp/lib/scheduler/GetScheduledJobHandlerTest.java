package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    public void testExample()
    {
        mockOneTimeCalendar();
        runScript( "/lib/xp/examples/scheduler/get.js" );
    }

    @Test
    public void getJob()
        throws Exception
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/GetScheduledJobHandlerTest.js", "getJob" );
    }

    @Test
    public void getNotExist()
        throws Exception
    {

        runFunction( "/test/GetScheduledJobHandlerTest.js", "getNotExist" );
    }

    @Test
    public void getNull()
        throws Exception
    {

        runFunction( "/test/GetScheduledJobHandlerTest.js", "getNull" );
    }

}
