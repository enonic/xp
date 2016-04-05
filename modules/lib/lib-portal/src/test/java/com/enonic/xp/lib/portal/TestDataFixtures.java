package com.enonic.xp.lib.portal;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;

public final class TestDataFixtures
{
    public static Content newContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "mycontent" );
        builder.displayName( "My Content" );
        builder.parentPath( ContentPath.from( "/a/b" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.language( Locale.ENGLISH );
        builder.data( newPropertyTree() );

        builder.addExtraData( new ExtraData( MixinName.from( "com.enonic.myapplication:myschema" ), newTinyPropertyTree() ) );
        builder.page( newPage() );

        return builder.build();
    }

    public static Content newExampleContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "mycontent" );
        builder.displayName( "My Content" );
        builder.parentPath( ContentPath.from( "/a/b" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.language( Locale.ENGLISH );
        builder.data( newTinyPropertyTree() );
        return builder.build();
    }

    public static PropertyTree newPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setBoolean( "boolean", true );
        tree.addBooleans( "boolean", true, false );
        tree.setLong( "long", 1L );
        tree.addLongs( "longs", 1L, 2L, 3L );
        tree.setDouble( "double", 2.2 );
        tree.addDoubles( "doubles", 1.1, 2.2, 3.3 );
        tree.setString( "string", "a" );
        tree.addStrings( "strings", "a", "b", "c" );
        tree.setString( "stringEmpty", "" );
        tree.setString( "stringNull", null );
        tree.setString( "set.property", "value" );
        tree.addXml( "xml", "<xml><my-xml hello='world'/></xml>" );
        tree.addBinaryReference( "binaryReference", BinaryReference.from( "abc" ) );
        tree.addLink( "link", Link.from( ContentPath.from( "/my/content" ).toString() ) );
        tree.addGeoPoint( "geoPoint", new GeoPoint( 1.1, -1.1 ) );
        tree.addGeoPoints( "geoPoints", new GeoPoint( 1.1, -1.1 ), new GeoPoint( 2.2, -2.2 ) );
        tree.addInstant( "instant", Instant.MAX );
        tree.addLocalDate( "localDate", LocalDate.of( 2014, 1, 31 ) );
        tree.addLocalDateTime( "localDateTime", LocalDateTime.of( 2014, 1, 31, 10, 30, 5 ) );

        final PropertySet set1 = tree.addSet( "c" );
        set1.setBoolean( "d", true );
        set1.addStrings( "e", "3", "4", "5" );
        set1.setLong( "f", 2L );

        return tree;
    }

    public static PropertyTree newTinyPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setString( "a", "1" );
        return tree;
    }

    public static Page newPage()
    {
        final Page.Builder builder = Page.create();
        builder.config( newTinyPropertyTree() );
        builder.controller( DescriptorKey.from( "myapplication:mycontroller" ) );
        builder.regions( newPageRegions() );
        return builder.build();
    }

    public static PageRegions newPageRegions()
    {
        final PageRegions.Builder builder = PageRegions.create();
        builder.add( newTopRegion() );
        return builder.build();
    }

    public static Region newTopRegion()
    {
        final Region.Builder builder = Region.create();
        builder.name( "top" );
        builder.add( newPartComponent() );
        builder.add( newLayoutComponent() );
        return builder.build();
    }

    public static Region newBottomRegion()
    {
        final Region.Builder builder = Region.create();
        builder.name( "bottom" );
        builder.add( newPartComponent() );
        return builder.build();
    }

    public static Component newPartComponent()
    {
        final PartComponent.Builder builder = PartComponent.create();
        builder.name( "mypart" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "myapplication:mypart" ) );
        return builder.build();
    }

    public static LayoutComponent newLayoutComponent()
    {
        final LayoutComponent.Builder builder = LayoutComponent.create();
        builder.name( "mylayout" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "myapplication:mylayout" ) );
        builder.regions( newLayoutRegions() );
        final LayoutComponent layout = builder.build();
        Region.create().name( "main" ).add( layout ).build();
        return layout;
    }

    public static LayoutRegions newLayoutRegions()
    {
        final LayoutRegions.Builder builder = LayoutRegions.create();
        builder.add( newBottomRegion() );
        return builder.build();
    }

    public static Site newSite()
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "Field", 42l );

        final SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapplication" ) ).
            config( siteConfigConfig ).
            build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "100123" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }
}
