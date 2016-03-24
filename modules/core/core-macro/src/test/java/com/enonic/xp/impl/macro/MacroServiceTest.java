package com.enonic.xp.impl.macro;

import static org.junit.Assert.*;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

public class MacroServiceTest
    extends ApplicationTestSupport
{

    private MacroServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        this.service = new MacroServiceImpl();
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.from( "myapp1" ), "macros1" );
        final MacroDescriptor descriptor = this.service.getByKey( macroKey );
        assertNotNull( descriptor );
        assertTrue( descriptor.getKey().equals( macroKey ) );
    }

    @Test
    public void testGetByApplication()
        throws Exception
    {
        final MacroDescriptors result = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );

        assertNotNull( result );
        assertEquals( 1, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final MacroDescriptors result = this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2" ) );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );
    }
}
