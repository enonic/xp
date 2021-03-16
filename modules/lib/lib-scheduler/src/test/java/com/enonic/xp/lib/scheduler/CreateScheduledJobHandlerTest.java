package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{

    @Test
    public void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();
        runScript( "/lib/xp/examples/scheduler/create.js" );
    }


    @Test
    public void createOneTimeJob()
        throws Exception
    {
        mockOneTimeCalendar();
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createOneTimeJob" );
    }

    @Test
    public void createCronJob()
        throws Exception
    {
        mockCronCalendar();
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createCronJob" );
    }

    @Test
    public void createWithoutName()
        throws Exception
    {
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutName" );
    }

    @Test
    public void createWithoutDescriptor()
        throws Exception
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutDescriptor" );
    }

    @Test
    public void createWithoutCalendar()
        throws Exception
    {
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutCalendar" );
    }

    @Test
    public void createWithoutUser()
        throws Exception
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutUser" );
    }


    @Test
    public void createWithoutPayload()
        throws Exception
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutPayload" );
    }
}
