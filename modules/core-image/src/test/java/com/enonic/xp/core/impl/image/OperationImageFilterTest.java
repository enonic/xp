package com.enonic.xp.core.impl.image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class OperationImageFilterTest
{
    @Test
    public void testFilter()
    {
        final BufferedImage source = Mockito.mock( BufferedImage.class );
        final BufferedImage target = Mockito.mock( BufferedImage.class );

        final BufferedImageOp op = Mockito.mock( BufferedImageOp.class );
        Mockito.when( op.filter( source, null ) ).thenReturn( target );

        final OperationImageFilter filter = new OperationImageFilter( op );

        final BufferedImage result = filter.filter( source );
        assertNotNull( result );
        assertSame( target, result );
    }
}
