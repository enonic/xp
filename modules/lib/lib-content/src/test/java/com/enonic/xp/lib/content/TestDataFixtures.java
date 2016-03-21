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
import com.enonic.xp.content.Contents;
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
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
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

        builder.addExtraData( new ExtraData( MixinName.from( "com.enonic.myapplication:myschema" ), newTinyPropertyTree() ) );
        builder.page( newPage() );
        builder.attachments( newAttachments() );

        return builder.build();
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
            addExtraData( new ExtraData( MixinName.from( "com.enonic.myapplication:myschema" ), newTinyPropertyTree() ) ).
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
}
