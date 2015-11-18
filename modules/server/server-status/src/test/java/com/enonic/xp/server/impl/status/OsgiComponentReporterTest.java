package com.enonic.xp.server.impl.status;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class OsgiComponentReporterTest
    extends BaseOsgiReporterTest
{
    private OsgiComponentReporter reporter;

    @Override
    protected void initialize()
        throws Exception
    {
        final List<ComponentDescriptionDTO> components = Lists.newArrayList();
        components.add( newComponent( "comp1" ) );
        components.add( newComponent( "comp2" ) );

        final ServiceComponentRuntime runtime = Mockito.mock( ServiceComponentRuntime.class );
        Mockito.when( runtime.getComponentDescriptionDTOs() ).thenReturn( components );
        Mockito.when( runtime.isComponentEnabled( Mockito.any() ) ).thenReturn( true );

        this.reporter = new OsgiComponentReporter();
        this.reporter.setRuntime( runtime );
    }

    private ComponentDescriptionDTO newComponent( final String name )
    {
        final ComponentDescriptionDTO result = new ComponentDescriptionDTO();
        result.name = name;
        result.bundle = new BundleDTO();
        result.bundle.symbolicName = "foo.bar";
        return result;
    }

    @Test
    public void testName()
    {
        assertEquals( "osgi.component", this.reporter.getName() );
    }

    @Test
    public void testReport()
    {
        final JsonNode json = this.reporter.getReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
