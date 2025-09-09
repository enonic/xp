package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        final Set<InputTypeProperty> p1Set = schemaConfig.getProperties( "p1" );
        final Iterator<InputTypeProperty> p1Iterator = p1Set.iterator();

        InputTypeProperty p1Property = p1Iterator.next();
        assertEquals( "p1", p1Property.getName() );
        assertEquals( "p1v1", p1Property.getValue() );
        assertEquals( 1, p1Property.getAttributes().size() );
        assertEquals( "attr1", p1Property.getAttribute( "attr1" ) );

        p1Property = p1Iterator.next();
        assertEquals( "p1", p1Property.getName() );
        assertEquals( "p1v2", p1Property.getValue() );
        assertEquals( 0, p1Property.getAttributes().size() );


        final Set<InputTypeProperty> p2Set = schemaConfig.getProperties( "p2" );
        final Iterator<InputTypeProperty> p2Iterator = p2Set.iterator();

        InputTypeProperty p2Property = p2Iterator.next();
        assertEquals( "p2", p2Property.getName() );
        assertEquals( "p2v1", p2Property.getValue() );
        assertEquals( 0, p2Property.getAttributes().size() );

        p2Property = p2Iterator.next();
        assertEquals( "p2", p2Property.getName() );
        assertEquals( "p2v2", p2Property.getValue() );
        assertEquals( 0, p2Property.getAttributes().size() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlLayoutDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
