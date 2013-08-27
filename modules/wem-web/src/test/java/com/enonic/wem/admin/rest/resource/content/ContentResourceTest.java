package com.enonic.wem.admin.rest.resource.content;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.web.servlet.ServletRequestHolder;

public class ContentResourceTest
    extends AbstractResourceTest2
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

        String jsonString = resource().path( "content" ).queryParam( "path", "/my_content" ).get( String.class );

        assertJson( "get_by_path.json", jsonString );

    }

    @Test
    public void get_by_id()
        throws Exception
    {

        String jsonString =
            resource().path( "content" ).queryParam( "contentIds", "aaa" ).queryParam( "contentIds", "bbb" ).get( String.class );

        assertJson( "get_by_id.json", jsonString );
    }

    @Test
    public void get_content_list()
        throws Exception
    {
        String jsonString = resource().path( "content/list" ).queryParam( "path", "mymodule:/" ).get( String.class );

        assertJson( "get_content_list.json", jsonString );

    }

    private Content createContent( final String id, final String name )
    {
        final ContentData contentData = new ContentData();
        contentData.setProperty( DataPath.from( "myData" ), new Value.Text( "value1" ) );

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( "mymodule:my_type" ) ).
            contentData( contentData ).
            build();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final ContentResource resource = new ContentResource();
        resource.setClient( client );

        final Content aaaContent = createContent( "aaa", "my_a_content" );
        final Content bbbContent = createContent( "bbb", "my_b_content" );

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn(
            Contents.from( aaaContent, bbbContent ) );

        Mockito.when( client.execute( Mockito.any( GetChildContent.class ) ) ).thenReturn( Contents.from( aaaContent, bbbContent ) );

        return resource;
    }
}
