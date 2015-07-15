package com.enonic.xp.module;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ApplicationKeysTest
{
    private static ArrayList<ApplicationKey> list = new ArrayList();

    @BeforeClass
    public static void initModuleKeys()
    {
        ApplicationKeysTest.list.add( ApplicationKey.from( "aaa" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "bbb" ) );
        ApplicationKeysTest.list.add( ApplicationKey.from( "ccc" ) );
    }

    @Test
    public void empty()
    {
        ModuleKeys moduleKeys = ModuleKeys.empty();

        assertEquals( 0, moduleKeys.getSize() );
    }

    @Test
    public void fromArray()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( ApplicationKeysTest.list.get( 0 ), ApplicationKeysTest.list.get( 1 ), ApplicationKeysTest.list.get( 2 ) );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromIterable()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( (Iterable) ApplicationKeysTest.list );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromCollection()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( ApplicationKeysTest.list );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromStringArray()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ApplicationKeysTest.list.get( 2 ) ) );
    }
}
