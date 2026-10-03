package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ImageStylesTest
{
    private static final ImageStyle FIRST_WIDE = style( "wide", "16:9" );

    private static final ImageStyle SECOND_WIDE = style( "wide", "21:9" );

    private static final ImageStyle SECOND_SQUARE = style( "square", "1:1" );

    private static final ImageStyle COLON_NAMED = style( "x:y", "4:3" );

    private final ImageStyles styles = new ImageStyles(
        StyleDescriptors.from( descriptor( "com.example.first", FIRST_WIDE, COLON_NAMED ), descriptor( "com.example.second", SECOND_WIDE, SECOND_SQUARE ) ) );

    @Test
    void nameAloneIsTheFirstStyleOfThatName()
    {
        assertSame( FIRST_WIDE, styles.get( "wide" ).style() );
        assertSame( SECOND_SQUARE, styles.get( "square" ).style() );
    }

    @Test
    void qualifiedNameIsTheStyleOfThatApplication()
    {
        assertSame( FIRST_WIDE, styles.get( "com.example.first:wide" ).style() );
        assertSame( SECOND_WIDE, styles.get( "com.example.second:wide" ).style() );
    }

    @Test
    void resolvedStyleGivesItsApplication()
    {
        assertEquals( ApplicationKey.from( "com.example.first" ), styles.get( "wide" ).application() );
        assertEquals( ApplicationKey.from( "com.example.second" ), styles.get( "com.example.second:wide" ).application() );
        assertEquals( ApplicationKey.from( "com.example.first" ), styles.get( "x:y" ).application() );
    }

    @Test
    void qualifiedNameOfAnotherApplicationHasNoStyle()
    {
        assertNull( styles.get( "com.example.first:square" ) );
        assertNull( styles.get( "com.example.unknown:wide" ) );
    }

    @Test
    void nameWithColonResolvesAsName()
    {
        assertSame( COLON_NAMED, styles.get( "x:y" ).style() );
        assertNull( styles.get( "missing" ) );
    }

    private static ImageStyle style( final String name, final String aspectRatio )
    {
        return ImageStyle.create().name( name ).label( name ).aspectRatio( aspectRatio ).build();
    }

    private static StyleDescriptor descriptor( final String application, final ImageStyle... styles )
    {
        final StyleDescriptor.Builder builder = StyleDescriptor.create().application( ApplicationKey.from( application ) );
        for ( final ImageStyle style : styles )
        {
            builder.addStyleElement( style );
        }
        return builder.build();
    }
}
