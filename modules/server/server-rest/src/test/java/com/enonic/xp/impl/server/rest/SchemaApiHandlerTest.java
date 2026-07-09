package com.enonic.xp.impl.server.rest;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonParseException;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.NamespaceNotFoundException;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.exception.DuplicateElementException;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.UpdateStylesParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.User;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchemaApiHandlerTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    private SchemaService schemaService;

    private SchemaApiHandler handler;

    @BeforeEach
    void setUp()
    {
        schemaService = mock( SchemaService.class );
        handler = new SchemaApiHandler( schemaService );
    }

    private static WebRequest request( final HttpMethod method, final String path )
    {
        final WebRequest request = new WebRequest();
        request.setMethod( method );
        request.setRawPath( "/_/server:schema" + path );
        return request;
    }

    private static WebRequest request( final HttpMethod method, final String path, final String body )
    {
        final WebRequest request = request( method, path );
        request.setBody( body );
        return request;
    }

    private static Resource mockResource()
    {
        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( "resource text" );
        when( resource.getTimestamp() ).thenReturn( 1690000000000L );
        return resource;
    }

    private static String body( final WebResponse response )
    {
        return (String) response.getBody();
    }

    @Test
    void listApps()
    {
        when( schemaService.listApplicationKeys() ).thenReturn(
            ApplicationKeys.from( ApplicationKey.from( "app1" ), ApplicationKey.from( "app2" ) ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/apps" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "app1" ) );
        assertTrue( body( response ).contains( "app2" ) );
    }

    @Test
    void appsMethodNotAllowed()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/apps" ) );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
        assertTrue( body( response ).contains( "\"status\":405" ) );
    }

    @Test
    void listNamespaces()
    {
        when( schemaService.listNamespaces() ).thenReturn(
            List.of( Namespace.create().key( ApplicationKey.from( "my.namespace" ) ).description( "my description" ).build() ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/namespaces" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "my.namespace" ) );
        assertTrue( body( response ).contains( "my description" ) );
    }

    @Test
    void getNamespace()
    {
        when( schemaService.getNamespace( ApplicationKey.from( "my.namespace" ) ) ).thenReturn(
            Namespace.create().key( ApplicationKey.from( "my.namespace" ) ).build() );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/namespaces/my.namespace" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "my.namespace" ) );
    }

    @Test
    void getNamespaceNotFound()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/namespaces/my.namespace" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
        assertTrue( body( response ).contains( "\"status\":404" ) );
    }

    @Test
    void createNamespace()
    {
        when( schemaService.createNamespace( any() ) ).thenReturn(
            Namespace.create().key( ApplicationKey.from( "my.namespace" ) ).description( "my description" ).build() );

        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/namespaces", "{\"key\":\"my.namespace\",\"description\":\"my description\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreateNamespaceParams> captor = ArgumentCaptor.forClass( CreateNamespaceParams.class );
        verify( schemaService ).createNamespace( captor.capture() );
        assertEquals( ApplicationKey.from( "my.namespace" ), captor.getValue().getKey() );
        assertEquals( "my description", captor.getValue().getDescription() );
    }

    @Test
    void createNamespaceMissingKey()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/namespaces", "{\"description\":\"text\"}" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void createNamespaceBadBody()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/namespaces", "not a json" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void createNamespaceConflict()
    {
        when( schemaService.createNamespace( any() ) ).thenThrow( new DuplicateElementException( "already exists" ) );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/namespaces", "{\"key\":\"my.namespace\"}" ) );

        assertEquals( HttpStatus.CONFLICT, response.getStatus() );
        assertTrue( body( response ).contains( "\"status\":409" ) );
    }

    @Test
    void updateNamespace()
    {
        when( schemaService.updateNamespace( any() ) ).thenReturn(
            Namespace.create().key( ApplicationKey.from( "my.namespace" ) ).description( "updated" ).build() );

        final WebResponse response =
            handler.handle( request( HttpMethod.PUT, "/namespaces/my.namespace", "{\"description\":\"updated\"}" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );

        final ArgumentCaptor<UpdateNamespaceParams> captor = ArgumentCaptor.forClass( UpdateNamespaceParams.class );
        verify( schemaService ).updateNamespace( captor.capture() );
        assertEquals( ApplicationKey.from( "my.namespace" ), captor.getValue().getKey() );
        assertEquals( "updated", captor.getValue().getDescription() );
    }

    @Test
    void updateNamespaceMissing()
    {
        when( schemaService.updateNamespace( any() ) ).thenThrow(
            new NamespaceNotFoundException( ApplicationKey.from( "my.namespace" ) ) );

        final WebResponse response = handler.handle( request( HttpMethod.PUT, "/namespaces/my.namespace", "{}" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void deleteNamespace()
    {
        when( schemaService.deleteNamespace( ApplicationKey.from( "my.namespace" ) ) ).thenReturn( true );

        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/namespaces/my.namespace" ) );

        assertEquals( HttpStatus.NO_CONTENT, response.getStatus() );
    }

    @Test
    void deleteNamespaceMissing()
    {
        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/namespaces/my.namespace" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    private SchemaResult<ContentType> contentTypeResult()
    {
        final ContentType contentType = mock( ContentType.class );
        when( contentType.getName() ).thenReturn( ContentTypeName.from( APP, "article" ) );
        return new SchemaResult<>( contentType, mockResource() );
    }

    @Test
    void listSchemas()
    {
        final SchemaResult<ContentType> result = contentTypeResult();
        when( schemaService.listContentTypes( any() ) ).thenReturn( List.of( result ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/schemas/myapp/content-type" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "myapp:article" ) );
        assertTrue( body( response ).contains( "content-type" ) );

        verify( schemaService ).listContentTypes( APP );
    }

    @Test
    void listSchemasBadType()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/schemas/myapp/unknown-type" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void listSchemasMethodNotAllowed()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/schemas/myapp/content-type" ) );

        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, response.getStatus() );
    }

    @Test
    void getSchema()
    {
        final SchemaResult<ContentType> result = contentTypeResult();
        when( schemaService.getContentType( ContentTypeName.from( APP, "article" ) ) ).thenReturn( result );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/schemas/myapp/content-type/article" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "myapp:article" ) );
        assertTrue( body( response ).contains( "resource text" ) );
        assertTrue( body( response ).contains( "2023-07-22" ) );
    }

    @Test
    void getSchemaNotFound()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/schemas/myapp/content-type/article" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void createSchema()
    {
        final SchemaResult<ContentType> result = contentTypeResult();
        when( schemaService.createContentType( any() ) ).thenReturn( result );

        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/schemas/myapp/content-type/article", "{\"resource\":\"kind: ContentType\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreateContentSchemaParams> captor = ArgumentCaptor.forClass( CreateContentSchemaParams.class );
        verify( schemaService ).createContentType( captor.capture() );
        assertEquals( ContentTypeName.from( APP, "article" ), captor.getValue().getName() );
        assertEquals( "kind: ContentType", captor.getValue().getResource() );
    }

    @Test
    void createSchemaMissingResource()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/schemas/myapp/content-type/article", "{}" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void createSchemaParseError()
    {
        when( schemaService.createContentType( any() ) ).thenThrow(
            new RuntimeException( new JsonParseException( null, "invalid yaml" ) ) );

        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/schemas/myapp/content-type/article", "{\"resource\":\"garbage\"}" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void updateSchemaMissing()
    {
        when( schemaService.updateContentType( any() ) ).thenThrow( new NodeNotFoundException( "node not found" ) );

        final WebResponse response = handler.handle(
            request( HttpMethod.PUT, "/schemas/myapp/content-type/article", "{\"resource\":\"kind: ContentType\"}" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void deleteSchema()
    {
        when( schemaService.deleteContentType( ContentTypeName.from( APP, "article" ) ) ).thenReturn( true );

        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/schemas/myapp/content-type/article" ) );

        assertEquals( HttpStatus.NO_CONTENT, response.getStatus() );
    }

    @Test
    void deleteSchemaMissing()
    {
        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/schemas/myapp/content-type/article" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void getComponent()
    {
        final PartDescriptor descriptor = mock( PartDescriptor.class );
        when( descriptor.getKey() ).thenReturn( DescriptorKey.from( APP, "mypart" ) );
        final SchemaResult<PartDescriptor> result = new SchemaResult<>( descriptor, mockResource() );
        when( schemaService.getPart( DescriptorKey.from( APP, "mypart" ) ) ).thenReturn( result );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/components/myapp/part/mypart" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "myapp:mypart" ) );
        assertTrue( body( response ).contains( "\"type\":\"part\"" ) );
    }

    @Test
    void createComponent()
    {
        final LayoutDescriptor descriptor = mock( LayoutDescriptor.class );
        when( descriptor.getKey() ).thenReturn( DescriptorKey.from( APP, "mylayout" ) );
        final SchemaResult<LayoutDescriptor> result = new SchemaResult<>( descriptor, mockResource() );
        when( schemaService.createLayout( any() ) ).thenReturn( result );

        final WebResponse response = handler.handle(
            request( HttpMethod.POST, "/components/myapp/layout/mylayout", "{\"resource\":\"kind: Layout\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreateComponentParams> captor = ArgumentCaptor.forClass( CreateComponentParams.class );
        verify( schemaService ).createLayout( captor.capture() );
        assertEquals( DescriptorKey.from( APP, "mylayout" ), captor.getValue().getKey() );
        assertEquals( "kind: Layout", captor.getValue().getResource() );
    }

    @Test
    void componentBadType()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/components/myapp/unknown/mypart" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    private SchemaResult<MacroDescriptor> macroResult()
    {
        final MacroDescriptor descriptor = mock( MacroDescriptor.class );
        when( descriptor.getKey() ).thenReturn( MacroKey.from( APP, "mymacro" ) );
        return new SchemaResult<>( descriptor, mockResource() );
    }

    @Test
    void getMacro()
    {
        final SchemaResult<MacroDescriptor> result = macroResult();
        when( schemaService.getMacro( any() ) ).thenReturn( result );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/macros/myapp/mymacro" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "myapp:mymacro" ) );
    }

    @Test
    void createMacro()
    {
        final SchemaResult<MacroDescriptor> result = macroResult();
        when( schemaService.createMacro( any() ) ).thenReturn( result );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/macros/myapp/mymacro", "{\"resource\":\"kind: Macro\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreateMacroParams> captor = ArgumentCaptor.forClass( CreateMacroParams.class );
        verify( schemaService ).createMacro( captor.capture() );
        assertEquals( MacroKey.from( APP, "mymacro" ), captor.getValue().getKey() );
    }

    @Test
    void deleteMacroMissing()
    {
        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/macros/myapp/mymacro" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void getStylesNotFound()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/styles/myapp" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void createStyles()
    {
        final Resource resource = mockResource();
        when( schemaService.createStyles( any() ) ).thenReturn( new SchemaResult<>( null, resource ) );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/styles/myapp", "{\"resource\":\"kind: Styles\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );
        assertTrue( body( response ).contains( "\"application\":\"myapp\"" ) );

        final ArgumentCaptor<CreateStylesParams> captor = ArgumentCaptor.forClass( CreateStylesParams.class );
        verify( schemaService ).createStyles( captor.capture() );
        assertEquals( APP, captor.getValue().getKey() );
        assertEquals( "kind: Styles", captor.getValue().getResource() );
    }

    @Test
    void updateStyles()
    {
        final Resource resource = mockResource();
        when( schemaService.updateStyles( any() ) ).thenReturn( new SchemaResult<>( null, resource ) );

        final WebResponse response = handler.handle( request( HttpMethod.PUT, "/styles/myapp", "{\"resource\":\"kind: Styles\"}" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );

        final ArgumentCaptor<UpdateStylesParams> captor = ArgumentCaptor.forClass( UpdateStylesParams.class );
        verify( schemaService ).updateStyles( captor.capture() );
        assertEquals( "kind: Styles", captor.getValue().getResource() );
    }

    @Test
    void deleteStyles()
    {
        when( schemaService.deleteStyles( APP ) ).thenReturn( true );

        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/styles/myapp" ) );

        assertEquals( HttpStatus.NO_CONTENT, response.getStatus() );
    }

    @Test
    void getCms()
    {
        final Resource resource = mockResource();
        when( schemaService.getCmsDescriptor( APP ) ).thenReturn( new SchemaResult<>( null, resource ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/cms/myapp" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "\"application\":\"myapp\"" ) );
        assertTrue( body( response ).contains( "resource text" ) );
    }

    @Test
    void deleteCmsMissing()
    {
        final WebResponse response = handler.handle( request( HttpMethod.DELETE, "/cms/myapp" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    private static Resource mockPhrasesResource()
    {
        final Resource resource = mockResource();
        when( resource.getKey() ).thenReturn( ResourceKey.from( "myapp:/cms/i18n/phrases/phrases_en.properties" ) );
        return resource;
    }

    @Test
    void listPhrases()
    {
        final Resource resource = mockPhrasesResource();
        when( schemaService.listPhrases( APP ) ).thenReturn( List.of( resource ) );

        final WebResponse response = handler.handle( request( HttpMethod.GET, "/phrases/myapp" ) );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertTrue( body( response ).contains( "phrases_en.properties" ) );
    }

    @Test
    void createPhrases()
    {
        final Resource resource = mockPhrasesResource();
        when( schemaService.createPhrases( any() ) ).thenReturn( resource );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/phrases/myapp/en", "{\"resource\":\"key=value\"}" ) );

        assertEquals( HttpStatus.CREATED, response.getStatus() );

        final ArgumentCaptor<CreatePhrasesParams> captor = ArgumentCaptor.forClass( CreatePhrasesParams.class );
        verify( schemaService ).createPhrases( captor.capture() );
        assertEquals( APP, captor.getValue().getKey() );
        assertEquals( "en", captor.getValue().getName() );
        assertEquals( "key=value", captor.getValue().getResource() );
    }

    @Test
    void getPhrasesNotFound()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/phrases/myapp/en" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void handleForbidden()
    {
        when( schemaService.createNamespace( any() ) ).thenThrow( new ForbiddenAccessException( User.anonymous() ) );

        final WebResponse response = handler.handle( request( HttpMethod.POST, "/namespaces", "{\"key\":\"my.namespace\"}" ) );

        assertEquals( HttpStatus.FORBIDDEN, response.getStatus() );
        assertTrue( body( response ).contains( "\"status\":403" ) );
    }

    @Test
    void handleUnknownPath()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/unknown" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }

    @Test
    void handleRootPath()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "" ) );

        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }
}
