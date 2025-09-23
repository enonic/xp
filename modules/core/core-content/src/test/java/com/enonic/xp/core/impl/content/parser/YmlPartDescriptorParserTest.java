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
import com.enonic.xp.region.PartDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class YmlPartDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/part-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final PartDescriptor.Builder builder = YmlPartDescriptorParser.parse( yaml, currentApplication );

        builder.key( DescriptorKey.from( currentApplication, "my-part" ) );

        final PartDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );

        assertEquals( "My Part", descriptor.getDisplayName() );
        assertEquals( "Description of My Part", descriptor.getDescription() );
        assertEquals( "part.description.myPart", descriptor.getDescriptionI18nKey() );

        // verify form
        final Form form = descriptor.getConfig();
        assertNotNull( form );
        assertNotNull( form.getFormItem( FormItemPath.from( "myField" ) ) );

//        // verify config
//        final InputTypeConfig schemaConfig = descriptor.getSchemaConfig();
//        final Set<InputTypeProperty> p1Set = schemaConfig.getProperties( "p1" );
//        final Iterator<InputTypeProperty> p1Iterator = p1Set.iterator();
//
//        InputTypeProperty p1Property = p1Iterator.next();
//        assertEquals( "p1", p1Property.getName() );
//        assertEquals( "p1v1", p1Property.getValue() );
//        assertEquals( 0, p1Property.getAttributes().size() );
//
//        final Set<InputTypeProperty> p2Set = schemaConfig.getProperties( "p2" );
//        final Iterator<InputTypeProperty> p2Iterator = p2Set.iterator();
//
//        InputTypeProperty p2Property = p2Iterator.next();
//        assertEquals( "p2", p2Property.getName() );
//        assertEquals( "p2v1", p2Property.getValue() );
//        assertEquals( 0, p2Property.getAttributes().size() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlPageDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
