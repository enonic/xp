package com.enonic.xp.module;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleKeysTest
{
    private static ArrayList<ModuleKey> list = new ArrayList();

    @BeforeClass
    public static void initModuleKeys()
    {
        ModuleKeysTest.list.add( ModuleKey.from( "aaa" ) );
        ModuleKeysTest.list.add( ModuleKey.from( "bbb" ) );
        ModuleKeysTest.list.add( ModuleKey.from( "ccc" ) );
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
        ModuleKeys moduleKeys = ModuleKeys.from( ModuleKeysTest.list.get( 0 ), ModuleKeysTest.list.get( 1 ), ModuleKeysTest.list.get( 2 ) );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromIterable()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( (Iterable) ModuleKeysTest.list );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromCollection()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( ModuleKeysTest.list );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 2 ) ) );
    }

    @Test
    public void fromStringArray()
    {
        ModuleKeys moduleKeys = ModuleKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( 3, moduleKeys.getSize() );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 0 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 1 ) ) );
        assertTrue( moduleKeys.contains( ModuleKeysTest.list.get( 2 ) ) );
    }
}
