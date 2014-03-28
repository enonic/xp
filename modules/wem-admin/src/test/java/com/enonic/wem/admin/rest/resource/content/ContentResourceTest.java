package com.enonic.wem.admin.rest.resource.content;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.GenerateContentName;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.content.GetContentTypesParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.query.ContentQueryResult;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.MaximumOccurrencesValidationError;
import com.enonic.wem.api.schema.content.validator.MissingRequiredValueValidationError;

import static com.enonic.wem.api.content.Content.newContent;
import static org.junit.Assert.*;

public class ContentResourceTest
    extends AbstractResourceTest
{
    private Client client;

    private ContentTypeService contentTypeService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();

        contentTypeService = Mockito.mock( ContentTypeService.class );
    }

    @After
    public void after()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void get_content_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", new Value.String( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", new Value.String( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", new Value.Double( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", new Value.Double( 1.333 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get( String.class );

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_content_summary_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", new Value.DateTime( DateTime.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", new Value.Long( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", new Value.Long( 2 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentByPath.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).
            queryParam( "expand", "summary" ).get( String.class );

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_by_path_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentByIds.class ) ) ).thenReturn( Contents.empty() );

        try
        {
            resource().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Content [/my_a_content] was not found" );
        }
    }

    @Test
    public void get_content_id_by_path_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", new Value.DateTime( DateTime.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", new Value.Long( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", new Value.Long( 2 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentVersion.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).
            queryParam( "version", "1" ).queryParam( "expand", "none" ).get( String.class );

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_path_and_version_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentVersion.class ) ) ).thenReturn( null );

        try
        {
            resource().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).
                queryParam( "version", "1" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Content [/my_a_content] was not found" );
        }
    }

    @Test
    public void get_content_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", new Value.String( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", new Value.String( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", new Value.Double( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", new Value.Double( 1.333 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentById.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content" ).queryParam( "id", "aaa" ).get( String.class );

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_site_content_by_id()
        throws Exception
    {
        RootDataSet moduleConfigConfig = new RootDataSet();
        moduleConfigConfig.setProperty( "A", new Value.Long( 1 ) );
        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule-1.0.0" ) ).
            config( moduleConfigConfig ).
            build();
        Site site = Site.newSite().
            template( SiteTemplateKey.from( "mysitetemplate-1.0.0" ) ).
            moduleConfigs( ModuleConfigs.from( moduleConfig ) ).build();

        Content content = createContent( "aaa", "my_a_content", "my_type" );
        content = newContent( content ).site( site ).build();

        ContentData contentData = content.getContentData();
        contentData.setProperty( "myProperty", new Value.String( "myValue" ) );

        Mockito.when( client.execute( Mockito.isA( GetContentById.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content" ).queryParam( "id", "aaa" ).get( String.class );

        assertJson( "get_content_with_site.json", jsonString );
    }

    @Test
    public void get_page_content_by_id()
        throws Exception
    {
        RootDataSet componentConfig = new RootDataSet();
        componentConfig.setProperty( "my-prop", new Value.String( "value" ) );

        PartComponent component = PartComponent.newPartComponent().
            name( "my-component" ).
            descriptor( PartDescriptorKey.from( "mainmodule-1.0.0:partTemplateName" ) ).
            config( componentConfig ).
            build();

        Region region = Region.newRegion().
            name( "my-region" ).
            add( component ).
            build();

        PageRegions regions = PageRegions.newPageRegions().
            add( region ).
            build();

        RootDataSet pageConfig = new RootDataSet();
        pageConfig.setProperty( "background-color", new Value.String( "blue" ) );
        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|mypagetemplate" ) ).
            regions( regions ).
            config( pageConfig ).
            build();

        Content content = createContent( "aaa", "my_a_content", "my_type" );
        content = newContent( content ).page( page ).build();

        ContentData contentData = content.getContentData();
        contentData.setProperty( "myProperty", new Value.String( "myValue" ) );

        Mockito.when( client.execute( Mockito.isA( GetContentById.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content" ).queryParam( "id", "aaa" ).get( String.class );

        assertJson( "get_content_with_page.json", jsonString );
    }

    @Test
    public void get_content_summary_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", new Value.DateTime( DateTime.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", new Value.Long( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", new Value.Long( 2 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentById.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content" ).queryParam( "id", "aaa" ).
            queryParam( "expand", "summary" ).get( String.class );

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_by_id_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentByIds.class ) ) ).thenReturn( Contents.empty() );

        try
        {
            resource().path( "content" ).queryParam( "id", "aaa" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Content [aaa] was not found" );
        }
    }

    @Test
    public void get_content_id_by_id_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", new Value.String( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", new Value.String( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", new Value.Double( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", new Value.Double( 1.333 ) );

        Mockito.when( client.execute( Mockito.isA( GetContentVersion.class ) ) ).thenReturn( aContent );

        String jsonString = resource().path( "content" ).queryParam( "id", "aaa" ).
            queryParam( "version", "2" ).queryParam( "expand", "none" ).get( String.class );

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_id_and_version_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentVersion.class ) ) ).thenReturn( null );

        try
        {
            resource().path( "content" ).queryParam( "id", "aaa" ).queryParam( "version", "2" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Content [aaa] was not found" );
        }
    }

    @Test
    public void list_content_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get( String.class );

        assertJson( "list_content_summary_byPath.json", jsonString );
    }

    @Test
    public void list_content_full_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).
            queryParam( "expand", "full" ).get( String.class );

        assertJson( "list_content_full_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_path_not_found()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.empty() );

        String jsonString = resource().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get( String.class );

        assertJson( "list_content_empty_byPath.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetRootContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list/bypath" ).queryParam( "expand", "none" ).get( String.class );

        assertJson( "list_content_id_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetContentByIds.class ) ) ).thenReturn( Contents.from( cContent ) );

        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list" ).queryParam( "parentId", "ccc" ).get( String.class );

        assertJson( "list_content_summary.json", jsonString );
    }

    @Test
    public void list_content_full_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetContentByIds.class ) ) ).thenReturn( Contents.from( cContent ) );

        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list" ).queryParam( "parentId", "ccc" ).
            queryParam( "expand", "full" ).get( String.class );

        assertJson( "list_content_full.json", jsonString );
    }

    @Test
    public void list_content_by_id_not_found()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetContentByIds.class ) ) ).thenReturn( Contents.empty() );

        String jsonString = resource().path( "content/list" ).queryParam( "parentId", "ccc" ).get( String.class );

        assertJson( "list_content_empty.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "my_type" );
        Mockito.when( client.execute( Mockito.isA( GetRootContent.class ) ) ).thenReturn( Contents.from( aContent, bContent ) );

        String jsonString = resource().path( "content/list" ).queryParam( "expand", "none" ).get( String.class );

        assertJson( "list_content_id.json", jsonString );
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
    @Ignore
    public void validate_content_success()
        throws Exception
    {

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my_type" ) ) );

        Mockito.when( client.execute( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        String jsonString = resource().path( "content/validate" ).
            entity( readFromFile( "validate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "validate_content_success.json", jsonString );
    }

    @Test
    @Ignore
    public void validate_content_error()
        throws Exception
    {

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my_type" ) ) );

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
        Mockito.when( client.execute( Mockito.isA( DeleteContent.class ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "one" ).build() ) );

        Mockito.when( client.execute( Mockito.isA( DeleteContent.class ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "two" ).build() ) );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_success.json", jsonString );
    }

    @Test
    public void delete_content_failure()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.eq( new DeleteContent().contentPath( ContentPath.from( "/one" ) ) ) ) ).thenThrow(
            new ContentNotFoundException( ContentPath.from( "one" ) ) );

        Mockito.when( client.execute( Mockito.eq( new DeleteContent().contentPath( ContentPath.from( "/two" ) ) ) ) ).thenThrow(
            new UnableToDeleteContentException( ContentPath.from( "two" ), "Some reason" ) );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_failure.json", jsonString );
    }

    @Test
    public void delete_content_both()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.eq( new DeleteContent().contentPath( ContentPath.from( "one" ) ) ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "one" ).build() ) );

        Mockito.when( client.execute( Mockito.eq( new DeleteContent().contentPath( ContentPath.from( "two" ) ) ) ) ).thenThrow(
            new UnableToDeleteContentException( ContentPath.from( "two" ), "Some reason" ) );

        String jsonString = resource().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_content_both.json", jsonString );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_exception()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my-type" ) ) );

        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( client.execute( Mockito.isA( CreateContent.class ) ) ).thenThrow( e );

        resource().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

    }

    @Test
    public void create_content_success()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my-type" ) ) );

        Content content = createContent( "content-id", "content-path", "content-type" );
        Mockito.when( client.execute( Mockito.isA( CreateContent.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_content_success.json", jsonString );
    }


    @Test(expected = ContentNotFoundException.class)
    public void update_content_failure()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my-type" ) ) );

        Exception e = new com.enonic.wem.api.content.ContentNotFoundException( ContentId.from( "content-id" ) );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenThrow( e );

        resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );
    }

    @Test
    public void update_content_nothing_updated()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "content-type" );
        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenReturn( content );
        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.times( 0 ) ).execute( Mockito.isA( RenameContent.class ) );

        assertJson( "update_content_nothing_updated.json", jsonString );
    }

    @Test
    public void update_content_success()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "content-type" );
        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenReturn( content );
        String jsonString = resource().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        Mockito.verify( client, Mockito.times( 0 ) ).execute( Mockito.isA( RenameContent.class ) );

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
        Property property = new Property.String( "myProperty", "myValue" );

        errors.add( new MaximumOccurrencesValidationError( input, 5 ) );
        errors.add( new MissingRequiredValueValidationError( input, property ) );

        return DataValidationErrors.from( errors );
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }

    private ContentQueryResult createContentIndexQueryResult( Contents contents, boolean includeFacets )
    {
        ContentQueryResult.Builder builder = ContentQueryResult.newResult( contents.getSize() );
        for ( Content content : contents )
        {
            builder.addContentHit( content.getId(), 1f );
        }
        return builder.build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().
            displayName( "My type" ).
            name( name ).
            build();
    }
}
