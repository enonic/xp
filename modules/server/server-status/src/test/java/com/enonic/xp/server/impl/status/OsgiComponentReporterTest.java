package com.enonic.xp.server.impl.status;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Constants;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class OsgiComponentReporterTest
    extends BaseOsgiReporterTest
{
    private OsgiComponentReporter reporter;

    @Override
    protected void initialize()
        throws Exception
    {
        final ComponentDescriptionDTO comp1 = newComponent( "comp1" );
        final ComponentDescriptionDTO comp2 = newComponent( "comp2" );

        final ServiceComponentRuntime runtime = Mockito.mock( ServiceComponentRuntime.class );
        Mockito.when( runtime.getComponentDescriptionDTOs() ).thenReturn( Lists.newArrayList( comp1, comp2 ) );

        final ComponentConfigurationDTO config1 = newConfig( 1, null );
        final ComponentConfigurationDTO config2 = newConfig( 2, "mypid" );

        Mockito.when( runtime.getComponentConfigurationDTOs( comp1 ) ).thenReturn( Lists.newArrayList( config1 ) );
        Mockito.when( runtime.getComponentConfigurationDTOs( comp2 ) ).thenReturn( Lists.newArrayList( config2 ) );

        this.reporter = new OsgiComponentReporter();
        this.reporter.setRuntime( runtime );
    }

    private ComponentDescriptionDTO newComponent( final String name )
    {
        final ComponentDescriptionDTO result = new ComponentDescriptionDTO();
        result.name = name;
        result.bundle = new BundleDTO();
        result.bundle.id = 0;
        return result;
    }

    private ComponentConfigurationDTO newConfig( final int id, final String pid )
    {
        final ComponentConfigurationDTO result = new ComponentConfigurationDTO();
        result.id = id;
        result.state = ComponentConfigurationDTO.ACTIVE;
        result.properties = Maps.newHashMap();
        result.properties.put( Constants.SERVICE_PID, pid );
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
