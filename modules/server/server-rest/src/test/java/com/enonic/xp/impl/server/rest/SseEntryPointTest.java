package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Version;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.event.Event;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ListApplicationJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SseEntryPointTest
{
    private SseEntryPoint listener;

    private ApplicationService applicationService;

    private Sse sse;

    private SseBroadcaster broadcaster;

    private OutboundSseEvent.Builder eventBuilder;

    @BeforeEach
    void setUp()
    {
        applicationService = mock( ApplicationService.class );
        listener = new SseEntryPoint( applicationService );
        sse = mock( Sse.class );
        broadcaster = mock( SseBroadcaster.class );
        eventBuilder = mock( OutboundSseEvent.Builder.class );

        when( sse.newBroadcaster() ).thenReturn( broadcaster );
        when( sse.newEventBuilder() ).thenReturn( eventBuilder );
        when( eventBuilder.name( anyString() ) ).thenReturn( eventBuilder );
        when( eventBuilder.id( anyString() ) ).thenReturn( eventBuilder );
        when( eventBuilder.mediaType( any( MediaType.class ) ) ).thenReturn( eventBuilder );
        when( eventBuilder.data( eq( ApplicationInfoJson.class ), any() ) ).thenReturn( eventBuilder );
        when( eventBuilder.data( eq( ListApplicationJson.class ), any() ) ).thenReturn( eventBuilder );
        when( eventBuilder.build() ).thenReturn( mock( OutboundSseEvent.class ) );
    }

    @Test
    void testOnEvent()
    {
        listener.setSse( sse );

        final Application application = mock( Application.class );

        when( application.getKey() ).thenReturn( ApplicationKey.from( "appKey" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );

        when( applicationService.getInstalledApplication( any( ApplicationKey.class ) ) ).thenReturn( application );
        when( applicationService.isLocalApplication( any( ApplicationKey.class ) ) ).thenReturn( false );

        final ArgumentCaptor<ApplicationInfoJson> applicationInfoCaptor = ArgumentCaptor.forClass( ApplicationInfoJson.class );

        listener.onEvent( Event.create( "application.cluster" ).
            localOrigin( true ).
            value( "id", UUID.randomUUID().toString() ).
            value( "eventType", "installed" ).
            value( "key", "appKey" ).
            build() );

        verify( sse, times( 1 ) ).newEventBuilder();
        verify( applicationService, times( 1 ) ).getInstalledApplication( any( ApplicationKey.class ) );
        verify( applicationService, times( 1 ) ).isLocalApplication( any( ApplicationKey.class ) );
        verify( eventBuilder ).data( eq( ApplicationInfoJson.class ), applicationInfoCaptor.capture() );
        verify( broadcaster, times( 1 ) ).broadcast( any( OutboundSseEvent.class ) );

        final ApplicationInfoJson actualApplication = applicationInfoCaptor.getValue();

        assertNotNull( actualApplication );
        assertEquals( "appKey", actualApplication.getKey() );
        assertFalse( actualApplication.getLocal() );
    }

    @Test
    void testOnEvent_AppNotFound()
    {
        listener.setSse( sse );

        when( applicationService.getInstalledApplication( any( ApplicationKey.class ) ) ).thenReturn( null );

        listener.onEvent( Event.create( "application.cluster" ).
            localOrigin( true ).
            value( "id", UUID.randomUUID().toString() ).
            value( "eventType", "installed" ).
            value( "key", "appKey" ).
            build() );

        verify( applicationService ).getInstalledApplication( any( ApplicationKey.class ) );
        verifyNoInteractions( broadcaster );
    }

    @Test
    void testOnEvent_Uninstalled()
    {
        listener.setSse( sse );

        listener.onEvent( Event.create( "application.cluster" ).
            localOrigin( true ).
            value( "id", UUID.randomUUID().toString() ).
            value( "eventType", "uninstalled" ).
            value( "key", "appKey" ).
            build() );

        verify( broadcaster, times( 1 ) ).broadcast( any( OutboundSseEvent.class ) );
    }

    @Test
    void testOnEvent_UnsupportedEventType()
    {
        listener.setSse( sse );

        listener.onEvent( Event.create( "application.cluster" ).
            localOrigin( true ).
            value( "id", UUID.randomUUID().toString() ).
            value( "eventType", "start" ).
            value( "key", "appKey" ).
            build() );
    }

    @Test
    void testOnEvent_NoSseContext()
    {
        listener.onEvent( Event.create( "application.cluster" ).
            localOrigin( true ).
            value( "id", UUID.randomUUID().toString() ).
            value( "eventType", "installed" ).
            value( "key", "appKey" ).
            build() );
    }

    @Test
    void testSubscribe()
    {
        listener.setSse( sse );

        final SseEventSink eventSink = mock( SseEventSink.class );

        doNothing().when( broadcaster ).register( eventSink );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "appKey" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );

        final Applications applications = Applications.from( application );

        when( applicationService.getInstalledApplications() ).thenReturn( applications );
        when( applicationService.isLocalApplication( any( ApplicationKey.class ) ) ).thenReturn( false );

        final ArgumentCaptor<ListApplicationJson> listApplicationCaptor = ArgumentCaptor.forClass( ListApplicationJson.class );

        listener.subscribe( eventSink );

        verify( broadcaster, times( 1 ) ).register( eventSink );
        verify( sse, times( 1 ) ).newEventBuilder();
        verify( eventBuilder ).data( eq( ListApplicationJson.class ), listApplicationCaptor.capture() );
        verify( applicationService, times( 1 ) ).getInstalledApplications();
        verify( applicationService, times( 1 ) ).isLocalApplication( any( ApplicationKey.class ) );

        final List<ApplicationInfoJson> actualApplications = listApplicationCaptor.getValue().getApplications();

        assertNotNull( actualApplications );
        assertFalse( actualApplications.isEmpty() );
        assertEquals( "appKey", actualApplications.get( 0 ).getKey() );
        assertFalse( actualApplications.get( 0 ).getLocal() );
    }

    @Test
    void testSubscribe_NoSseContext()
    {
        final SseEventSink eventSink = mock( SseEventSink.class );

        assertThrows( IllegalStateException.class, () -> listener.subscribe( eventSink ) );
    }

    @Test
    void testLifecycle()
    {
        listener.setSse( sse );
        listener.deactivate();
    }

}
