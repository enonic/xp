package com.enonic.wem.admin.rest.resource.content.page;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.command.content.page.UpdatePage;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class PageResourceTest
    extends AbstractResourceTest
{
    private Client client;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final PageResource resource = new PageResource();
        resource.setClient( client );

        return resource;
    }

    @Test
    public void update_page_success()
        throws Exception
    {
        Content content = createPage( "content-id", "content-name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( UpdatePage.class ) ) ).thenReturn( content );

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

        Mockito.when( client.execute( Mockito.isA( UpdatePage.class ) ) ).thenThrow( new ContentNotFoundException( content.getId() ) );

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

        Mockito.when( client.execute( Mockito.isA( CreatePage.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/page/create" ).
            entity( readFromFile( "update_page_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_page_success.json", jsonString );
    }

    private Content createPage( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", new Value.String( "value1" ) );
        rootDataSet.add( dataSet );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "template-1.0.0|mymodule-1.0.0|my-page" ) ).
            config( rootDataSet ).
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
