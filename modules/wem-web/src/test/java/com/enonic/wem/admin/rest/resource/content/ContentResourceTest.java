package com.enonic.wem.admin.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.QueryFacet;
import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.MaximumOccurrencesValidationError;
import com.enonic.wem.api.schema.content.validator.MissingRequiredValueValidationError;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.web.servlet.ServletRequestHolder;

public class ContentResourceTest
    extends AbstractResourceTest
{
    private Client client;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @After
    public void after()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void get_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", new Value.DateTime( DateTime.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", new Value.WholeNumber( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", new Value.WholeNumber( 2 ) );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( Contents.from( aContent ) );

        String jsonString = resource().path( "content" ).queryParam( "path", "/my_a_content" ).get( String.class );

        assertJson( "get_content_by_path.json", jsonString );
    }

    @Test
    public void get_root_content()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( client.execute( Mockito.isA( GetRootContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list" ).get( String.class );

        assertJson( "list_content.json", jsonString );
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", new Value.Text( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", new Value.Text( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", new Value.DecimalNumber( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", new Value.DecimalNumber( 1.333 ) );

        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );

        final ContentData bContentData = bContent.getContentData();
        bContentData.setProperty( "myArrayOfSets[0].myFirstSetProperty1", new Value.Text( "1" ) );
        bContentData.setProperty( "myArrayOfSets[0].myFirstSetProperty2", new Value.Text( "a" ) );
        bContentData.setProperty( "myArrayOfSets[1].mySecondSetProperty1", new Value.Text( "2" ) );
        bContentData.setProperty( "myArrayOfSets[1].mySecondSetProperty2", new Value.Text( "b" ) );

        bContentData.setProperty( "myArrayOfArrays[0].myFirstArray[0]", new Value.Text( "1" ) );
        bContentData.setProperty( "myArrayOfArrays[0].myFirstArray[1]", new Value.Text( "2" ) );
        bContentData.setProperty( "myArrayOfArrays[1].mySecondArray[0]", new Value.Text( "3" ) );
        bContentData.setProperty( "myArrayOfArrays[1].mySecondArray[1]", new Value.Text( "4" ) );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString =
            resource().path( "content" ).queryParam( "contentIds", "aaa" ).queryParam( "contentIds", "bbb" ).get( String.class );

        assertJson( "get_content_by_id.json", jsonString );
    }

    @Test
    public void get_content_list()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list" ).queryParam( "path", "mymodule:/" ).get( String.class );

        assertJson( "list_content.json", jsonString );
    }

    @Test
    public void find_content_with_facets()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( client.execute( Mockito.isA( FindContent.class ) ) ).thenReturn(
            createContentIndexQueryResult( Contents.from( aContent, bContent ), true ) );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my_type" ) ) );

        Mockito.when( client.execute( Mockito.isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( createSpace( "my_space" ) ) );

        String jsonString = resource().path( "content/find" ).entity( readFromFile( "find_content_with_facets_params.json" ),
                                                                      MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "find_content_with_facets.json", jsonString );
    }

    @Test
    public void generate_name()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GenerateContentName.class ) ) ).thenReturn( "some-rea11y-werd-name" );

        String jsonString =
            resource().path( "content/generateName" ).queryParam( "displayName", "Some rea11y we!rd name..." ).get( String.class );

        assertJson( "generate_content_name.json", jsonString );
    }

    @Test
    public void validate_content_success()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my_type" ) ) );

        Mockito.when( client.execute( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        String jsonString = resource().path( "content/validate" ).
            entity( readFromFile( "validate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "validate_content_success.json", jsonString );
    }

    @Test
    public void validate_content_error()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my_type" ) ) );

        Mockito.when( client.execute( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( createDataValidationErrors() );

        String jsonString = resource().path( "content/validate" ).
            entity( readFromFile( "validate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "validate_content_error.json", jsonString );
    }

    @Test
    public void delete_content_success()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteContent.class ) ) ).thenReturn( DeleteContentResult.SUCCESS );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_success.json", jsonString );
    }

    @Test
    public void delete_content_failure()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteContent.class ) ) ).thenReturn( DeleteContentResult.NOT_FOUND,
                                                                                         DeleteContentResult.UNABLE_TO_DELETE );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_failure.json", jsonString );
    }

    @Test
    public void delete_content_both()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.isA( DeleteContent.class ) ) ).thenReturn( DeleteContentResult.SUCCESS,
                                                                                         DeleteContentResult.UNABLE_TO_DELETE );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_both.json", jsonString );
    }

    @Test
    public void create_content_exception()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        CreateContent command = new CreateContent().displayName( "Content One" ).parentContentPath( ContentPath.from( "parent-path" ) );
        Exception e = new Exception( "Exception occured." );

        Mockito.when( client.execute( Mockito.isA( CreateContent.class ) ) ).thenThrow( new CreateContentException( command, e ) );

        String jsonString = resource().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_content_exception.json", jsonString );
    }

    @Test
    public void create_content_success()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        ContentId contentId = ContentId.from( "content-id" );
        ContentPath parentPath = ContentPath.from( "parent-path" );
        ContentPath contentPath = ContentPath.from( parentPath, "content-path" );
        CreateContentResult result = new CreateContentResult( contentId, contentPath );

        Mockito.when( client.execute( Mockito.isA( CreateContent.class ) ) ).thenReturn( result );

        String jsonString = resource().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_content_success.json", jsonString );
    }

    @Test
    public void update_content_exception()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        UpdateContent command = new UpdateContent().selector( ContentId.from( "content-id" ) );
        Exception e = new Exception( "Exception occured." );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenThrow( new UpdateContentException( command, e ) );

        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.never() ).execute( Mockito.isA( RenameContent.class ) );

        assertJson( "update_content_exception.json", jsonString );
    }

    @Test
    public void update_content_failure()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        Exception e = new com.enonic.wem.api.content.ContentNotFoundException( ContentId.from( "content-id" ) );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenReturn( UpdateContentResult.from( e ) );

        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.never() ).execute( Mockito.isA( RenameContent.class ) );

        assertJson( "update_content_failure.json", jsonString );
    }

    @Test
    public void update_content_nothing_updated()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenReturn( null );

        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( RenameContent.class ) );

        assertJson( "update_content_nothing_updated.json", jsonString );
    }

    @Test
    public void update_content_success()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule", "my-type" ) ) );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenReturn( UpdateContentResult.SUCCESS );

        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( RenameContent.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final ContentResource resource = new ContentResource();
        resource.setClient( client );

        return resource;
    }

    private DataValidationErrors createDataValidationErrors()
    {
        List<DataValidationError> errors = new ArrayList<>( 2 );

        Input input = Input.newInput().name( "myInput" ).inputType( InputTypes.PHONE ).required( true ).maximumOccurrences( 3 ).build();
        Property property = Property.newProperty( "myProperty" ).type( ValueTypes.TEXT ).value( "myValue" ).build();

        errors.add( new MaximumOccurrencesValidationError( input, 5 ) );
        errors.add( new MissingRequiredValueValidationError( input, property ) );

        return DataValidationErrors.from( errors );
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( contentTypeName ) ).
            build();
    }

    private ContentIndexQueryResult createContentIndexQueryResult( Contents contents, boolean includeFacets )
    {
        ContentIndexQueryResult result = new ContentIndexQueryResult( contents.getSize() );
        for ( Content content : contents )
        {
            result.addContentHit( content.getId(), 1f );
        }

        if ( includeFacets )
        {
            Facets facets = new Facets();

            TermsFacet contentTypesFacet = new TermsFacet();
            contentTypesFacet.setName( "contentType" );
            contentTypesFacet.addResult( "system:folder", "Folder", 5 );
            contentTypesFacet.addResult( "system:image", "Image", 24 );
            contentTypesFacet.addResult( "system:space", "Space", 4 );
            facets.addFacet( contentTypesFacet );

            TermsFacet spacesFacet = new TermsFacet();
            spacesFacet.setName( "space" );
            spacesFacet.addResult( "bildearkiv", "Bildearkiv", 30 );
            spacesFacet.addResult( "bluman trampoliner", "Bluman Trampoliner", 1 );
            spacesFacet.addResult( "bluman intranett", "Bluman Intranett", 1 );
            facets.addFacet( spacesFacet );

            QueryFacet query1 = new QueryFacet( 0l );
            query1.setName( "< 1 hour" );
            facets.addFacet( query1 );
            QueryFacet query2 = new QueryFacet( 0l );
            query2.setName( "< 1 week" );
            facets.addFacet( query2 );
            QueryFacet query3 = new QueryFacet( 0l );
            query3.setName( "< 1 day" );
            facets.addFacet( query3 );

            result.setFacets( facets );
        }

        return result;
    }

    private ContentType createContentType( String module, String name )
    {
        return ContentType.newContentType().
            displayName( "My type" ).
            module( ModuleName.from( module ) ).
            name( name ).
            build();
    }

    private Space createSpace( String name )
    {
        return Space.newSpace().
            name( name ).
            displayName( "My space" ).
            build();
    }
}
