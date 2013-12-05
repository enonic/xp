package com.enonic.wem.admin.rest.resource.content.site;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.command.content.site.DeleteSite;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class SiteResourceTest
    extends AbstractResourceTest
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

    @Test
    public void create_site_failure()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( CreateSite.class ) ) ).thenThrow( new ContentNotFoundException( content.getId() ) );

        String jsonString = resource().path( "content/site/create" ).
            entity( readFromFile( "create_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_site_failure.json", jsonString );
    }

    @Test
    public void create_site_success()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( CreateSite.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/site/create" ).
            entity( readFromFile( "create_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_site_success.json", jsonString );
    }

    @Test
    public void update_site_failure()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( UpdateSite.class ) ) ).thenThrow( new ContentNotFoundException( content.getId() ) );

        String jsonString = resource().path( "content/site/update" ).
            entity( readFromFile( "update_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_site_failure.json", jsonString );
    }

    @Test
    public void update_site_success()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( UpdateSite.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/site/update" ).
            entity( readFromFile( "update_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_site_success.json", jsonString );
    }

    @Test
    public void delete_site_failure()
        throws Exception
    {
        Content content = createContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( DeleteSite.class ) ) ).thenThrow( new ContentNotFoundException( content.getId() ) );

        String jsonString = resource().path( "content/site/delete" ).
            entity( readFromFile( "delete_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_site_failure.json", jsonString );
    }

    @Test
    public void delete_site_success()
        throws Exception
    {
        Content content = createContent( "content-id", "Content Name", "content-type" );

        Mockito.when( client.execute( Mockito.isA( DeleteSite.class ) ) ).thenReturn( content );

        String jsonString = resource().path( "content/site/delete" ).
            entity( readFromFile( "delete_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "delete_site_success.json", jsonString );
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final SiteResource resource = new SiteResource();
        resource.setClient( client );

        return resource;
    }

    private Content createSiteContent( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", new Value.String( "value1" ) );
        rootDataSet.add( dataSet );

        ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "module-1.0.0" ) ).
            config( rootDataSet ).
            build();

        Site site = Site.newSite().
            template( SiteTemplateKey.from( "template-1.0.0" ) ).
            addModuleConfig( moduleConfig ).
            build();

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            site( site ).
            build();
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", new Value.String( "value1" ) );
        rootDataSet.add( dataSet );

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            site( null ).
            build();
    }
}
