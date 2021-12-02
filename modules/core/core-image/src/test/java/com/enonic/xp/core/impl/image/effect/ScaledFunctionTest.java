package com.enonic.xp.core.impl.image.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScaledFunctionTest
{
    @Test
    void estimateResolution()
    {
        final ScaledFunction scaledFunction = new ScaledFunction( ScaleCalculator.block( 10, 15, 0.5, 0.5 ) );
        assertEquals( 150, scaledFunction.estimateResolution( 1000, 1500 ) );

    }
}