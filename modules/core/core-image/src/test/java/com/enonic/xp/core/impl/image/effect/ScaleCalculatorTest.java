package com.enonic.xp.core.impl.image.effect;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScaleCalculatorTest
{
    @Test
    void block_upscale_focal_upper_left()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 1000, 1000, 0, 0 ).calc( 100, 200 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 1000, 2000, 0, 0, 1000, 1000 );
    }

    @Test
    void block_focal_upper_left()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 10, 10, 0, 0 ).calc( 100, 200 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 10, 20, 0, 0, 10, 10 );
    }

    @Test
    void block_focal_lower_left()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 10, 10, 0, 1 ).calc( 100, 200 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 10, 20, 0, 10, 10, 10 );
    }

    @Test
    void block_mixscale_focal_lower_left()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 1000, 10, 0, 1 ).calc( 100, 200 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 1000, 2000, 0, 1990, 1000, 10 );
    }

    @Test
    void block_focal_upper_right()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 10, 10, 1, 0 ).calc( 200, 100 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 20, 10, 10, 0, 10, 10 );
    }

    @Test
    void block_focal_lower_right()
    {
        final ScaleCalculator.Values values = ScaleCalculator.block( 10, 10, 1, 1 ).calc( 200, 100 );

        assertThat( values ).extracting( v -> v.newWidth, v -> v.newHeight, v -> v.widthOffset, v -> v.heightOffset, v -> v.viewWidth,
                                         v -> v.viewHeight ).containsExactly( 20, 10, 10, 0, 10, 10 );
    }

}