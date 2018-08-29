package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.time.Instant;
import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;

import static org.junit.Assert.*;

public class ControllerMappingsResolverTest
{
    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Test
    public void testCanRender()
        throws Exception
    {
        final SiteService siteService = Mockito.mock( SiteService.class );

        final Content content = createContent();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        final Site site = createSite( SiteConfigs.from( siteConfig ) );

        final ControllerMappingDescriptor mapingDescriptor = ControllerMappingDescriptor.create().
            contentConstraint( "type:'.*:content-type'" ).
            controller( ResourceKey.from( "myapplication:/some/path" ) ).
            build();
        final SiteDescriptor siteDescriptor =
            SiteDescriptor.create().mappingDescriptors( ControllerMappingDescriptors.from( mapingDescriptor ) ).build();

        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( siteService );
        final boolean canBeRendered = resolver.canRender( content, site );

        assertTrue( canBeRendered );
    }

    @Test
    public void testCanRenderNoDescriptors()
        throws Exception
    {
        final SiteService siteService = Mockito.mock( SiteService.class );

        final Content content = createContent();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        final Site site = createSite( SiteConfigs.from( siteConfig ) );

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().mappingDescriptors( ControllerMappingDescriptors.empty() ).build();

        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( siteService );
        final boolean canBeRendered = resolver.canRender( content, site );

        assertFalse( canBeRendered );
    }

    @Test
    public void testCanRenderNoMatch()
        throws Exception
    {
        final SiteService siteService = Mockito.mock( SiteService.class );

        final Content content = createContent();

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        final Site site = createSite( SiteConfigs.from( siteConfig ) );

        final ControllerMappingDescriptor mapingDescriptor = ControllerMappingDescriptor.create().
            contentConstraint( "type:'.*:other-content-type'" ).
            controller( ResourceKey.from( "myapplication:/some/path" ) ).
            build();
        final SiteDescriptor siteDescriptor =
            SiteDescriptor.create().mappingDescriptors( ControllerMappingDescriptors.from( mapingDescriptor ) ).build();

        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( siteService );
        final boolean canBeRendered = resolver.canRender( content, site );

        assertFalse( canBeRendered );
    }

    private Content createContent()
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create().
            id( ContentId.from( "83ac6e65-791b-4398-9ab5-ff5cab999036" ) ).
            parentPath( ContentPath.ROOT ).
            name( "content-name" ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( "myapplication:content-type" ) ).
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                first( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build() ).
            build();
    }

    private Site createSite( SiteConfigs siteConfigs )
    {
        return Site.create().
            siteConfigs( siteConfigs ).
            id( ContentId.from( "8dcb8d39-e3be-4b2d-99dd-223666fc900c" ) ).
            parentPath( ContentPath.ROOT ).
            name( "my-site" ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.site() ).
            build();
    }
}
