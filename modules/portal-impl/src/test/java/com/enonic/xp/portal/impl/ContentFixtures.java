package com.enonic.xp.portal.impl;

import java.time.Instant;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

public final class ContentFixtures
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
        builder.data( newPropertyTree() );

        builder.addExtraData( new ExtraData( MixinName.from( "myapplication:myschema" ), newTinyPropertyTree() ) );
        builder.page( newPage() );

        return builder.build();
    }

    public static PropertyTree newPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setLong( "a", 1L );
        tree.setString( "b", "2" );
        tree.setBoolean( "c.d", true );

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
        return builder.build();
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

    public static PageDescriptor newPageDescriptor()
    {
        Form pageForm = Form.create().
            addFormItem( Input.create().name( "pause" ).label( "Pause" ).inputType( InputTypeName.DOUBLE ).build() ).
            build();

        return PageDescriptor.create().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "header" ).build() ).
                add( RegionDescriptor.create().name( "main" ).build() ).
                add( RegionDescriptor.create().name( "footer" ).build() ).
                build() ).key( DescriptorKey.from( "myapplication:landing-page" ) ).
            build();
    }
}
