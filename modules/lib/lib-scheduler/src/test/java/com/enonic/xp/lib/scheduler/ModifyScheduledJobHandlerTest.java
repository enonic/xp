package com.enonic.xp.lib.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ModifyScheduledJobHandlerTest
    extends BaseScheduledJobHandlerTest
{
    @Test
    public void testExample()
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runScript( "/lib/xp/examples/scheduler/modify.js" );
    }

    @Test
    public void modifyJob()
        throws Exception
    {
        mockOneTimeCalendar();
        mockCronCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyJob" );
    }

    @Test
    public void modifyJobWithNull()
        throws Exception
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyJobWithNull" );
    }

    @Test
    public void modifyDescriptorWithNull()
        throws Exception
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyDescriptorWithNull" );
    }

    @Test
    public void modifyConfigWithNull()
        throws Exception
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyConfigWithNull" );
    }

    @Test
    public void modifyCalendarWithNull()
        throws Exception
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyCalendarWithNull" );
    }

    @Test
    public void modifyEnabledWithNull()
        throws Exception
    {
        mockOneTimeCalendar();

        runFunction( "/test/ModifyScheduledJobHandlerTest.js", "modifyEnabledWithNull" );
    }

}
