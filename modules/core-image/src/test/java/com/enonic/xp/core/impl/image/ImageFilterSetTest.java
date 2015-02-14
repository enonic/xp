package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.image.ImageFilter;
import com.enonic.xp.core.impl.image.ImageFilterSet;

import static org.junit.Assert.*;

public class ImageFilterSetTest
{
    @Test
    public void testEmpty()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );

        final ImageFilterSet set = new ImageFilterSet();
        assertSame( source, set.filter( source ) );
    }

    @Test
    public void testList()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );
        final BufferedImage target = Mockito.mock( BufferedImage.class );

        final ImageFilter filter = Mockito.mock( ImageFilter.class );
        Mockito.when( filter.filter( source ) ).thenReturn( target );

        final ImageFilterSet set = new ImageFilterSet();
        set.addFilter( filter );

        assertSame( target, set.filter( source ) );
    }
}
