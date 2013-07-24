package com.enonic.wem;

import java.util.Arrays;

import junit.framework.Assert;

public class TestUtil
{
    public static void assertUnorderedArraysEquals(Object[] a1, Object[] a2) {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort( b1 );
        Arrays.sort(b2);

        assertArraysEquals( b1, b2 );
    }

    public static void assertArraysEquals( final Object[] a1, final Object[] a2 ) {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static String arrayToString( final Object[] a ) {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ ) {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }
}
