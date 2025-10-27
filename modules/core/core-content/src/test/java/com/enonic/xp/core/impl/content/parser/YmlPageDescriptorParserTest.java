package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlPageDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/page-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final PageDescriptor.Builder builder = YmlPageDescriptorParser.parse( yaml, currentApplication );

        builder.key( DescriptorKey.from( currentApplication, "landing-page" ) );

        final PageDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );

        assertEquals( "Landing page", descriptor.getDisplayName() );
        assertEquals( "Description of the Landing page", descriptor.getDescription() );
        assertEquals( "page.description.landingPage", descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getConfig();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "width" ) ) );

        // verify regions
        final RegionDescriptors regions = descriptor.getRegions();
        assertEquals( 1, regions.numberOfRegions() );
        assertEquals( "main", regions.iterator().next().getName() );

        // verify config
        final GenericValue schemaConfig = descriptor.getSchemaConfig();

        assertTrue( schemaConfig.optional( "p1" ).isPresent() );
        assertEquals( "v1", schemaConfig.optional( "p1" ).get().asString() );

        assertTrue( schemaConfig.optional( "p2" ).isPresent() );
        assertEquals( "v2", schemaConfig.optional( "p2" ).get().asString() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlPageDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
