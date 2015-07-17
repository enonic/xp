package com.enonic.xp.module;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

import static org.junit.Assert.*;

public class ModulesTest
{
    private static ArrayList<Module> list = new ArrayList();

    @BeforeClass
    public static void initList()
    {
        final Module module1 = Mockito.mock( Module.class );
        final Module module2 = Mockito.mock( Module.class );
        final Module module3 = Mockito.mock( Module.class );

        Mockito.when( module1.getDisplayName() ).thenReturn( "aaa" );

        Mockito.when( module1.getKey() ).thenReturn( ApplicationKey.from( "aaa" ) );
        Mockito.when( module2.getKey() ).thenReturn( ApplicationKey.from( "bbb" ) );
        Mockito.when( module3.getKey() ).thenReturn( ApplicationKey.from( "ccc" ) );

        ModulesTest.list.add( module1 );
        ModulesTest.list.add( module2 );
        ModulesTest.list.add( module3 );
    }

    @Test
    public void fromEmpty()
    {
        Modules modules = Modules.empty();
        assertEquals( 0, modules.getSize() );
    }

    @Test
    public void fromIterable()
    {
        final Modules modules = Modules.from( (Iterable<Module>) ModulesTest.list );

        assertEquals( 3, modules.getSize() );
        assertEquals( "aaa", modules.first().getDisplayName() );
        assertNotNull( modules.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Modules modules = Modules.from( ModulesTest.list );

        assertEquals( 3, modules.getSize() );
        assertEquals( "aaa", modules.first().getDisplayName() );
        assertNotNull( modules.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromArrayList()
    {
        Modules modules = Modules.from( ModulesTest.list.get( 0 ), ModulesTest.list.get( 1 ), ModulesTest.list.get( 2 ) );

        assertEquals( 3, modules.getSize() );
        assertEquals( "aaa", modules.first().getDisplayName() );
        assertNotNull( modules.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( modules.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void getApplicationKeys()
    {
        final Modules modules = Modules.from( ModulesTest.list );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( applicationKeys, modules.getApplicationKeys() );
    }

}
