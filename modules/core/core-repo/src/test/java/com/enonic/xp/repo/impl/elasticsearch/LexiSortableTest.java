package com.enonic.xp.repo.impl.elasticsearch;

import org.junit.Assert;
import org.junit.Test;

public class LexiSortableTest
{
    @Test
    public void toLexiSortable_long()
    {
        final String s1 = LexiSortable.toLexiSortable( 0L );
        final String s2 = LexiSortable.toLexiSortable( -1111L );
        final String s3 = LexiSortable.toLexiSortable( 1111L );

        Assert.assertEquals( "l8000000000000000", s1 );
        Assert.assertEquals( "l7FFFFFFFFFFFFBA9", s2 );
        Assert.assertEquals( "l8000000000000457", s3 );

        Assert.assertTrue( s2.compareTo( s1 ) < 0 );
        Assert.assertTrue( s2.compareTo( s3 ) < 0 );
        Assert.assertTrue( s3.compareTo( s1 ) > 0 );
        Assert.assertTrue( s3.compareTo( s2 ) > 0 );
    }

    @Test
    public void toLexiSortable_double()
    {
        final String s1 = LexiSortable.toLexiSortable( 0.0d );
        final String s2 = LexiSortable.toLexiSortable( -0.00001d );
        final String s3 = LexiSortable.toLexiSortable( 0.00001d );

        Assert.assertEquals( "d8000000000000000", s1 );
        Assert.assertEquals( "d411B074A771C970E", s2 );
        Assert.assertEquals( "dBEE4F8B588E368F1", s3 );

        Assert.assertTrue( s2.compareTo( s1 ) < 0 );
        Assert.assertTrue( s2.compareTo( s3 ) < 0 );
        Assert.assertTrue( s3.compareTo( s1 ) > 0 );
        Assert.assertTrue( s3.compareTo( s2 ) > 0 );
    }
}
