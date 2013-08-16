package com.enonic.wem.admin.rest.resource.content;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.content.model.ContentJson;
import com.enonic.wem.admin.rest.resource.content.model.ContentListJson;
import com.enonic.wem.admin.rest.resource.content.model.PropertyJson;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class ContentResourceTest
{
    private Client client;

    @Before
    public void setup()
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC ).getMillis() );

        client = Mockito.mock( Client.class );

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
        final Content myContent = createContent( "abc", "my_content" );

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn( Contents.from( myContent ) );

        final ContentResource resource = new ContentResource();
        resource.setClient( client );

        ContentListJson result = resource.get( "/my_content", null, null );

        List<ContentJson> contents = result.getContent();
        assertEquals( 1, contents.size() );

        ContentJson content = contents.get( 0 );
        assertEquals( "abc", content.getId() );
        assertEquals( "/my_content", content.getPath() );
        assertEquals( "my_content", content.getName() );
        assertEquals( "mymodule:my_type", content.getType() );
        assertEquals( "My Content", content.getDisplayName() );
        assertEquals( "user:myStore:me", content.getOwner() );
        assertEquals( "user:system:admin", content.getModifier() );
        assertEquals( false, content.isRoot() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getModifiedTime() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getCreatedTime() );
        assertEquals( "http://localhost/admin/rest/content/image/abc", content.getIconUrl() );

        PropertyJson property = ( PropertyJson ) content.getData().getData().get( 0 ).getData();

        assertEquals( "myData", property.getName() );
        assertEquals( "myData", property.getPath() );
        assertEquals( "Text", property.getType() );
        assertEquals( "value1", property.getValue() );
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Content aaaContent = createContent( "aaa", "my_a_content" );
        final Content bbbContent = createContent( "bbb", "my_b_content" );

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn(
            Contents.from( aaaContent, bbbContent ) );

        final ContentResource resource = new ContentResource();
        resource.setClient( client );

        ContentListJson result = resource.get( null, null, Arrays.asList( "aaa", "bbb" ) );

        List<ContentJson> contents = result.getContent();
        assertEquals( 2, contents.size() );

        ContentJson content = contents.get( 0 );
        assertEquals( "aaa", content.getId() );
        assertEquals( "/my_a_content", content.getPath() );
        assertEquals( "my_a_content", content.getName() );
        assertEquals( "mymodule:my_type", content.getType() );
        assertEquals( "My Content", content.getDisplayName() );
        assertEquals( "user:myStore:me", content.getOwner() );
        assertEquals( "user:system:admin", content.getModifier() );
        assertEquals( false, content.isRoot() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getModifiedTime() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getCreatedTime() );
        assertEquals( "http://localhost/admin/rest/content/image/aaa", content.getIconUrl() );

        PropertyJson property = ( PropertyJson ) content.getData().getData().get( 0 ).getData();

        assertEquals( "myData", property.getName() );
        assertEquals( "myData", property.getPath() );
        assertEquals( "Text", property.getType() );
        assertEquals( "value1", property.getValue() );

        content = contents.get( 1 );
        assertEquals( "bbb", content.getId() );
        assertEquals( "/my_b_content", content.getPath() );
        assertEquals( "my_b_content", content.getName() );
        assertEquals( "mymodule:my_type", content.getType() );
        assertEquals( "My Content", content.getDisplayName() );
        assertEquals( "user:myStore:me", content.getOwner() );
        assertEquals( "user:system:admin", content.getModifier() );
        assertEquals( false, content.isRoot() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getModifiedTime() );
        assertEquals( "2000-01-01T12:00:00.000Z", content.getCreatedTime() );
        assertEquals( "http://localhost/admin/rest/content/image/bbb", content.getIconUrl() );

        property = ( PropertyJson ) content.getData().getData().get( 0 ).getData();

        assertEquals( "myData", property.getName() );
        assertEquals( "myData", property.getPath() );
        assertEquals( "Text", property.getType() );
        assertEquals( "value1", property.getValue() );
    }

    private Content createContent( final String id, final String name )
    {
        final ContentData contentData = new ContentData();
        contentData.setProperty( DataPath.from( "myData" ), new Value.Text( "value1" ) );

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( "mymodule:my_type" ) ).
            contentData( contentData ).
            build();
    }
}
