package com.enonic.xp.tools.testing.validate;

import java.io.File;

import org.junit.Test;

public class SiteValidatorTest
{
    @Test
    public void testApp1()
    {
        new SiteValidator().
            rootDir( new File( "./src/test/resources/app1" ) ).
            validate();
    }
}
