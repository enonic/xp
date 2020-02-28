package com.enonic.xp.support;


import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractEqualsTest
{
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
            assertTrue( !x.equals( unequal ), "expected to be unequal" );
        }
    }


    /**
     * For any non-null reference value x, x.equals(x) should return true.
     */
    private void testReflexive()
    {
        // positive test
        Object x = getObjectX();
        assertTrue( x.equals( x ), "reflexive equals" );
    }

    /**
     * For any non-null reference values x and y, x.equals(y)  should return true if and only if y.equals(x) returns true.
     */
    private void testSymmetric()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        assertTrue( ( x.equals( y ) && y.equals( x ) ), "symmetric equals" );

        // negative test
        Object unequalToX = getObjectsThatNotEqualsX()[0];
        assertTrue( !x.equals( unequalToX ) && !unequalToX.equals( x ), "reflexive equals" );
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

        assertTrue( ( x.equals( y ) && y.equals( z ) ) && x.equals( z ), "symmetric equals" );
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

        assertTrue( firstCheck && secondCheck , "consistent equals");

        Object unequalToX = getObjectsThatNotEqualsX()[0];
        firstCheck = x.equals( unequalToX );
        secondCheck = x.equals( unequalToX );

        assertTrue( !firstCheck && !secondCheck, "consistent equals" );
    }

    private void assertHashCodeContract()
    {
        testHashCodeConsistency();
    }

    private void testHashCodeConsistency()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        assertTrue( x.hashCode() == x.hashCode(), "consistent hashCode" );
        assertTrue( x.hashCode() == y.hashCode(), "hashCode produces same hash when objects are equal" );

        boolean firstCheck = x.hashCode() == y.hashCode();
        boolean secondCheck = x.hashCode() == y.hashCode();

        assertTrue( firstCheck && secondCheck, "hashCode produces same hash when objects are equal consistently" );
    }


    public abstract Object getObjectX();

    public abstract Object[] getObjectsThatNotEqualsX();

    public abstract Object getObjectThatEqualsXButNotTheSame();

    public abstract Object getObjectThatEqualsXButNotTheSame2();
}
