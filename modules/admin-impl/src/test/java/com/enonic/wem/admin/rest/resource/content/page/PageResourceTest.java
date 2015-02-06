package com.enonic.wem.admin.rest.resource.content.page;

import java.time.Instant;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.data.PropertyIdProviderAccessor;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.branch.Branch;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;

public class PageResourceTest
    extends AbstractResourceTest
{
    private PageService pageService;

    @Before
    public void before()
    {
        PropertyIdProviderAccessor.instance().set( new PropertyTree.PredictivePropertyIdProvider() );
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
            thenReturn( createContentType( "mymodule:my_type" ) );

        final SecurityService securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void update_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );

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
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );

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
        Content content = createPage( "content-id", "content-name", "mymodule:content-type" );

        Mockito.when( this.pageService.create( Mockito.isA( CreatePageParams.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/page/create" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_page_success.json", jsonString );
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        rootDataSet.addString( "property1", "value1" );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            regions( newPageRegions().build() ).
            build();

        return Content.newContent().
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
        return ContentType.newContentType().
            superType( ContentTypeName.structured() ).
            displayName( "My type" ).
            name( name ).
            icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) ).
            build();
    }
}
