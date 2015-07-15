package com.enonic.xp.app;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.module.Module;

import static org.junit.Assert.*;

public class ApplicationsTest
{
    private static final String APPLICATION1 = "app_1";

    private static final String APPLICATION2 = "app_2";

    private static final String APPLICATION3 = "app_3";

    private List<Application> list;

    private Application application1;

    private Application application2;

    private Application application3;

    @Before
    public void setup()
        throws Exception
    {
        final Module module1 = Mockito.mock( Module.class );
        Mockito.when( module1.getKey() ).thenReturn( ApplicationKey.from( APPLICATION1 ) );
        final Module module2 = Mockito.mock( Module.class );
        Mockito.when( module2.getKey() ).thenReturn( ApplicationKey.from( APPLICATION2 ) );
        final Module module3 = Mockito.mock( Module.class );
        Mockito.when( module3.getKey() ).thenReturn( ApplicationKey.from( APPLICATION3 ) );
        application1 = new Application( module1 );
        application2 = new Application( module2 );
        application3 = new Application( module3 );

        this.list = Arrays.asList( application1, application2, application3 );
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
        final Applications applications = Applications.from( (Iterable<Application>) this.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( application1, applications.first() );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION1 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION2 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION3 ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Applications applications = Applications.from( this.list );

        assertEquals( 3, applications.getSize() );
        assertEquals( application1, applications.first() );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION1 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION2 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION3 ) ) );
    }

    @Test
    public void fromArray()
    {
        Applications applications = Applications.from( this.list.get( 0 ), this.list.get( 1 ), this.list.get( 2 ) );

        assertEquals( 3, applications.getSize() );
        assertEquals( application1, applications.first() );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION1 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION2 ) ) );
        assertNotNull( applications.getApplication( ApplicationKey.from( APPLICATION3 ) ) );
    }

    @Test
    public void getApplicationKeys()
    {
        final Applications applications = Applications.from( this.list );

        final ApplicationKeys applicationKeys = ApplicationKeys.from( APPLICATION1, APPLICATION2, APPLICATION3 );

        assertEquals( applicationKeys, applications.getKeys() );
    }

    @Test
    public void filter()
    {
        final Applications applications = Applications.from( this.list );
        final Applications filteredApplications =
            applications.filter( application -> application2.getKey().equals( application.getKey() ) );

        assertEquals( 1, filteredApplications.getSize() );
        assertEquals( application2, filteredApplications.first() );
    }

}