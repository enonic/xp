package com.enonic.xp.app;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;

import static org.junit.Assert.*;

public class ApplicationTest
{

    @Test
    public void testCreate()
    {
        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getDisplayName() ).thenReturn( "My App" );
        Mockito.when( module.getKey() ).thenReturn( ModuleKey.from( "myapplication" ) );

        final Application application = new Application( module );

        assertEquals( ApplicationKey.from( "myapplication" ), application.getKey() );
        assertEquals( module, application.getModule() );
        assertEquals( "My App", application.getModule().getDisplayName() );
    }

    @Test
    public void testToString()
    {
        final Module module = Mockito.mock( Module.class );
        Mockito.when( module.getDisplayName() ).thenReturn( "My App" );
        Mockito.when( module.getKey() ).thenReturn( ModuleKey.from( "myapplication" ) );

        final Application application = new Application( module );

        assertNotNull( application.toString() );
    }

}