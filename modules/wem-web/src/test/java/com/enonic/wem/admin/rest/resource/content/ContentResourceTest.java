package com.enonic.wem.admin.rest.resource.content;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest2;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.FindContent;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.query.ContentIndexQueryResult;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.facet.Facets;
import com.enonic.wem.api.facet.QueryFacet;
import com.enonic.wem.api.facet.TermsFacet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;
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

        assertJson( "get_content_by_path.json", jsonString );

    }

    @Test
    public void get_by_id()
        throws Exception
    {

        String jsonString =
            resource().path( "content" ).queryParam( "contentIds", "aaa" ).queryParam( "contentIds", "bbb" ).get( String.class );

        assertJson( "get_content_by_id.json", jsonString );
    }

    @Test
    public void get_content_list()
        throws Exception
    {
        String jsonString = resource().path( "content/list" ).queryParam( "path", "mymodule:/" ).get( String.class );

        assertJson( "list_content.json", jsonString );

    }

    @Test
    public void find_content_with_facets()
        throws Exception
    {
        String jsonString = resource().path( "content/find" ).entity( readFromFile( "find_content_with_facets_params.json" ),
                                                                      MediaType.APPLICATION_JSON_TYPE ).post( String.class );

        assertJson( "find_content_with_facets.json", jsonString );

    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final ContentResource resource = new ContentResource();
        resource.setClient( client );

        final Content aaaContent = createContent( "aaa", "my_a_content", "mymodule:my_type" );
        final Content bbbContent = createContent( "bbb", "my_b_content", "mymodule:my_type" );

        final ContentType aaaContentType = createContentType( "my_type" );
        final Space aaaSpace = createSpace( "my_space" );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( Contents.from( aaaContent, bbbContent ) );

        Mockito.when( client.execute( Mockito.isA( FindContent.class ) ) ).thenReturn(
            createContentIndexQueryResult( Contents.from( aaaContent, bbbContent ), true ) );

        Mockito.when( client.execute( Mockito.isA( GetContentTypes.class ) ) ).thenReturn( ContentTypes.from( aaaContentType ) );

        Mockito.when( client.execute( Mockito.isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( aaaSpace ) );

        Mockito.when( client.execute( Mockito.isA( GetChildContent.class ) ) ).thenReturn( Contents.from( aaaContent, bbbContent ) );

        return resource;
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
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
            type( new QualifiedContentTypeName( contentTypeName ) ).
            contentData( contentData ).
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
            contentTypesFacet.setDisplayName( "Content Type" );
            contentTypesFacet.setName( "contentType" );
            contentTypesFacet.addResult( "system:folder", "Folder", 5 );
            contentTypesFacet.addResult( "system:image", "Image", 24 );
            contentTypesFacet.addResult( "system:space", "Space", 4 );
            facets.addFacet( contentTypesFacet );

            TermsFacet spacesFacet = new TermsFacet();
            spacesFacet.setDisplayName( "Space" );
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

    private ContentType createContentType( String name )
    {
        return ContentType.newContentType().
            displayName( "My type" ).
            module( ModuleName.from( "mymodule" ) ).
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
