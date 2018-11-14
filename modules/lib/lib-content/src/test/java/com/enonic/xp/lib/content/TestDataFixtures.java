package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
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
        builder.publishInfo( ContentPublishInfo.create().
            from( Instant.parse( "2016-11-03T10:00:00Z" ) ).
            to( Instant.parse( "2016-11-23T10:00:00Z" ) ).
            build() );
        builder.addExtraData( new ExtraData( XDataName.from( "com.enonic.myapplication:myschema" ), newTinyPropertyTree() ) );
        builder.page( newPage() );
        builder.attachments( newAttachments() );

        return builder.build();
    }

    public static Content newContentWithPageAsFragment()
    {
        return newExampleContentBuilder().page( Page.create().
            fragment( createLayoutComponent() ).
            build() ).build();
    }


    public static Content newExampleContent()
    {
        return newExampleContentBuilder().build();
    }

    public static Content.Builder newExampleContentBuilder()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "mycontent" );
        builder.displayName( "My Content" );
        builder.parentPath( ContentPath.from( "/path/to" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.language( Locale.ENGLISH );

        final PropertyTree tree = new PropertyTree();
        tree.setString( "myfield", "Hello World" );

        builder.data( tree );
        builder.attachments( newAttachments() );
        builder.valid( true );
        builder.childOrder( ChildOrder.from( "_ts DESC, _name ASC" ) );

        return builder;
    }

    public static Content newSmallContent()
    {
        final Content.Builder builder = Content.create().
            id( ContentId.from( "123456" ) ).
            name( "mycontent" ).
            type( ContentTypeName.from( "test:myContentType" ) ).
            displayName( "My Content" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            data( newSmallPropertyTree() ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                build() ).
            addExtraData( new ExtraData( XDataName.from( "com.enonic.myapplication:myschema" ), newTinyPropertyTree() ) ).
            page( newPage() );

        return builder.build();
    }

    public static Contents newContents( final int num )
    {
        final List<Content> list = Lists.newArrayList();
        for ( int i = 0; i < num; i++ )
        {
            final Content content = Content.create().
                id( ContentId.from( "id" + ( i + 1 ) ) ).
                name( "name" + ( i + 1 ) ).
                displayName( "My Content " + ( i + 1 ) ).
                parentPath( ContentPath.from( "/a/b" ) ).
                modifier( PrincipalKey.from( "user:system:admin" ) ).
                modifiedTime( Instant.ofEpochSecond( 0 ) ).
                creator( PrincipalKey.from( "user:system:admin" ) ).
                createdTime( Instant.ofEpochSecond( 0 ) ).
                build();

            list.add( content );
        }

        return Contents.from( list );
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

    public static PropertyTree newSmallPropertyTree()
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

    public static PropertyTree newImageComponentPropertyTree()
    {
        final PropertyTree tree = new PropertyTree();
        tree.setString( "caption", "Caption" );
        return tree;
    }

    public static Page newPage()
    {
        final Page.Builder builder = Page.create();

        builder.config( newTinyPropertyTree() );
        builder.descriptor( DescriptorKey.from( "my-app-key:mycontroller" ) );
        builder.regions( newPageRegions() );

        return builder.build();
    }

    public static PageRegions newPageRegions()
    {
        return PageRegions.create().
            add( newTopRegion() ).
            add( newBottomRegion() ).
            build();
    }

    public static Region newTopRegion()
    {
        return Region.create().
            name( "top" ).
            add( createPartComponent( "MyPart1", "app-descriptor-x:name-x", newTinyPropertyTree() ) ).
            add( createLayoutComponent() ).
            add( LayoutComponent.create().name( "Layout" ).build() ).
            build();
    }

    public static Region newBottomRegion()
    {
        return Region.create().
            name( "bottom" ).
            add( createPartComponent( "MyPart2", "app-descriptor-y:name-y", newTinyPropertyTree() ) ).
            add( createImageComponent( "img-id-x", "Image Component", newImageComponentPropertyTree() ) ).
            add( ImageComponent.create().name( "Image" ).build() ).
            build();
    }

    public static LayoutRegions newLayoutRegions()
    {
        final LayoutRegions.Builder builder = LayoutRegions.create();
        builder.add( newBottomRegion() );
        return builder.build();
    }

    public static Attachments newAttachments()
    {
        final Attachment a1 = Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            size( 6789 ).
            build();
        final Attachment a2 = Attachment.create().
            name( "document.pdf" ).
            mimeType( "application/pdf" ).
            size( 12345 ).
            build();
        return Attachments.from( a1, a2 );
    }

    private static ImageComponent createImageComponent( final String imageId, final String imageDisplayName,
                                                        final PropertyTree imageConfig )
    {
        final ContentId id = ContentId.from( imageId );
        final Content imageContent = Content.create().
            name( "someimage" ).
            displayName( imageDisplayName ).
            parentPath( ContentPath.ROOT ).
            build();

        return ImageComponent.create().
            name( imageDisplayName ).
            image( id ).
            config( imageConfig ).
            build();
    }

    private static FragmentComponent createFragmentComponent( final String fragmentId, final String fragmentDisplayName )
    {
        final ContentId id = ContentId.from( fragmentId );
        final Content fragmentContent = Content.create().
            name( "somefragment" ).
            displayName( fragmentDisplayName ).
            parentPath( ContentPath.ROOT ).
            build();

        return FragmentComponent.create().
            name( fragmentDisplayName ).
            fragment( id ).
            build();
    }

    private static PartComponent createPartComponent( final String partName, final String descriptorKey, final PropertyTree partConfig )
    {
        final DescriptorKey descriptor = DescriptorKey.from( descriptorKey );

        return PartComponent.create().
            name( partName ).
            descriptor( descriptor ).
            config( partConfig ).
            build();
    }

    private static LayoutComponent createLayoutComponent()
    {
        final Region region1 = Region.create().
            name( "left" ).
            add( PartComponent.create().
                name( "Part" ).
                build() ).
            add( TextComponent.create().
                name( "Text" ).
                text( "text text text" ).
                build() ).
            add( TextComponent.create().
                name( "Text" ).
                build() ).
            build();

        final Region region2 = Region.create().
            name( "right" ).
            add( createImageComponent( "image-id", "Some Image", null ) ).
            add( createFragmentComponent( "213sda-ss222", "My Fragment" ) ).
            build();

        final LayoutRegions layoutRegions = LayoutRegions.create().add( region1 ).add( region2 ).build();

        return LayoutComponent.create().name( "MyLayout" ).descriptor( "layoutDescriptor:name" ).regions( layoutRegions ).build();
    }
}
