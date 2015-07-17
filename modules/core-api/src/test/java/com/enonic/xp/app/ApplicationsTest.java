package com.enonic.xp.app;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.module.Module;

import static org.junit.Assert.*;

public class ApplicationsTest
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

        ApplicationsTest.list.add( module1 );
        ApplicationsTest.list.add( module2 );
        ApplicationsTest.list.add( module3 );
    }

    @Test
    public void fromEmpty()
    {
        Applications applications = Applications.empty();
        assertEquals( 0, applications.getSize() );
    }

    @Test
    public void fromIterable()
    {
        final Applications applications = Applications.from( (Iterable<Module>) ApplicationsTest.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromArrayList()
    {
        Applications applications = Applications.from( ApplicationsTest.list.get( 0 ), ApplicationsTest.list.get( 1 ),
                                                       ApplicationsTest.list.get( 2 ) );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getModule( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getModule( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void getApplicationKeys()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( applicationKeys, applications.getApplicationKeys() );
    }

}
