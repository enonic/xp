package com.enonic.wem.api.support;


import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractEqualsTest
{
    @Test
    public void assertEqualsAndHashCodeContract()
    {
        testAllUnequalsAreUnequal();

        testReflexive();

        testSymmetric();

        testTransitive();

        testConsistent();

        assertHashCodeContract();
    }

    private void testAllUnequalsAreUnequal()
    {
        Object x = getObjectX();

        for ( Object unequal : getObjectsThatNotEqualsX() )
        {
            Assert.assertTrue( "expected to be unequal", !x.equals( unequal ) );
        }
    }


    /**
     * For any non-null reference value x, x.equals(x) should return true.
     */
    private void testReflexive()
    {
        // positive test
        Object x = getObjectX();
        Assert.assertTrue( "reflexive equals", x.equals( x ) );
    }

    /**
     * For any non-null reference values x and y, x.equals(y)  should return true if and only if y.equals(x) returns true.
     */
    private void testSymmetric()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        Assert.assertTrue( "symmetric equals", ( x.equals( y ) && y.equals( x ) ) );

        // negative test
        Object unequalToX = getObjectsThatNotEqualsX()[0];
        Assert.assertTrue( "reflexive equals", !x.equals( unequalToX ) && !unequalToX.equals( x ) );
    }

    /**
     * For any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should
     * return true.
     */
    private void testTransitive()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();
        Object z = getObjectThatEqualsXButNotTheSame2();

        Assert.assertTrue( "symmetric equals", ( x.equals( y ) && y.equals( z ) ) && x.equals( z ) );
    }

    /**
     * For any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true  or consistently return
     * false, provided no information used in equals comparisons on the objects is modified.
     */
    private void testConsistent()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        boolean firstCheck = x.equals( y );
        boolean secondCheck = x.equals( y );

        Assert.assertTrue( "consistent equals", firstCheck && secondCheck );

        Object unequalToX = getObjectsThatNotEqualsX()[0];
        firstCheck = x.equals( unequalToX );
        secondCheck = x.equals( unequalToX );

        Assert.assertTrue( "consistent equals", !firstCheck && !secondCheck );
    }

    private void assertHashCodeContract()
    {
        testHashCodeConsistency();
    }

    private void testHashCodeConsistency()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        Assert.assertTrue( "consistent hashCode", x.hashCode() == x.hashCode() );
        Assert.assertTrue( "hashCode produces same hash when objects are equal", x.hashCode() == y.hashCode() );

        boolean firstCheck = x.hashCode() == y.hashCode();
        boolean secondCheck = x.hashCode() == y.hashCode();

        Assert.assertTrue( "hashCode produces same hash when objects are equal consistently", firstCheck && secondCheck );
    }


    public abstract Object getObjectX();

    public abstract Object[] getObjectsThatNotEqualsX();

    public abstract Object getObjectThatEqualsXButNotTheSame();

    public abstract Object getObjectThatEqualsXButNotTheSame2();
}
