package com.enonic.xp.server.udc.impl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.concurrent.RecurringJob;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UdcServiceTest
{
    @Test
    void testLifecycle()
    {
        UdcScheduler udcSchedulerMock = mock( UdcScheduler.class );
        UdcConfig config = mock( UdcConfig.class );
        final RecurringJob recurringJob = mock( RecurringJob.class );

        when( config.url() ).thenReturn( "http://localhost:8080" );
        when( udcSchedulerMock.scheduleWithFixedDelay( notNull() ) ).thenReturn( recurringJob );

        UdcService service = new UdcService( udcSchedulerMock, config );

        service.activate();
        verify( udcSchedulerMock ).scheduleWithFixedDelay( notNull() );

        service.deactivate();
        verify( recurringJob ).cancel();
    }
}
