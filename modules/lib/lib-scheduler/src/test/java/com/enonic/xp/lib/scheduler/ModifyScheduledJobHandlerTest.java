package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModifyScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runScript( "/lib/xp/examples/scheduler/modify.js" );
    }

    @Test
    void modifyJob()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyJob" );
    }

    @Test
    void modifyJobWithNull()
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyJobWithNull" );
    }

    @Test
    void modifyDescriptorWithNull()
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyDescriptorWithNull" );
    }

    @Test
    void modifyConfigWithNull()
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyConfigWithNull" );
    }

    @Test
    void modifyCalendarWithNull()
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyCalendarWithNull" );
    }

    @Test
    void modifyEnabledWithNull()
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyEnabledWithNull" );
    }

}
