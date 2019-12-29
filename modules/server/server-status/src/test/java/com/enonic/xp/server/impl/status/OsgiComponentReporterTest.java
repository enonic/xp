package com.enonic.xp.server.impl.status;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Constants;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

import com.fasterxml.jackson.databind.JsonNode;

public class OsgiComponentReporterTest
    extends BaseOsgiReporterTest<OsgiComponentReporter>
{
    public OsgiComponentReporterTest()
    {
        super( "osgi.component" );
    }

    @Override
    protected OsgiComponentReporter newReporter()
        throws Exception
    {
        final ComponentDescriptionDTO comp1 = newComponent( "comp1" );
        final ComponentDescriptionDTO comp2 = newComponent( "comp2" );

        final ServiceComponentRuntime runtime = Mockito.mock( ServiceComponentRuntime.class );
        Mockito.when( runtime.getComponentDescriptionDTOs() ).thenReturn( List.of( comp1, comp2 ) );

        final ComponentConfigurationDTO config1 = newConfig( 1, null );
        final ComponentConfigurationDTO config2 = newConfig( 2, "mypid" );

        Mockito.when( runtime.getComponentConfigurationDTOs( comp1 ) ).thenReturn( List.of( config1 ) );
        Mockito.when( runtime.getComponentConfigurationDTOs( comp2 ) ).thenReturn( List.of( config2 ) );

        final OsgiComponentReporter reporter = new OsgiComponentReporter();
        reporter.setRuntime( runtime );
        return reporter;
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
        result.properties = new HashMap<>();
        result.properties.put( Constants.SERVICE_PID, pid );
        return result;
    }

    @Test
    public void testReport()
        throws Exception
    {
        final JsonNode json = jsonReport();
        final JsonNode expected = this.helper.loadTestJson( "result.json" );

        this.helper.assertJsonEquals( expected, json );
    }
}
