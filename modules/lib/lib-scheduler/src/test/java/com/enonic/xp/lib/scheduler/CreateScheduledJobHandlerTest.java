package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{

    @Test
    void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();
        runScript( "/lib/xp/examples/scheduler/create.js" );
    }


    @Test
    void createOneTimeJob()
    {
        mockOneTimeCalendar();
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createOneTimeJob" );
    }

    @Test
    void createCronJob()
    {
        mockCronCalendar();
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createCronJob" );
    }

    @Test
    void createWithoutName()
    {
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutName" );
    }

    @Test
    void createWithoutDescriptor()
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutDescriptor" );
    }

    @Test
    void createWithoutCalendar()
    {
        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutCalendar" );
    }

    @Test
    void createWithoutUser()
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutUser" );
    }


    @Test
    void createWithoutConfig()
    {
        mockCronCalendar();

        runFunction( "/test/CreateScheduledJobHandlerTest.js", "createWithoutConfig" );
    }
}
