package com.enonic.xp.app;

import org.junit.Test;

import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class ApplicationKeyTest
{
    @Test
    public void testCreate()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }

    @Test
    public void testEquals()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
        assertEquals( ApplicationKey.from( "myapplication" ).hashCode(), applicationKey.hashCode() );
    }

    @Test
    public void testFromModuleKey()
    {
        final ModuleKey module = ModuleKey.from( "mymodule" );
        ApplicationKey applicationKey = ApplicationKey.from( module );

        assertEquals( ApplicationKey.from( "mymodule" ).getName(), applicationKey.getName() );
    }
}