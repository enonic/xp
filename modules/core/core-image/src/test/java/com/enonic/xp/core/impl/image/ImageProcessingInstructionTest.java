package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageProcessingInstructionTest
{
    @Test
    void basicConstruction()
    {
        final ReadImageParams params = ReadImageParams.newImageParams()
            .contentId( ContentId.from( "123" ) )
            .binaryReference( BinaryReference.from( "456" ) )
            .mimeType( "image/webp" )
            .quality( 85 )
            .scaleParams( new ScaleParams( "width", new Object[]{500} ) )
            .orientation( ImageOrientation.BottomRight )
            .filterParam( "blur(5)" )
            .build();

        final NormalizedImageParams normalized = new NormalizedImageParams( params );
        final ImageProcessingInstruction instruction = new ImageProcessingInstruction( normalized, true );

        assertEquals( "webp", instruction.getFormat() );
        assertEquals( 85, instruction.getQuality() );
        assertEquals( "width", instruction.getScaleParams().getName() );
        assertEquals( ImageOrientation.BottomRight, instruction.getOrientation() );
        assertEquals( "blur(5)", instruction.getFilterParam() );
        assertTrue( instruction.isProgressive() );
        assertEquals( 0xFFFFFF, instruction.getBackgroundColor() );
    }

    @Test
    void notProgressive()
    {
        final ReadImageParams params = ReadImageParams.newImageParams()
            .contentId( ContentId.from( "123" ) )
            .binaryReference( BinaryReference.from( "456" ) )
            .mimeType( "image/jpeg" )
            .build();

        final NormalizedImageParams normalized = new NormalizedImageParams( params );
        final ImageProcessingInstruction instruction = new ImageProcessingInstruction( normalized, false );

        assertEquals( "jpeg", instruction.getFormat() );
        assertFalse( instruction.isProgressive() );
    }
}
