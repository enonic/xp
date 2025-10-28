package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DoubleHelperTest
{

    private static final double ZERO = 0.0;

    private static final double ONE = 1.0;

    @Test
    void test_compare()
    {
        assertTrue( DoubleHelper.fuzzyEquals( ONE, 1.0 ) );
        assertTrue( DoubleHelper.fuzzyEquals( ONE, 1.00000000000000001D ) );

        assertFalse( DoubleHelper.fuzzyEquals( ONE, 1.0001 ) );
        assertFalse( DoubleHelper.fuzzyEquals( ONE, -1.0 ) );
    }

    @Test
    void test_compare_zero()
    {
        assertTrue( DoubleHelper.fuzzyEquals( ZERO, 0.0 ) );
        assertTrue( DoubleHelper.fuzzyEquals( ZERO, 0.000000000000000001D ) );

        assertFalse( DoubleHelper.fuzzyEquals( ZERO, 0.0001 ) );
    }

    @Test
    void test_compare_with_diff_zero()
    {
        assertTrue( DoubleHelper.fuzzyEquals( ZERO, -0.0 ) );
        assertTrue( DoubleHelper.fuzzyEquals( ZERO, -0.0000000000000001 ) );

        assertFalse( DoubleHelper.fuzzyEquals( ZERO, -0.00001 ) );
    }


}
