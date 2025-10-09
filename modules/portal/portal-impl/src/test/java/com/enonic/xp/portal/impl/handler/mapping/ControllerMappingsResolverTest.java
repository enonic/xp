package com.enonic.xp.portal.impl.handler.mapping;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerMappingsResolverTest
{
    private SiteService siteService;

    @BeforeEach
    public final void setup()
        throws Exception
    {
        this.siteService = Mockito.mock( SiteService.class );
    }

    @Test
    public void testNoDescriptors()
    {
        final Content content = newContent();
        final Site site = newSite();

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );
        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/landing-page", ImmutableMultimap.of(), content, site.getSiteConfigs(), null );

        assertTrue( mapping.isEmpty() );
    }

    @Test
    public void testResolve()
    {
        final Content content = newContent();
        final Site site = newSite();

        final SiteDescriptor siteDescriptor = newSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( getAppKey() ) ).thenReturn( siteDescriptor );
        final SiteDescriptor siteDescriptor2 = newSiteDescriptor2();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor2 );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );
        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/landing-page", ImmutableMultimap.of(), content, site.getSiteConfigs(), null );

        assertThat( mapping ).map( ControllerMappingDescriptor::getController )
            .map( ResourceKey::getPath )
            .contains( "/site/controllers/controller2.js" );
    }

    @Test
    public void testResolveWithParameters()
    {
        final Content content = newContent();
        final Site site = newSite();

        final SiteDescriptor siteDescriptor = newSiteDescriptor3();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );

        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/api", ImmutableMultimap.of( "key", "123", "category", "foo" ), content, site.getSiteConfigs(), null );

        assertThat( mapping ).map( ControllerMappingDescriptor::getController )
            .map( ResourceKey::getPath )
            .contains( "/other/controller1.js" );
    }

    @Test
    public void testResolvePatternWithParametersNoMatch()
    {
        final Content content = newContent();
        final Site site = newSite();

        final SiteDescriptor siteDescriptor = newSiteDescriptor3();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );

        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/api", ImmutableMultimap.of(), content, site.getSiteConfigs(), null );

        assertTrue( mapping.isEmpty() );
    }

    @Test
    public void testResolveService()
    {
        final Content content = newContent();
        final Site site = newSite();

        final SiteDescriptor siteDescriptor = newServiceMappingDescriptor();

        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );

        final Optional<ControllerMappingDescriptor> serviceMapping =
            resolver.resolve( "/api", ImmutableMultimap.of(), content, site.getSiteConfigs(), "image" );

        final Optional<ControllerMappingDescriptor> patternMapping =
            resolver.resolve( "/api", ImmutableMultimap.of( "key", "123", "category", "foo" ), content, site.getSiteConfigs(), null );

        assertFalse( patternMapping.isEmpty() );
        assertFalse( serviceMapping.isEmpty() );
    }

    @Test
    public void testPatternCatchAll_matches_anything()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .pattern( "/.*" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );

        final Content content = newContent();
        final Site site = newSite();

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( getAppKey2() ).mappingDescriptors( mappings ).build();

        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );

        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/landing-page", ImmutableMultimap.of(), content, site.getSiteConfigs(), null );

        assertThat( mapping ).map( ControllerMappingDescriptor::getController )
            .map( ResourceKey::getPath )
            .contains( "/other/controller1.js" );

        final Optional<ControllerMappingDescriptor> mapping2 =
            resolver.resolve( "/does-not-exist", ImmutableMultimap.of(), null, site.getSiteConfigs(), null );

        assertThat( mapping2 ).map( ControllerMappingDescriptor::getController )
            .map( ResourceKey::getPath )
            .contains( "/other/controller1.js" );

        final Optional<ControllerMappingDescriptor> mapping3 =
            resolver.resolve( "/", ImmutableMultimap.of(), site, site.getSiteConfigs(), null );

        assertThat( mapping3 ).map( ControllerMappingDescriptor::getController )
            .map( ResourceKey::getPath )
            .contains( "/other/controller1.js" );
    }

    @Test
    public void testPatternCatchAllType_not_matches_missing()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .contentConstraint( "type:'.+:.+'" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );

        final Site site = newSite();

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().applicationKey( getAppKey2() ).mappingDescriptors( mappings ).build();

        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService );

        final Optional<ControllerMappingDescriptor> mapping =
            resolver.resolve( "/does-not-exist", ImmutableMultimap.of(), null, site.getSiteConfigs(), null );

        assertTrue( mapping.isEmpty() );

    }

    private SiteDescriptor newSiteDescriptor()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey(), "/site/controllers/controller1.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "_id:'123456'" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptor mapping2 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey(), "/site/controllers/controller2.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "_path:'/mysite/landing-page'" )
            .order( 5 )
            .build();
        final ControllerMappingDescriptor mapping3 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey(), "/site/controllers/controller3.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "_name:'landing-page'" )
            .order( 15 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1, mapping2, mapping3 );
        return SiteDescriptor.create().applicationKey( getAppKey() ).mappingDescriptors( mappings ).build();
    }

    private SiteDescriptor newSiteDescriptor2()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "_id:'123456'" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptor mapping2 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller2.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "_path:'/mysite/landing-page'" )
            .order( 5 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1, mapping2 );
        return SiteDescriptor.create().applicationKey( getAppKey2() ).mappingDescriptors( mappings ).build();
    }

    private SiteDescriptor newSiteDescriptor3()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .pattern( "/.*api.*\\?category=.*&key=\\d+" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );
        return SiteDescriptor.create().applicationKey( getAppKey2() ).mappingDescriptors( mappings ).build();
    }

    private SiteDescriptor newDescriptorForFragments()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey(), "/site/controllers/controller1.js" ) )
            .pattern( "/.*" )
            .contentConstraint( "type:'portal:fragment'" )
            .order( 10 )
            .build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );
        return SiteDescriptor.create().mappingDescriptors( mappings ).build();
    }

    private SiteDescriptor newServiceMappingDescriptor()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .service( "image" )
            .order( 10 )
            .build();

        final ControllerMappingDescriptor mapping2 = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) )
            .pattern( "/.*api.*\\?category=.*&key=\\d+" )
            .order( 10 )
            .build();

        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1, mapping2 );
        return SiteDescriptor.create().applicationKey( getAppKey2() ).mappingDescriptors( mappings ).build();
    }

    private Content newContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "landing-page" );
        builder.displayName( "My Landing Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( getAppKey(), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        return builder.build();
    }

    private Content newFragmentContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "897546" ) );
        builder.name( "my-fragment" );
        builder.displayName( "My fragment" );
        builder.parentPath( ContentPath.from( "/mysite/fragment" ) );
        builder.type( ContentTypeName.fragment() );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        return builder.build();
    }

    private Site newSite()
    {
        final SiteConfig siteConfig = SiteConfig.create().application( getAppKey() ).config( new PropertyTree() ).build();

        final SiteConfig siteConfig2 = SiteConfig.create().application( getAppKey2() ).config( new PropertyTree() ).build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "100123" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig, siteConfig2 ) );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

    private ApplicationKey getAppKey()
    {
        return ApplicationKey.from( "com.enonic.test.app" );
    }

    private ApplicationKey getAppKey2()
    {
        return ApplicationKey.from( "com.enonic.test.otherapp" );
    }
}
