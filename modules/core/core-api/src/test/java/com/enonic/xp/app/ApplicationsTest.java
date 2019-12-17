package com.enonic.xp.app;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplicationsTest
{
    private static ArrayList<Application> list = new ArrayList();

    @BeforeAll
    public static void initList()
    {
        final Application application1 = Mockito.mock( Application.class );
        final Application application2 = Mockito.mock( Application.class );
        final Application application3 = Mockito.mock( Application.class );

        Mockito.when( application1.getDisplayName() ).thenReturn( "aaa" );

        Mockito.when( application1.getKey() ).thenReturn( ApplicationKey.from( "aaa" ) );
        Mockito.when( application2.getKey() ).thenReturn( ApplicationKey.from( "bbb" ) );
        Mockito.when( application3.getKey() ).thenReturn( ApplicationKey.from( "ccc" ) );

        ApplicationsTest.list.add( application1 );
        ApplicationsTest.list.add( application2 );
        ApplicationsTest.list.add( application3 );
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
        final Applications applications = Applications.from( (Iterable<Application>) ApplicationsTest.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getApplication( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getApplication( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void fromArrayList()
    {
        Applications applications =
            Applications.from( ApplicationsTest.list.get( 0 ), ApplicationsTest.list.get( 1 ), ApplicationsTest.list.get( 2 ) );

        assertEquals( 3, applications.getSize() );
        assertEquals( "aaa", applications.first().getDisplayName() );
        assertNotNull( applications.getApplication( ApplicationKey.from( "aaa" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "bbb" ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( "ccc" ) ) );
    }

    @Test
    public void getApplicationKeys()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( applicationKeys, applications.getApplicationKeys() );
    }

}
