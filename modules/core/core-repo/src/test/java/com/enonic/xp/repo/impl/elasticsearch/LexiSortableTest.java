package com.enonic.xp.repo.impl.elasticsearch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexiSortableTest
{
    @Test
    void toLexiSortable_long()
    {
        final String s1 = LexiSortable.toLexiSortable( 0L );
        final String s2 = LexiSortable.toLexiSortable( -1111L );
        final String s3 = LexiSortable.toLexiSortable( 1111L );

        assertEquals( "l8000000000000000", s1 );
        assertEquals( "l7FFFFFFFFFFFFBA9", s2 );
        assertEquals( "l8000000000000457", s3 );

        assertTrue( s2.compareTo( s1 ) < 0 );
        assertTrue( s2.compareTo( s3 ) < 0 );
        assertTrue( s3.compareTo( s1 ) > 0 );
        assertTrue( s3.compareTo( s2 ) > 0 );
    }

    @Test
    void toLexiSortable_double()
    {
        final String s1 = LexiSortable.toLexiSortable( 0.0d );
        final String s2 = LexiSortable.toLexiSortable( -0.00001d );
        final String s3 = LexiSortable.toLexiSortable( 0.00001d );

        assertEquals( "d8000000000000000", s1 );
        assertEquals( "d411B074A771C970E", s2 );
        assertEquals( "dBEE4F8B588E368F1", s3 );

        assertTrue( s2.compareTo( s1 ) < 0 );
        assertTrue( s2.compareTo( s3 ) < 0 );
        assertTrue( s3.compareTo( s1 ) > 0 );
        assertTrue( s3.compareTo( s2 ) > 0 );
    }
}
