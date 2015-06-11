package com.enonic.xp.tools.testing.validate;

import java.io.File;

import org.junit.Test;

public class ModuleValidatorTest
{
    @Test
    public void testApp1()
    {
        new ModuleValidator().
            rootDir( new File( "./src/test/resources/module1" ) ).
            validate();
    }
}
