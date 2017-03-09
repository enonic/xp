package com.enonic.xp.issue;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class IssuePathTest
{
    @Test
    public void testEquals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return IssuePath.from( IssueName.from( "myissue" ) );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{IssuePath.from( IssueName.from( "myotherissue" ) )};
            }

            IssueName issueName = IssueName.from( "myissue" );

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return IssuePath.from( issueName );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return IssuePath.from( issueName );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testIssuePathValue()
    {
        IssuePath issuePath = IssuePath.from( IssueName.from( "myissue" ) );

        assertEquals( "/issue/myissue", issuePath.getValue() );
    }
}
