package com.enonic.wem.admin.rest.resource.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.elasticsearch.common.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.MockRestResponse;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.DeleteContentParams;
import com.enonic.wem.api.content.DeleteContentResult;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.content.GetContentByIdsParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.PushContentParams;
import com.enonic.wem.api.content.RenameContentParams;
import com.enonic.wem.api.content.UnableToDeleteContentException;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.ValidateContentData;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.validator.DataValidationError;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.api.schema.content.validator.MaximumOccurrencesValidationError;
import com.enonic.wem.api.schema.content.validator.MissingRequiredValueValidationError;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.site.Site.newSite;
import static org.junit.Assert.*;

public class ContentResourceTest
    extends AbstractResourceTest
{
    private final String currentTime = "2013-08-23T12:55:09.162Z";

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    @After
    public void after()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Override
    protected Object getResourceInstance()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );

        final ContentResource resource = new ContentResource();

        contentService = Mockito.mock( ContentService.class );
        resource.setContentService( contentService );
        resource.setContentTypeService( contentTypeService );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentType( "mymodule:my_type" ) );

        return resource;
    }

    @Test
    public void get_content_by_path()
        throws Exception
    {
        final Content content = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData data = content.getContentData();

        data.setProperty( "myArray[0]", Value.newLong( 1 ) );
        data.setProperty( "myArray[1]", Value.newLong( 2 ) );

        data.setProperty( "mySetWithArray.myArray[0]", Value.newDouble( 3.14159 ) );
        data.setProperty( "mySetWithArray.myArray[1]", Value.newDouble( 1.333 ) );

        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( content );

        String jsonString = request().
            path( "content/bypath" ).
            queryParam( "path", "/my_a_content" ).
            get().getAsString();

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_content_summary_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", Value.newInstant( Instant.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", Value.newLong( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", Value.newLong( 2 ) );

        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).
            thenReturn( aContent );

        String jsonString = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).
            queryParam( "expand", "summary" ).get().getAsString();

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_by_path_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).
            thenReturn( Contents.empty() );

        final MockRestResponse response = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get();

        assertEquals( response.getStatus(), 404 );
        assertEquals( response.getAsString(), "Content [/my_a_content] was not found" );
    }

    @Test
    public void get_content_id_by_path_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", Value.newInstant( Instant.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", Value.newLong( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", Value.newLong( 2 ) );

        Mockito.when( contentService.getByPath( Mockito.eq( ContentPath.from( "/my_a_content" ) ) ) ).thenReturn( aContent );

        String jsonString = request().
            path( "content/bypath" ).
            queryParam( "path", "/my_a_content" ).
            queryParam( "expand", "none" ).
            get().getAsString();

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_path_and_version_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByPath( Mockito.eq( ContentPath.from( "/my_a_content" ) ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "content/bypath" ).queryParam( "path", "/my_a_content" ).get();
        assertEquals( response.getStatus(), 404 );
        assertEquals( response.getAsString(), "Content [/my_a_content] was not found" );
    }

    @Test
    public void get_content_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", Value.newLong( 1 ) );
        aContentData.setProperty( "myArray[1]", Value.newLong( 2 ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", Value.newDouble( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", Value.newDouble( 1.333 ) );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_full.json", jsonString );
    }

    @Test
    public void get_site_content_by_id()
        throws Exception
    {
        RootDataSet moduleConfigConfig = new RootDataSet();
        moduleConfigConfig.setProperty( "A", Value.newLong( 1 ) );
        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( moduleConfigConfig ).
            build();

        Site content = createSite( "aaa", "my_a_content", "mymodule:my_type", ModuleConfigs.from( moduleConfig ) );

        ContentData contentData = content.getContentData();
        contentData.setProperty( "myProperty", Value.newString( "myValue" ) );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( content );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_with_site.json", jsonString );
    }

    @Test
    public void get_page_content_by_id()
        throws Exception
    {
        RootDataSet componentConfig = new RootDataSet();
        componentConfig.setProperty( "my-prop", Value.newString( "value" ) );

        PartComponent component = PartComponent.newPartComponent().
            name( "my-component" ).
            descriptor( PartDescriptorKey.from( "mainmodule:partTemplateName" ) ).
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
        pageConfig.setProperty( "background-color", Value.newString( "blue" ) );
        Page page = Page.newPage().
            template( PageTemplateKey.from( "mypagetemplate" ) ).
            regions( regions ).
            config( pageConfig ).
            build();

        Content content = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        content = newContent( content ).page( page ).build();

        ContentData contentData = content.getContentData();
        contentData.setProperty( "myProperty", Value.newString( "myValue" ) );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( content );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).get().getAsString();

        assertJson( "get_content_with_page.json", jsonString );
    }

    @Test
    public void get_content_summary_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();
        aContentData.setProperty( "myProperty", Value.newInstant( Instant.parse( this.currentTime ) ) );

        aContentData.setProperty( "mySet.setProperty1", Value.newLong( 1 ) );
        aContentData.setProperty( "mySet.setProperty2", Value.newLong( 2 ) );

        Mockito.when( contentService.getById( ContentId.from( "aaa" ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).
            queryParam( "expand", "summary" ).get().getAsString();

        assertJson( "get_content_summary.json", jsonString );
    }

    @Test
    public void get_content_by_id_not_found()
        throws Exception
    {
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.empty() );

        final MockRestResponse response = request().path( "content" ).queryParam( "id", "aaa" ).get();
        assertEquals( response.getStatus(), 404 );
        assertEquals( response.getAsString(), "Content [aaa] was not found" );
    }

    @Test
    public void get_content_id_by_id_and_version()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", Value.newString( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", Value.newString( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", Value.newDouble( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", Value.newDouble( 1.333 ) );

        Mockito.when( contentService.getById( Mockito.eq( ContentId.from( "aaa" ) ) ) ).thenReturn( aContent );

        String jsonString = request().path( "content" ).queryParam( "id", "aaa" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "get_content_id.json", jsonString );
    }

    @Test
    public void get_content_by_id_and_version_not_found()
        throws Exception
    {
        Mockito.when( contentService.getById( Mockito.eq( ContentId.from( "aaa" ) ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "content" ).queryParam( "id", "aaa" ).get();
        assertEquals( response.getStatus(), 404 );
        assertEquals( response.getAsString(), "Content [aaa] was not found" );
    }

    @Test
    public void list_content_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get().getAsString();

        assertJson( "list_content_summary_byPath.json", jsonString );
    }

    @Test
    public void list_content_full_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).
            queryParam( "expand", "full" ).get().getAsString();

        assertJson( "list_content_full_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_path_not_found()
        throws Exception
    {
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.empty() ).
                hits( 0 ).
                totalHits( 0 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "parentPath", "/" ).get().getAsString();

        assertJson( "list_content_empty_byPath.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_path()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list/bypath" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "list_content_id_byPath.json", jsonString );
    }

    @Test
    public void list_content_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "mymodule:my_type" );
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( cContent );

        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "parentId", "ccc" ).get().getAsString();

        assertJson( "list_content_summary.json", jsonString );
    }

    @Test
    public void list_content_full_by_id()
        throws Exception
    {
        final Content cContent = createContent( "ccc", "my_c_content", "mymodule:my_type" );
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( cContent );

        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "parentId", "ccc" ).
            queryParam( "expand", "full" ).get().getAsString();

        assertJson( "list_content_full.json", jsonString );
    }

    @Test
    public void list_root_content_id_by_id()
        throws Exception
    {
        final Content aContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );
        Mockito.when( contentService.findByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn(
            FindContentByParentResult.create().
                contents( Contents.from( aContent, bContent ) ).
                hits( 2 ).
                totalHits( 2 ).
                build() );

        String jsonString = request().path( "content/list" ).queryParam( "expand", "none" ).get().getAsString();

        assertJson( "list_content_id.json", jsonString );
    }

    @Test
    public void generate_name()
        throws Exception
    {
        Mockito.when( contentService.generateContentName( "Some rea11y we!rd name..." ) ).thenReturn( "some-rea11y-werd-name" );

        String jsonString =
            request().path( "content/generateName" ).queryParam( "displayName", "Some rea11y we!rd name..." ).get().getAsString();

        assertJson( "generate_content_name.json", jsonString );
    }

    @Test
    @Ignore // Not implemented yet
    public void validate_content_success()
        throws Exception
    {

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my_type" ) ) );

        Mockito.when( contentService.validate( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( DataValidationErrors.empty() );

        String jsonString = request().path( "content/validate" ).
            entity( readFromFile( "validate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "validate_content_success.json", jsonString );
    }

    @Test
    @Ignore // Not implemented yet
    public void validate_content_error()
        throws Exception
    {

        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my_type" ) ) );

        Mockito.when( contentService.validate( Mockito.isA( ValidateContentData.class ) ) ).thenReturn( createDataValidationErrors() );

        String jsonString = request().path( "content/validate" ).
            entity( readFromFile( "validate_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "validate_content_error.json", jsonString );
    }

    @Test
    public void delete_content_success()
        throws Exception
    {
        Mockito.when( contentService.delete( Mockito.isA( DeleteContentParams.class ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "one" ).build() ) );

        Mockito.when( contentService.delete( Mockito.isA( DeleteContentParams.class ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "two" ).build() ) );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_success.json", jsonString );
    }

    @Test
    public void delete_content_failure()
        throws Exception
    {
        Mockito.when(
            contentService.delete( Mockito.eq( new DeleteContentParams().contentPath( ContentPath.from( "/one" ) ) ) ) ).thenThrow(
            new ContentNotFoundException( ContentPath.from( "one" ), ContentConstants.WORKSPACE_STAGE ) );

        Mockito.when(
            contentService.delete( Mockito.eq( new DeleteContentParams().contentPath( ContentPath.from( "/two" ) ) ) ) ).thenThrow(
            new UnableToDeleteContentException( ContentPath.from( "two" ), "Some reason" ) );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_failure.json", jsonString );
    }

    @Test
    public void delete_content_both()
        throws Exception
    {
        Mockito.when(
            contentService.delete( Mockito.eq( new DeleteContentParams().contentPath( ContentPath.from( "one" ) ) ) ) ).thenReturn(
            new DeleteContentResult( newContent().parentPath( ContentPath.ROOT ).name( "one" ).build() ) );

        Mockito.when( contentService.delete( Mockito.eq( new DeleteContentParams().contentPath( ContentPath.from( "two" ) ) ) ) ).thenThrow(
            new UnableToDeleteContentException( ContentPath.from( "two" ), "Some reason" ) );

        String jsonString = request().path( "content/delete" ).
            entity( readFromFile( "delete_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_content_both.json", jsonString );
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_content_exception()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my-type" ) ) );

        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( contentService.create( Mockito.isA( CreateContentParams.class ) ) ).thenThrow( e );

        request().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

    }

    @Test
    public void create_content_success()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my-type" ) ) );

        Content content = createContent( "content-id", "content-path", "mymodule:content-type" );
        Mockito.when( contentService.create( Mockito.isA( CreateContentParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/create" ).
            entity( readFromFile( "create_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_content_success.json", jsonString );
    }


    @Test(expected = ContentNotFoundException.class)
    public void update_content_failure()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my-type" ) ) );

        Exception e =
            new com.enonic.wem.api.content.ContentNotFoundException( ContentId.from( "content-id" ), ContentConstants.WORKSPACE_STAGE );

        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenThrow( e );

        request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void update_content_nothing_updated()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "mymodule:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_nothing_updated.json", jsonString );
    }

    @Test
    public void update_content_success()
        throws Exception
    {
        Mockito.when( contentTypeService.getByNames( Mockito.isA( GetContentTypesParams.class ) ) ).thenReturn(
            ContentTypes.from( createContentType( "mymodule:my-type" ) ) );

        Content content = createContent( "content-id", "content-name", "mymodule:content-type" );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( content );
        String jsonString = request().path( "content/update" ).
            entity( readFromFile( "update_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        Mockito.verify( contentService, Mockito.times( 0 ) ).rename( Mockito.isA( RenameContentParams.class ) );

        assertJson( "update_content_success.json", jsonString );
    }

    @Test
    public void publish()
        throws Exception
    {
        final String contentIdString = "1";

        final Content aContent = createContent( contentIdString, "my_a_content", "mymodule:my_type" );

        final ContentData aContentData = aContent.getContentData();

        aContentData.setProperty( "myArray[0]", Value.newString( "arrayValue1" ) );
        aContentData.setProperty( "myArray[1]", Value.newString( "arrayValue2" ) );

        aContentData.setProperty( "mySetWithArray.myArray[0]", Value.newDouble( 3.14159 ) );
        aContentData.setProperty( "mySetWithArray.myArray[1]", Value.newDouble( 1.333 ) );

        final PushContentParams pushContentParams =
            new PushContentParams( ContentConstants.WORKSPACE_PROD, ContentId.from( contentIdString ) );

        Mockito.when( contentService.push( pushContentParams ) ).
            thenReturn( aContent );

        String jsonString = request().path( "content/publish" ).
            entity( readFromFile( "publish_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "publish_content_success.json", jsonString );
    }


    @Test(expected = ContentNotFoundException.class)
    public void publish_not_found()
        throws Exception
    {

        final Exception e =
            new com.enonic.wem.api.content.ContentNotFoundException( ContentId.from( "content-id" ), ContentConstants.WORKSPACE_STAGE );

        Mockito.when( contentService.push( Mockito.isA( PushContentParams.class ) ) ).
            thenThrow( e );

        request().path( "content/publish" ).
            entity( readFromFile( "publish_content_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

    }

    private DataValidationErrors createDataValidationErrors()
    {
        List<DataValidationError> errors = new ArrayList<>( 2 );

        Input input = Input.newInput().name( "myInput" ).inputType( InputTypes.PHONE ).required( true ).maximumOccurrences( 3 ).build();
        Property property = Property.newString( "myProperty", "myValue" );

        errors.add( new MaximumOccurrencesValidationError( input, 5 ) );
        errors.add( new MissingRequiredValueValidationError( input, property ) );

        return DataValidationErrors.from( errors );
    }


    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        final RootDataSet metadata = new RootDataSet();
        metadata.setProperty( "myProperty", Value.newLong( 1 ) );

        return newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( Instant.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            addMetadata( new Metadata( MetadataSchemaName.from( "myModule:myField" ), metadata ) ).
            build();
    }

    private Site createSite( final String id, final String name, final String contentTypeName, ModuleConfigs moduleConfigs )
    {
        return newSite().
            moduleConfigs( moduleConfigs ).
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( Instant.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( name ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            build();
    }
}
