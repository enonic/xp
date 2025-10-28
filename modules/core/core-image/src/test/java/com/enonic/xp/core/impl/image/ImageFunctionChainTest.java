package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;

class ImageFunctionChainTest
{
    @Test
    void testEmpty()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );

        final ImageFunctionChain set = new ImageFunctionChain( List.of() );
        assertSame( source, set.apply( source ) );
    }

    @Test
    void testList()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );
        final BufferedImage target = Mockito.mock( BufferedImage.class );

        final ImageFunction filter = Mockito.mock( ImageFunction.class );
        Mockito.when( filter.apply( source ) ).thenReturn( target );

        final ImageFunctionChain set = new ImageFunctionChain( List.of( filter ) );

        assertSame( target, set.apply( source ) );
    }
}
