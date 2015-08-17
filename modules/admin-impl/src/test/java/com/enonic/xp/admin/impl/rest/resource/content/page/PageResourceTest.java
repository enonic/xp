package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.time.Instant;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AbstractResourceTest;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.CounterPropertyIdProvider;
import com.enonic.xp.data.PropertyIdProviderAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.page.CreatePageParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageService;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.UpdatePageParams;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;

public class PageResourceTest
    extends AbstractResourceTest
{
    private PageService pageService;

    @Before
    public void before()
    {
        PropertyIdProviderAccessor.instance().set( new CounterPropertyIdProvider() );
    }

    @Override
    protected Object getResourceInstance()
    {
        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        this.pageService = Mockito.mock( PageService.class );

        final PageResource resource = new PageResource();
        resource.setPageService( pageService );
        resource.setContentTypeService( contentTypeService );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentType( "myapplication:my_type" ) );

        final SecurityService securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void update_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "myapplication:content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_success.json", jsonString );
    }

    @Test(expected = ContentNotFoundException.class)
    public void update_page_failure()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "myapplication:content-type" );

        Mockito.when( this.pageService.update( Mockito.isA( UpdatePageParams.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Branch.from( "branch" ) ) );

        String jsonString = request().path( "content/page/update" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_failure.json", jsonString );
    }

    @Test
    public void create_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "myapplication:content-type" );

        Mockito.when( this.pageService.create( Mockito.isA( CreatePageParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/page/create" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_success.json", jsonString );
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree( new CounterPropertyIdProvider() );

        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            regions( PageRegions.create().build() ).
            build();

        return Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( "/" + name ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            valid( true ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }

    private ContentType createContentType( String name )
    {
        return ContentType.create().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( name ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            build();
    }
}
