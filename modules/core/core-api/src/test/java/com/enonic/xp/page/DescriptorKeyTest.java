package com.enonic.xp.page;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractEqualsTest;

public class DescriptorKeyTest
{

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return DescriptorKey.from( "mainmodule:partTemplateName" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{DescriptorKey.from( "xeon:partTemplateName" ), DescriptorKey.from( "mainmodule:partTemplateName2" ),
                    new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new DescriptorKey( ApplicationKey.from( "mainmodule" ), "partTemplateName" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return DescriptorKey.from( "mainmodule:partTemplateName" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
