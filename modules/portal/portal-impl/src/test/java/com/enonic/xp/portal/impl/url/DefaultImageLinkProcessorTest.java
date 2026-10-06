package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.style.ImageStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultImageLinkProcessorTest
{
    private static final ImageStyle WIDE = ImageStyle.create().name( "wide" ).aspectRatio( "16:9" ).build();

    @Test
    void scaleOfWidth()
    {
        assertEquals( "width(768)", DefaultImageLinkProcessor.scale( null, null, null ) );
        assertEquals( "width(1000)", DefaultImageLinkProcessor.scale( null, null, 1000 ) );
    }

    @Test
    void heightFollowsTheAspectRatio()
    {
        assertEquals( "block(768,432)", DefaultImageLinkProcessor.scale( WIDE, null, null ) );
        // the height is rounded to the nearest pixel, not truncated before it is scaled
        assertEquals( "block(1000,563)", DefaultImageLinkProcessor.scale( WIDE, null, 1000 ) );
        assertEquals( "block(660,371)", DefaultImageLinkProcessor.scale( WIDE, null, 660 ) );
        assertEquals( "block(1000,667)", DefaultImageLinkProcessor.scale( null, "3:2", 1000 ) );
    }

    @Test
    void styleAspectRatioWinsOverScale()
    {
        assertEquals( "block(1000,563)", DefaultImageLinkProcessor.scale( WIDE, "1:1", 1000 ) );
    }
}
