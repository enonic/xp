package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.resource.content.ContentResource;
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
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

public class PageResourceTest
    extends AbstractResourceTest
{
    private PageService pageService;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        this.pageService = Mockito.mock( PageService.class );

        final PageResource resource = new PageResource();
        resource.pageService = pageService;

        return resource;
    }

    @Test
    public void update_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_page_success.json", jsonString );
    }

    @Test(expected = ContentNotFoundException.class)
    public void update_page_failure()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), ContentResource.STAGE_WORKSPACE ) );

        String jsonString = resource().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_page_failure.json", jsonString );
    }

    @Test
    public void create_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( this.pageService.create( Mockito.isA( CreatePageParams.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/page/create" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

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
}
