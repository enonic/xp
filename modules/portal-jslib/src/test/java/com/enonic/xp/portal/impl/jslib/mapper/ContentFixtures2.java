package com.enonic.xp.portal.impl.jslib.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.Contents;
import com.enonic.xp.core.content.Metadata;
import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.Page;
import com.enonic.xp.core.content.page.PageDescriptor;
import com.enonic.xp.core.content.page.PageRegions;
import com.enonic.xp.core.content.page.region.Component;
import com.enonic.xp.core.content.page.region.LayoutComponent;
import com.enonic.xp.core.content.page.region.LayoutRegions;
import com.enonic.xp.core.content.page.region.PartComponent;
import com.enonic.xp.core.content.page.region.Region;
import com.enonic.xp.core.content.site.ModuleConfig;
import com.enonic.xp.core.content.site.ModuleConfigs;
import com.enonic.xp.core.content.site.Site;
import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.form.Form;
import com.enonic.xp.core.form.inputtype.InputTypes;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.schema.mixin.MixinName;
import com.enonic.xp.core.security.PrincipalKey;
import com.enonic.xp.core.util.BinaryReference;
import com.enonic.xp.core.util.GeoPoint;
import com.enonic.xp.core.util.Link;

import static com.enonic.xp.core.content.page.region.RegionDescriptor.newRegionDescriptor;
import static com.enonic.xp.core.content.page.region.RegionDescriptors.newRegionDescriptors;
import static com.enonic.xp.core.form.Input.newInput;

public final class ContentFixtures2
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

    public static Contents newContents()
    {
        final Content content1 = Content.newContent().
            id( ContentId.from( "111111" ) ).
            name( "mycontent" ).
            displayName( "My Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Content content2 = Content.newContent().
            id( ContentId.from( "222222" ) ).
            name( "othercontent" ).
            displayName( "Other Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Content content3 = Content.newContent().
            id( ContentId.from( "333333" ) ).
            name( "another" ).
            displayName( "Another Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        return Contents.from( content1, content2, content3 );
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
        tree.addHtmlPart( "htmlPart", "<p>some<b>html</b></p>" );
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
        final LayoutComponent layout = builder.build();

        final Region parentRegion = Region.newRegion().name( "main" ).build();
        layout.setRegion( parentRegion );
        return layout;
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
