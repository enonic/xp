package com.enonic.xp.portal.impl;

import java.time.Instant;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Metadata;
import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.Page;
import com.enonic.xp.content.page.PageDescriptor;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.LayoutComponent;
import com.enonic.xp.content.page.region.LayoutRegions;
import com.enonic.xp.content.page.region.PartComponent;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.xp.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.xp.form.Input.newInput;

public final class ContentFixtures
{
    public static Content newContent()
    {
        final Content.Builder builder = Content.newContent();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "mycontent" );
        builder.displayName( "My Content" );
        builder.parentPath( ContentPath.from( "/a/b" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( newPropertyTree() );

        builder.addMetadata( new Metadata( MixinName.from( "mymodule:myschema" ), newTinyPropertyTree() ) );
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
        final Page.Builder builder = Page.newPage();
        builder.config( newTinyPropertyTree() );
        builder.controller( DescriptorKey.from( "mymodule:mycontroller" ) );
        builder.regions( newPageRegions() );
        return builder.build();
    }

    public static PageRegions newPageRegions()
    {
        final PageRegions.Builder builder = PageRegions.newPageRegions();
        builder.add( newTopRegion() );
        return builder.build();
    }

    public static Region newTopRegion()
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( "top" );
        builder.add( newPartComponent() );
        builder.add( newLayoutComponent() );
        return builder.build();
    }

    public static Region newBottomRegion()
    {
        final Region.Builder builder = Region.newRegion();
        builder.name( "bottom" );
        builder.add( newPartComponent() );
        return builder.build();
    }

    public static Component newPartComponent()
    {
        final PartComponent.Builder builder = PartComponent.newPartComponent();
        builder.name( "mypart" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "mymodule:mypart" ) );
        return builder.build();
    }

    public static LayoutComponent newLayoutComponent()
    {
        final LayoutComponent.Builder builder = LayoutComponent.newLayoutComponent();
        builder.name( "mylayout" );
        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "mymodule:mylayout" ) );
        builder.regions( newLayoutRegions() );
        return builder.build();
    }

    public static LayoutRegions newLayoutRegions()
    {
        final LayoutRegions.Builder builder = LayoutRegions.newLayoutRegions();
        builder.add( newBottomRegion() );
        return builder.build();
    }

    public static Site newSite()
    {
        final PropertyTree moduleConfigConfig = new PropertyTree();
        moduleConfigConfig.setLong( "Field", 42l );

        final ModuleConfig moduleConfig = ModuleConfig.newModuleConfig().
            module( ModuleKey.from( "mymodule" ) ).
            config( moduleConfigConfig ).
            build();

        final Site.Builder site = Site.newSite();
        site.id( ContentId.from( "100123" ) );
        site.moduleConfigs( ModuleConfigs.from( moduleConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

    public static PageDescriptor newPageDescriptor()
    {
        Form pageForm = Form.newForm().
            addFormItem( newInput().name( "pause" ).inputType( InputTypes.DOUBLE ).build() ).
            build();

        return PageDescriptor.newPageDescriptor().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( newRegionDescriptors().
                add( newRegionDescriptor().name( "header" ).build() ).
                add( newRegionDescriptor().name( "main" ).build() ).
                add( newRegionDescriptor().name( "footer" ).build() ).
                build() ).key( DescriptorKey.from( "mymodule:landing-page" ) ).
            build();
    }
}
