package com.enonic.xp.core.impl.content.page.region;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.page.AbstractDescriptorServiceTest;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.PartComponent;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComponentServiceTest
    extends AbstractDescriptorServiceTest
{
    protected ComponentServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.service = new ComponentServiceImpl();
        this.service.setResourceService( this.resourceService );

        final LayoutDescriptorServiceImpl layoutDescriptorService = new LayoutDescriptorServiceImpl( this.descriptorService );

        final PartDescriptorServiceImpl partDescriptorService = new PartDescriptorServiceImpl( this.descriptorService );

        this.service.setLayoutDescriptorService( layoutDescriptorService );
        this.service.setPartDescriptorService( partDescriptorService );
    }

    @Test
    public void testGetPartByKey()
        throws Exception
    {
        final Component part1App1 = this.service.getByKey( DescriptorKey.from( "myapp1:mypart" ) );
        final Component part1App2 = this.service.getByKey( DescriptorKey.from( "myapp2:mypart" ) );

        assertNotNull( part1App1 );
        assertNotNull( part1App2 );

        assertTrue( part1App1 instanceof PartComponent );
        assertTrue( part1App2 instanceof PartComponent );
    }

    @Test
    public void testGetLayoutByKey()
        throws Exception
    {
        final Component layout1App1 = this.service.getByKey( DescriptorKey.from( "myapp1:mylayout" ) );
        final Component layout1App2 = this.service.getByKey( DescriptorKey.from( "myapp2:mylayout" ) );

        assertNotNull( layout1App1 );
        assertNotNull( layout1App2 );

        assertTrue( layout1App1 instanceof LayoutComponent );
        assertTrue( layout1App2 instanceof LayoutComponent );
    }

    @Test
    public void testGetByKeyMissingComponent()
        throws Exception
    {
        final Component missingComponent = this.service.getByKey( DescriptorKey.from( "myapp1:missingComponent" ) );
        assertNull( missingComponent );
    }

    @Test
    public void testGetByKeyMissingApp()
        throws Exception
    {
        final Component missingComponent = this.service.getByKey( DescriptorKey.from( "missingApp:missingComponent" ) );
        assertNull( missingComponent );
    }


}
