package com.enonic.xp.core.impl.media;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

import static org.junit.Assert.*;

public class MediaInfoServiceTest
{
    private MediaInfoServiceImpl service;

    @Before
    public void setup()
    {
        this.service = new MediaInfoServiceImpl();
        service.setBinaryExtractor( source ->
                                    {
                                        Map<String, List<String>> data = Maps.newHashMap();
                                        data.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( "image/jpeg" ) );
                                        data.put( "myExtractedValue", Lists.newArrayList( "fisk" ) );

                                        return ExtractedData.create().
                                            metadata( data ).
                                            text( "myTextValue" ).
                                            imageOrientation( "1" ).
                                            build();
                                    } );
    }

    @Test
    public void createImmutableTextLine_generation()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "NikonD100.jpg" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );

        assertEquals( "image/jpeg", mediaInfo.getMediaType() );

        for ( Map.Entry<String, Collection<String>> entry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            System.out.println( "addFormItem( createImmutableTextLine( \"" + entry.getKey() + "\" ).occurrences( 0, 1 ).build() )." );
        }
    }

    @Test
    public void loadImageWithNativeOrientation()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "NikonD100.jpg" ) );
        final Content media = this.createMedia( "image", ContentPath.ROOT, false );
        final ImageOrientation orientation = this.service.getImageOrientation( byteSource, media );

        assertEquals( 1, orientation.getValue() );
    }

    @Test
    public void loadImageWithEditedOrientation()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "NikonD100.jpg" ) );
        final Content media = this.createMedia( "image", ContentPath.ROOT, true );

        final ImageOrientation orientation = this.service.getImageOrientation( byteSource, media );

        assertEquals( 3, orientation.getValue() );
    }

    @Test
    public void multiple_colorSpace_entries()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "Multiple-colorSpace-entries.jpg" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );
    }

    @Test
    public void multiple_FNumber_entries()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "Multiple-FNumber-entries.JPG" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );
    }

    private Content createMedia( String name, ContentPath parentPath, boolean addOrientation )
    {
        final PropertyTree imageDataTree = new PropertyTree();
        if ( addOrientation )
        {
            imageDataTree.addProperty( "orientation", ValueFactory.newString( "3" ) );
        }
        final ExtraData eData = new ExtraData( MixinName.from( "Image data" ), imageDataTree );

        return Content.create( ContentTypeName.imageMedia() ).name( name ).parentPath( parentPath ).addExtraData( eData ).build();
    }
}
