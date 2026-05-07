package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.impl.server.rest.model.ApplicationInfoJson;
import com.enonic.xp.impl.server.rest.model.ApplicationParams;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationApiHandlerTest
{
    private ApplicationResourceService applicationResourceService;

    private MultipartService multipartService;

    private SseManager sseManager;

    private ApplicationApiHandler handler;

    @BeforeEach
    void setUp()
    {
        applicationResourceService = mock( ApplicationResourceService.class );
        multipartService = mock( MultipartService.class );
        sseManager = mock( SseManager.class );
        handler = new ApplicationApiHandler( applicationResourceService, multipartService, sseManager );
    }

    @Test
    void handleEvents()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.GET );
        request.setRawPath( "/_/server:app/events" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertNotNull( response.getSse() );
    }

    @Test
    void handleMethodNotAllowed()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.PUT );
        request.setRawPath( "/_/server:app/installUrl" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
    }

    @Test
    void handleNotFound()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/unknown" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void handleInstall()
    {
        final Application application = createApplication();
        final ApplicationInfoJson resultJson = ApplicationInfoJson.create( application, null, false );

        final MultipartForm form = mock( MultipartForm.class );
        when( multipartService.parse( any() ) ).thenReturn( form );
        when( applicationResourceService.install( form ) ).thenReturn( resultJson );

        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/install" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).install( form );
    }

    @Test
    void handleInstallUrl()
    {
        final Application application = createApplication();
        final ApplicationInfoJson resultJson = ApplicationInfoJson.create( application, null, false );
        when( applicationResourceService.installUrl( any() ) ).thenReturn( resultJson );

        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/installUrl" );
        request.setBody( "{\"URL\":\"https://enonic.net\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final String body = response.getBody().toString();
        assertTrue( body.contains( "\"key\":\"testapplication\"" ) );
    }

    @Test
    void handleInstallUrlBadRequest()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/installUrl" );
        request.setBody( "not json" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void handleStart()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/start" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).start( any() );
    }

    @Test
    void handleStop()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/stop" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        verify( applicationResourceService ).stop( any() );
    }

    @Test
    void handleUninstall()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/uninstall" );
        request.setBody( "{\"key\":\"testapplication\"}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final ArgumentCaptor<ApplicationParams> captor = ArgumentCaptor.forClass( ApplicationParams.class );
        verify( applicationResourceService ).uninstall( captor.capture() );
        assertEquals( Set.of( "testapplication" ), captor.getValue().getKey() );
    }

    @Test
    void handleUninstallMultiple()
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.POST );
        request.setRawPath( "/_/server:app/uninstall" );
        request.setBody( "{\"key\":[\"app1\",\"app2\"]}" );

        final WebResponse response = handler.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        final ArgumentCaptor<ApplicationParams> captor = ArgumentCaptor.forClass( ApplicationParams.class );
        verify( applicationResourceService ).uninstall( captor.capture() );
        assertEquals( Set.of( "app1", "app2" ), captor.getValue().getKey() );
    }

    @Test
    void onSseEvent_open_addsToGroupAndSendsList()
    {
        when( applicationResourceService.getInstalledApplications() ).thenReturn( List.of() );

        final UUID clientId = UUID.randomUUID();
        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( clientId )
            .attributes( GenericValue.newObject().build() )
            .build();

        handler.onSseEvent( event );

        verify( sseManager ).addToGroup( "server:app:events", clientId );
        verify( sseManager ).send( eq( clientId ), any( SseMessage.class ) );
    }

    @Test
    void onSseEvent_close_isIgnored()
    {
        final SseEvent event = SseEvent.create()
            .type( SseEventType.CLOSE )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();

        handler.onSseEvent( event );

        verify( sseManager, never() ).addToGroup( any(), any() );
        verify( sseManager, never() ).send( any(), any() );
    }

    @Test
    void onEvent_wrongType_ignored()
    {
        final Event event = Event.create( "unknown.event" ).localOrigin( true ).value( "eventType", "installed" ).build();

        handler.onEvent( event );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    @Test
    void onEvent_nonLocalOrigin_ignored()
    {
        final Event event = Event.create( "application.cluster" ).localOrigin( false ).value( "eventType", "installed" ).build();

        handler.onEvent( event );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    @Test
    void onEvent_emptyGroup_skipsSend()
    {
        when( sseManager.getGroupSize( "server:app:events" ) ).thenReturn( 0 );

        final Event event = Event.create( "application.cluster" )
            .localOrigin( true )
            .value( "eventType", "installed" )
            .value( "key", "testapplication" )
            .build();

        handler.onEvent( event );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    @Test
    void onEvent_uninstalled_sendsToGroup()
    {
        when( sseManager.getGroupSize( "server:app:events" ) ).thenReturn( 1 );

        final Event event = Event.create( "application.cluster" )
            .localOrigin( true )
            .value( "eventType", "uninstalled" )
            .value( "key", "testapplication" )
            .build();

        handler.onEvent( event );

        verify( sseManager ).sendToGroup( eq( "server:app:events" ), any( SseMessage.class ) );
    }

    @Test
    void onEvent_installed_missingApplication_skipsSend()
    {
        when( sseManager.getGroupSize( "server:app:events" ) ).thenReturn( 1 );
        when( applicationResourceService.getInstalledApplication( any() ) ).thenReturn( null );

        final Event event = Event.create( "application.cluster" )
            .localOrigin( true )
            .value( "eventType", "installed" )
            .value( "key", "testapplication" )
            .build();

        handler.onEvent( event );

        verify( sseManager, never() ).sendToGroup( any(), any() );
    }

    @Test
    void onEvent_installed_sendsToGroup()
    {
        when( sseManager.getGroupSize( "server:app:events" ) ).thenReturn( 1 );
        final ApplicationInfoJson info = ApplicationInfoJson.create( createApplication(), null, false );
        when( applicationResourceService.getInstalledApplication( ApplicationKey.from( "testapplication" ) ) ).thenReturn( info );

        final Event event = Event.create( "application.cluster" )
            .localOrigin( true )
            .value( "eventType", "installed" )
            .value( "key", "testapplication" )
            .build();

        handler.onEvent( event );

        verify( sseManager ).sendToGroup( eq( "server:app:events" ), any( SseMessage.class ) );
    }

    @Test
    void onEvent_unknownSubType_ignored()
    {
        final Event event = Event.create( "application.cluster" )
            .localOrigin( true )
            .value( "eventType", "bogus" )
            .value( "key", "testapplication" )
            .build();

        handler.onEvent( event );

        verify( sseManager, never() ).sendToGroup( any(), any() );
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
}
