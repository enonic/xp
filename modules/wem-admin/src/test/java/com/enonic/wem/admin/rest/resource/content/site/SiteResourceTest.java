package com.enonic.wem.admin.rest.resource.content.site;

import java.time.Instant;

import javax.ws.rs.core.MediaType;

import org.elasticsearch.common.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.ModuleConfig;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

public class SiteResourceTest
    extends AbstractResourceTest
{
    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private SiteTemplateService siteTemplateService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    private final static Workspace WORKSPACE = Workspace.from( "workspace" );

    @After
    public void after()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test(expected = ContentNotFoundException.class)
    public void create_site_failure()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.create( Mockito.isA( CreateSiteParams.class ), Mockito.isA( Context.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), WORKSPACE ) );

        request().path( "content/site/create" ).
            entity( readFromFile( "create_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void create_site_success()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.create( Mockito.isA( CreateSiteParams.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            content );

        String jsonString = request().path( "content/site/create" ).
            entity( readFromFile( "create_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_site_success.json", jsonString );
    }

    @Test(expected = ContentNotFoundException.class)
    public void update_site_failure()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.update( Mockito.isA( UpdateSiteParams.class ), Mockito.isA( Context.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), WORKSPACE ) );

        String jsonString = request().path( "content/site/update" ).
            entity( readFromFile( "update_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_site_failure.json", jsonString );
    }

    @Test
    public void update_site_success()
        throws Exception
    {
        Content content = createSiteContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.update( Mockito.isA( UpdateSiteParams.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            content );

        String jsonString = request().path( "content/site/update" ).
            entity( readFromFile( "update_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_site_success.json", jsonString );
    }

    @Test(expected = ContentNotFoundException.class)
    public void delete_site_failure()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.delete( Mockito.isA( ContentId.class ), Mockito.isA( Context.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), WORKSPACE ) );

        request().path( "content/site/delete" ).
            entity( readFromFile( "delete_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
    }

    @Test
    public void delete_site_success()
        throws Exception
    {
        Content content = createContent( "content-id", "content-name", "content-type" );

        Mockito.when( this.siteService.delete( Mockito.isA( ContentId.class ), Mockito.isA( Context.class ) ) ).thenReturn( content );

        String jsonString = request().path( "content/site/delete" ).
            entity( readFromFile( "delete_site_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete_site_success.json", jsonString );
    }

    @Override
    protected Object getResourceInstance()
    {
        siteService = Mockito.mock( SiteService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        siteTemplateService = Mockito.mock( SiteTemplateService.class );

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).
            thenReturn( createContentType( "content-type" ) );

        final SiteResource resource = new SiteResource();
        resource.siteService = this.siteService;
        resource.contentTypeService = this.contentTypeService;
        resource.siteTemplateService = this.siteTemplateService;

        return resource;
    }

    private Content createSiteContent( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
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
            createdTime( Instant.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            site( site ).
            build();
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        RootDataSet rootDataSet = new RootDataSet();

        Property dataSet = new Property( "property1", Value.newString( "value1" ) );
        rootDataSet.add( dataSet );

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( Instant.parse( this.currentTime ) ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( contentTypeName ) ).
            site( null ).
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
