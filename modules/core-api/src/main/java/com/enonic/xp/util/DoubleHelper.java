package com.enonic.xp.util;


import com.google.common.math.DoubleMath;

public class DoubleHelper
{
    private static final Double EPSILON = 1.11e-16;

    public static boolean fuzzyEquals( final double first, final double second )
    {
        return DoubleMath.fuzzyEquals( first, second, EPSILON );
    }
}
