package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.app.ApplicationServiceImpl;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

import static org.junit.Assert.*;

public class MacroDescriptorServiceTest
    extends ApplicationTestSupport
{

    private MacroDescriptorServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        this.service = new MacroDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setApplicationService( this.applicationService );
    }

    @Test
    public void testGetByKey()
        throws Exception
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.from( "myapp1" ), "macro1" );
        final MacroDescriptor descriptor = this.service.getByKey( macroKey );
        assertNotNull( descriptor );
        assertTrue( descriptor.getKey().equals( macroKey ) );
    }

    @Test
    public void testGetBySystemKey()
        throws Exception
    {
        final MacroKey macroKey = MacroKey.from( ApplicationKey.SYSTEM, "youtube" );
        final MacroDescriptor descriptor = this.service.getByKey( macroKey );
        assertNotNull( descriptor );
        assertTrue( descriptor.getKey().equals( macroKey ) );
        assertEquals( "Youtube macro", descriptor.getDisplayName() );
        assertEquals( "Youtube macro", descriptor.getDescription() );
        assertNotNull( descriptor.getForm() );
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
    public void testGetBySystemApplication()
        throws Exception
    {
        final MacroDescriptors result = this.service.getByApplication( ApplicationKey.SYSTEM );
        assertNotNull( result );
        assertEquals( 4, result.getSize() );
    }

    @Test
    public void testGetByApplications()
        throws Exception
    {
        final MacroDescriptors result =
            this.service.getByApplications( ApplicationKeys.from( "myapp1", "myapp2", ApplicationKey.SYSTEM.getName() ) );

        assertNotNull( result );
        assertEquals( 6, result.getSize() );
    }

    @Test
    public void testGetAll()
        throws Exception
    {
        final MacroDescriptors result = this.service.getAll();
        assertEquals( 2, result.getSize() );
    }
}