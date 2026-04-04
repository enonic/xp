package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationJson;
import com.enonic.xp.impl.server.rest.model.ListApplicationJson;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.multipart.MultipartForm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ApplicationResourceTest
    extends JaxRsResourceTestSupport
{
    private ApplicationResourceService applicationResourceService;

    @Override
    protected ApplicationResource getResourceInstance()
    {
        applicationResourceService = mock( ApplicationResourceService.class );

        return new ApplicationResource( applicationResourceService );
    }

    @Test
    void install()
    {
        ApplicationResource resource = getResourceInstance();

        MultipartForm multipartForm = mock( MultipartForm.class );

        resource.install( multipartForm );

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( applicationResourceService ).install( multipartForm );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void install_url()
        throws Exception
    {
        final Application application = createApplication();
        final ApplicationDescriptor descriptor = ApplicationDescriptor.create()
            .key( ApplicationKey.from( "testapplication" ) )
            .title( "application name" )
            .url( "https://enonic.net" )
            .vendorName( "Enonic" )
            .vendorUrl( "https://www.enonic.com" )
            .build();
        final ApplicationInfoJson infoJson = ApplicationInfoJson.create( application, descriptor, false );
        when( this.applicationResourceService.installUrl( any() ) ).thenReturn( infoJson );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"https://enonic.net\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertJson( "install_url_result.json", jsonString );
    }

    @Test
    void install_url_process_error()
        throws Exception
    {
        when( this.applicationResourceService.installUrl( any() ) ).thenThrow(
            new RuntimeException( "Connection refused" ) );

        String jsonString = request().path( "app/installUrl" )
            .entity( "{\"URL\":\"https://enonic.net\"}", MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application\"}", jsonString );
    }

    @Test
    void install_invalid_url()
        throws Exception
    {
        when( this.applicationResourceService.installUrl( any() ) ).thenThrow(
            new RuntimeException( "Invalid URL" ) );

        String jsonString =
            request().path( "app/installUrl" ).entity( "{\"URL\":\"invalid url\"}", MediaType.APPLICATION_JSON_TYPE ).post().getAsString();

        assertEquals( "{\"failure\":\"Failed to upload application\"}", jsonString );
    }

    @Test
    void test_start_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/start" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).start( any() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_stop_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/stop" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).stop( any() );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void test_uninstall_application()
        throws Exception
    {
        final Application application = createApplication();

        request().path( "app/uninstall" ).entity( "{\"key\": \"" + application.getKey() + "\" }", MediaType.APPLICATION_JSON_TYPE ).post();

        final InOrder inOrder = Mockito.inOrder( applicationResourceService );
        inOrder.verify( this.applicationResourceService ).uninstall( any() );
        inOrder.verifyNoMoreInteractions();
    }

    private Application createApplication()
    {
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "testapplication" ) );
        when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
        when( application.getMinSystemVersion() ).thenReturn( "5.0" );
        when( application.getMaxSystemVersion() ).thenReturn( "5.1" );
        when( application.isStarted() ).thenReturn( true );
        when( application.getModifiedTime() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ) );

        return application;
    }

    @Nested
    class SseTest
    {
        private ApplicationResource resource;

        private ApplicationResourceService appResourceService;

        private Sse sse;

        private SseBroadcaster broadcaster;

        private OutboundSseEvent.Builder eventBuilder;

        @BeforeEach
        void setUp()
        {
            appResourceService = mock( ApplicationResourceService.class );
            resource = new ApplicationResource( appResourceService );
            sse = mock( Sse.class );
            broadcaster = mock( SseBroadcaster.class );
            eventBuilder = mock( OutboundSseEvent.Builder.class );

            when( sse.newBroadcaster() ).thenReturn( broadcaster );
            when( sse.newEventBuilder() ).thenReturn( eventBuilder );
            when( eventBuilder.name( anyString() ) ).thenReturn( eventBuilder );
            when( eventBuilder.id( anyString() ) ).thenReturn( eventBuilder );
            when( eventBuilder.mediaType( any( MediaType.class ) ) ).thenReturn( eventBuilder );
            when( eventBuilder.data( eq( ApplicationJson.class ), any() ) ).thenReturn( eventBuilder );
            when( eventBuilder.data( eq( ListApplicationJson.class ), any() ) ).thenReturn( eventBuilder );
            when( eventBuilder.build() ).thenReturn( mock( OutboundSseEvent.class ) );
        }

        @Test
        void onEvent()
        {
            resource.setSse( sse );

            final ApplicationInfoJson appInfo = mock( ApplicationInfoJson.class );
            when( appInfo.getKey() ).thenReturn( "appKey" );
            when( appInfo.getLocal() ).thenReturn( false );

            when( appResourceService.getInstalledApplication( any( ApplicationKey.class ) ) ).thenReturn( appInfo );

            final ArgumentCaptor<ApplicationJson> applicationCaptor = ArgumentCaptor.forClass( ApplicationJson.class );

            resource.onEvent( Event.create( "application.cluster" ).
                localOrigin( true ).
                value( "id", UUID.randomUUID().toString() ).
                value( "eventType", "installed" ).
                value( "key", "appKey" ).
                build() );

            verify( sse, times( 1 ) ).newEventBuilder();
            verify( appResourceService, times( 1 ) ).getInstalledApplication( any( ApplicationKey.class ) );
            verify( eventBuilder ).data( eq( ApplicationJson.class ), applicationCaptor.capture() );
            verify( broadcaster, times( 1 ) ).broadcast( any( OutboundSseEvent.class ) );

            final ApplicationJson actualApplication = applicationCaptor.getValue();

            assertNotNull( actualApplication );
            assertEquals( "appKey", actualApplication.getKey() );
            assertFalse( actualApplication.getLocal() );
        }

        @Test
        void onEvent_AppNotFound()
        {
            resource.setSse( sse );

            when( appResourceService.getInstalledApplication( any( ApplicationKey.class ) ) ).thenReturn( null );

            resource.onEvent( Event.create( "application.cluster" ).
                localOrigin( true ).
                value( "id", UUID.randomUUID().toString() ).
                value( "eventType", "installed" ).
                value( "key", "appKey" ).
                build() );

            verify( appResourceService ).getInstalledApplication( any( ApplicationKey.class ) );
            verifyNoInteractions( broadcaster );
        }

        @Test
        void onEvent_Uninstalled()
        {
            resource.setSse( sse );

            resource.onEvent( Event.create( "application.cluster" ).
                localOrigin( true ).
                value( "id", UUID.randomUUID().toString() ).
                value( "eventType", "uninstalled" ).
                value( "key", "appKey" ).
                build() );

            verify( broadcaster, times( 1 ) ).broadcast( any( OutboundSseEvent.class ) );
        }

        @Test
        void onEvent_UnsupportedEventType()
        {
            resource.setSse( sse );

            resource.onEvent( Event.create( "application.cluster" ).
                localOrigin( true ).
                value( "id", UUID.randomUUID().toString() ).
                value( "eventType", "start" ).
                value( "key", "appKey" ).
                build() );
        }

        @Test
        void onEvent_NoSseContext()
        {
            resource.onEvent( Event.create( "application.cluster" ).
                localOrigin( true ).
                value( "id", UUID.randomUUID().toString() ).
                value( "eventType", "installed" ).
                value( "key", "appKey" ).
                build() );
        }

        @Test
        void subscribe()
        {
            resource.setSse( sse );

            final SseEventSink eventSink = mock( SseEventSink.class );

            doNothing().when( broadcaster ).register( eventSink );

            final ApplicationInfoJson appInfo = mock( ApplicationInfoJson.class );
            when( appInfo.getKey() ).thenReturn( "appKey" );
            when( appInfo.getLocal() ).thenReturn( false );

            when( appResourceService.getInstalledApplications() ).thenReturn( List.of( appInfo ) );

            final ArgumentCaptor<ListApplicationJson> listApplicationCaptor =
                ArgumentCaptor.forClass( ListApplicationJson.class );

            resource.subscribe( eventSink );

            verify( broadcaster, times( 1 ) ).register( eventSink );
            verify( sse, times( 1 ) ).newEventBuilder();
            verify( eventBuilder ).data( eq( ListApplicationJson.class ), listApplicationCaptor.capture() );
            verify( appResourceService, times( 1 ) ).getInstalledApplications();

            final List<ApplicationJson> actualApplications = listApplicationCaptor.getValue().getApplications();

            assertNotNull( actualApplications );
            assertFalse( actualApplications.isEmpty() );
            assertEquals( "appKey", actualApplications.getFirst().getKey() );
            assertFalse( actualApplications.getFirst().getLocal() );
        }

        @Test
        void subscribe_NoSseContext()
        {
            final SseEventSink eventSink = mock( SseEventSink.class );

            assertThrows( IllegalStateException.class, () -> resource.subscribe( eventSink ) );
        }

        @Test
        void lifecycle()
        {
            resource.setSse( sse );
            resource.deactivate();
        }
    }
}
