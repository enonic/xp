package com.enonic.xp.core.impl.model;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlSiteDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/site-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final SiteDescriptor.Builder builder = YmlSiteDescriptorParser.parse( yaml, currentApplication );

        builder.applicationKey( currentApplication );

        final SiteDescriptor siteDescriptor = builder.build();

        assertEquals( currentApplication, siteDescriptor.getApplicationKey() );

        // verify form
        final Form form = siteDescriptor.getForm();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "backgroundColor" ) ) );

        // verify xData
        final XDataMappings xDataMappings = siteDescriptor.getXDataMappings();
        final Iterator<XDataMapping> xDataMappingIterator = xDataMappings.iterator();

        final XDataMapping xDataMapping_1 = xDataMappingIterator.next();
        assertEquals( XDataName.from( currentApplication, "all-except-folders" ), xDataMapping_1.getXDataName() );
        assertEquals( "^(?!base:folder$).*", xDataMapping_1.getAllowContentTypes() );
        assertTrue( xDataMapping_1.getOptional() );

        final XDataMapping xDataMapping_2 = xDataMappingIterator.next();
        assertEquals( XDataName.from( currentApplication, "folders-only" ), xDataMapping_2.getXDataName() );
        assertEquals( "base:folder", xDataMapping_2.getAllowContentTypes() );
        assertFalse( xDataMapping_2.getOptional() );

        // verify response processors
        final ResponseProcessorDescriptors responseProcessors = siteDescriptor.getResponseProcessors();
        final Iterator<ResponseProcessorDescriptor> processorsIterator = responseProcessors.iterator();

        final ResponseProcessorDescriptor processor_1 = processorsIterator.next();
        assertEquals( "background-color-filter", processor_1.getName() );
        assertEquals( 10, processor_1.getOrder() );
        assertEquals( currentApplication, processor_1.getApplication() );

        final ResponseProcessorDescriptor processor_2 = processorsIterator.next();
        assertEquals( "branch-filter", processor_2.getName() );
        assertEquals( 10, processor_2.getOrder() );
        assertEquals( currentApplication, processor_2.getApplication() );

        final ResponseProcessorDescriptor processor_3 = processorsIterator.next();
        assertEquals( "app-header-filter", processor_3.getName() );
        assertEquals( 11, processor_3.getOrder() );
        assertEquals( currentApplication, processor_3.getApplication() );

        // verify mappings
        final ControllerMappingDescriptors mappingDescriptors = siteDescriptor.getMappingDescriptors();
        final Iterator<ControllerMappingDescriptor> mappingsIterator = mappingDescriptors.iterator();

        final ControllerMappingDescriptor mapping_1 = mappingsIterator.next();
        assertEquals( ResourceKey.from( currentApplication, "/site/services/image.js" ), mapping_1.getFilter() );
        assertNull( mapping_1.getController() );
        assertEquals( "image", mapping_1.getService() );
        assertEquals( 10, mapping_1.getOrder() );

        final ControllerMappingDescriptor mapping_2 = mappingsIterator.next();
        assertEquals( ResourceKey.from( currentApplication, "/site/mappings/my-controller.js" ), mapping_2.getController() );
        assertNull( mapping_2.getService() );
        assertNull( mapping_2.getFilter() );
        assertEquals( 10, mapping_2.getOrder() );
        assertEquals( "/my-controller", mapping_2.getPattern().pattern() );
        assertFalse( mapping_2.invertPattern() );

        final ControllerMappingDescriptor mapping_3 = mappingsIterator.next();
        assertEquals( ResourceKey.from( currentApplication, "/site/mappings/my-filter.js" ), mapping_3.getFilter() );
        assertNull( mapping_3.getController() );
        assertNull( mapping_3.getService() );
        assertEquals( 10, mapping_3.getOrder() );
        assertEquals( "/my-filter", mapping_3.getPattern().pattern() );
        assertTrue( mapping_3.invertPattern() );
        assertEquals( "type:'portal:fragment'", mapping_3.getContentConstraint().toString() );

        // verify mounted APIs
        final DescriptorKeys mountedApis = siteDescriptor.getApiMounts();
        final Iterator<DescriptorKey> mountedApiIterator = mountedApis.iterator();
        assertEquals( DescriptorKey.from( "admin:widget" ), mountedApiIterator.next() );
        assertEquals( DescriptorKey.from( currentApplication, "content" ), mountedApiIterator.next() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlSiteDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
