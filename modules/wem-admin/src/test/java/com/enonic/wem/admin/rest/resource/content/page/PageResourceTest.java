package com.enonic.wem.admin.rest.resource.content.page;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

public class PageResourceTest
    extends AbstractResourceTest
{
    private PageService pageService;

    private ContentTypeService contentTypeService;

    private SiteTemplateService siteTemplateService;

    @Override
    protected Object getResourceInstance()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
        siteTemplateService = Mockito.mock( SiteTemplateService.class );
        this.pageService = Mockito.mock( PageService.class );

        final PageResource resource = new PageResource();
        resource.pageService = pageService;
        resource.contentTypeService = contentTypeService;
        resource.siteTemplateService = siteTemplateService;

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentType( "my_type" ) );

        return resource;
    }

    @Test
    public void update_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            content );

        String jsonString = request().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_success.json", jsonString );
    }

    @Test(expected = ContentNotFoundException.class)
    public void update_page_failure()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ), Mockito.isA( Context.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Workspace.from( "workspace" ) ) );

        String jsonString = request().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_failure.json", jsonString );
    }

    @Test
    public void create_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.create( Mockito.isA( CreatePageParams.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            content );

        String jsonString = request().path( "content/page/create" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_success.json", jsonString );
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "mymodule|my-page" ) ).
            config( rootDataSet ).
            regions( newPageRegions().build() ).
            build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().
            displayName( "My type" ).
            name( name ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            build();
    }
}
