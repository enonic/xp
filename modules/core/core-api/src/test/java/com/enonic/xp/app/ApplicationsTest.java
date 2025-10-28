package com.enonic.xp.app;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationsTest
{
    private static final ArrayList<Application> list = new ArrayList();

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
    void fromEmpty()
    {
        Applications applications = Applications.empty();
        assertEquals( 0, applications.getSize() );
    }

    @Test
    void fromIterable()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        assertEquals( "aaa", applications.first().getDisplayName() );
        assertThat( applications ).containsExactly( list.toArray( ApplicationsTest.list.toArray( Application[]::new ) ) );
    }


    @Test
    void getApplicationKeys()
    {
        final Applications applications = Applications.from( ApplicationsTest.list );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( "aaa", "bbb", "ccc" );

        assertEquals( applicationKeys, applications.getApplicationKeys() );
    }

}
