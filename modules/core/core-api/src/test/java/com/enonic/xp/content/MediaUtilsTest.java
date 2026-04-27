package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.image.Cropping;
import com.enonic.xp.image.FocalPoint;
import com.enonic.xp.media.ImageOrientation;

import static org.assertj.core.api.Assertions.assertThat;

class MediaUtilsTest
{
    @Test
    void readCropping_returnsImageSpaceCropping()
    {
        final PropertySet mediaData = newMediaData();
        final PropertySet crop = mediaData.addSet( ContentPropertyNames.MEDIA_CROPPING );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );

        final Cropping result = MediaUtils.readCropping( mediaData );

        assertThat( result ).isNotNull();
        assertThat( result.top() ).isEqualTo( 0.10 );
        assertThat( result.left() ).isEqualTo( 0.20 );
        assertThat( result.bottom() ).isEqualTo( 0.80 );
        assertThat( result.right() ).isEqualTo( 0.90 );
    }

    @Test
    void readCropping_ignoresLeftoverZoomProperty()
    {
        final PropertySet mediaData = newMediaData();
        final PropertySet crop = mediaData.addSet( ContentPropertyNames.MEDIA_CROPPING );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_RIGHT, 0.90 );
        crop.setDouble( "zoom", 2.0 );

        final Cropping result = MediaUtils.readCropping( mediaData );

        assertThat( result ).isNotNull();
        assertThat( result.top() ).isEqualTo( 0.10 );
        assertThat( result.right() ).isEqualTo( 0.90 );
    }

    @Test
    void readCropping_returnsNullWhenEdgeMissing()
    {
        final PropertySet mediaData = newMediaData();
        final PropertySet crop = mediaData.addSet( ContentPropertyNames.MEDIA_CROPPING );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_TOP, 0.10 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_LEFT, 0.20 );
        crop.setDouble( ContentPropertyNames.MEDIA_CROPPING_BOTTOM, 0.80 );

        assertThat( MediaUtils.readCropping( mediaData ) ).isNull();
    }

    @Test
    void readCropping_returnsNullWhenNoCroppingSet()
    {
        assertThat( MediaUtils.readCropping( newMediaData() ) ).isNull();
    }

    @Test
    void readCropping_returnsNullForNullMediaSet()
    {
        assertThat( MediaUtils.readCropping( null ) ).isNull();
    }

    @Test
    void readOrientation_returnsEnumForValidLong()
    {
        final PropertySet mediaSet = newMediaData();
        mediaSet.setLong( ContentPropertyNames.ORIENTATION, 6L );

        assertThat( MediaUtils.readOrientation( mediaSet ) ).isEqualTo( ImageOrientation.RightTop );
    }

    @Test
    void readOrientation_returnsNullWhenAbsent()
    {
        assertThat( MediaUtils.readOrientation( newMediaData() ) ).isNull();
    }

    @Test
    void readOrientation_returnsDefaultOnArithmeticException()
    {
        final PropertySet mediaSet = newMediaData();
        mediaSet.setLong( ContentPropertyNames.ORIENTATION, Long.MAX_VALUE );

        assertThat( MediaUtils.readOrientation( mediaSet ) ).isEqualTo( ImageOrientation.DEFAULT );
    }

    @Test
    void readOrientation_returnsNullForNullMediaSet()
    {
        assertThat( MediaUtils.readOrientation( null ) ).isNull();
    }

    @Test
    void readFocalPoint_returnsPointWhenFullPair()
    {
        final PropertySet mediaData = newMediaData();
        final PropertySet focal = mediaData.addSet( ContentPropertyNames.MEDIA_FOCAL_POINT );
        focal.setDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_X, 0.4 );
        focal.setDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_Y, 0.6 );

        final FocalPoint result = MediaUtils.readFocalPoint( mediaData );

        assertThat( result ).isNotNull();
        assertThat( result.xOffset() ).isEqualTo( 0.4 );
        assertThat( result.yOffset() ).isEqualTo( 0.6 );
    }

    @Test
    void readFocalPoint_returnsNullWhenPartial()
    {
        final PropertySet mediaSet = newMediaData();
        final PropertySet focal = mediaSet.addSet( ContentPropertyNames.MEDIA_FOCAL_POINT );
        focal.setDouble( ContentPropertyNames.MEDIA_FOCAL_POINT_X, 0.4 );

        assertThat( MediaUtils.readFocalPoint( mediaSet ) ).isNull();
    }

    @Test
    void readFocalPoint_returnsNullForNullMediaSet()
    {
        assertThat( MediaUtils.readFocalPoint( null ) ).isNull();
    }

    private static PropertySet newMediaData()
    {
        return new PropertyTree().addSet( ContentPropertyNames.MEDIA );
    }
}
