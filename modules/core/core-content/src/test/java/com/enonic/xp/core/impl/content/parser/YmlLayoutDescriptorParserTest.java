package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlLayoutDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/layout-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final LayoutDescriptor.Builder builder = YmlLayoutDescriptorParser.parse( yaml, currentApplication );

        builder.key( DescriptorKey.from( currentApplication, "3-columns" ) );

        final LayoutDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );

        assertEquals( "3 columns", descriptor.getDisplayName() );
        assertEquals( "Layout with 3 columns", descriptor.getDescription() );
        assertEquals( "layout.description.3columns", descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getConfig();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "columnConfig" ) ) );

        // verify regions
        final RegionDescriptors regions = descriptor.getRegions();
        assertEquals( 3, regions.numberOfRegions() );

        final Iterator<RegionDescriptor> regionIterator = regions.iterator();
        assertEquals( "left", regionIterator.next().getName() );
        assertEquals( "middle", regionIterator.next().getName() );
        assertEquals( "right", regionIterator.next().getName() );

        // verify config
        final InputTypeConfig schemaConfig = descriptor.getSchemaConfig();

        assertTrue( schemaConfig.getProperty( "myString" ).isPresent() );
        assertEquals( "String value", schemaConfig.getProperty( "myString" ).get().getValue().asString() );

        assertTrue( schemaConfig.getProperty( "myInt" ).isPresent() );
        assertEquals( 42, schemaConfig.getProperty( "myInt" ).get().getValue().asInteger() );

        assertTrue( schemaConfig.getProperty( "myList" ).isPresent() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlLayoutDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
