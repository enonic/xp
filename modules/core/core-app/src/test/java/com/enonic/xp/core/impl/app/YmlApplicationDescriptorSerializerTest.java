package com.enonic.xp.core.impl.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YmlApplicationDescriptorSerializerTest
{
    private static final ApplicationKey MYAPP = ApplicationKey.from( "myapp" );

    @Test
    void minimal_descriptor()
    {
        final String yaml = YmlApplicationDescriptorSerializer.serialize( ApplicationDescriptor.create().key( MYAPP ).build() );

        assertEquals( "kind: \"Application\"\nname: \"myapp\"\n", yaml );
    }

    @Test
    void round_trip_plain_fields()
    {
        final ApplicationDescriptor descriptor = ApplicationDescriptor.create()
            .key( MYAPP )
            .title( "My App" )
            .description( "Brief description" )
            .vendorName( "Enonic" )
            .vendorUrl( "https://enonic.com" )
            .url( "https://github.com/enonic/myapp" )
            .schemaConfig( GenericValue.newObject()
                               .put( "property_1", "value_1" )
                               .put( "number", 42 )
                               .put( "flag", true )
                               .put( "nested", GenericValue.newObject().put( "inner", "x" ).build() )
                               .build() )
            .icon( Icon.from( new byte[]{1, 2, 3}, "image/png", Instant.now() ) )
            .build();

        final String yaml = YmlApplicationDescriptorSerializer.serialize( descriptor );
        final ApplicationDescriptor parsed = YmlApplicationDescriptorParser.parse( yaml, MYAPP ).build();

        assertEquals( "My App", parsed.getTitle() );
        assertNull( parsed.getTitleI18nKey() );
        assertEquals( "Brief description", parsed.getDescription() );
        assertNull( parsed.getDescriptionI18nKey() );
        assertEquals( "Enonic", parsed.getVendorName() );
        assertEquals( "https://enonic.com", parsed.getVendorUrl() );
        assertEquals( "https://github.com/enonic/myapp", parsed.getUrl() );
        assertEquals( descriptor.getSchemaConfig(), parsed.getSchemaConfig() );
        assertNull( parsed.getIcon() );
        assertFalse( yaml.contains( "icon" ), yaml );
    }

    @Test
    void round_trip_localized_fields()
    {
        final ApplicationDescriptor descriptor = ApplicationDescriptor.create()
            .key( MYAPP )
            .title( new LocalizedText( "My App", "app.title" ) )
            .description( new LocalizedText( "", "app.description" ) )
            .build();

        final String yaml = YmlApplicationDescriptorSerializer.serialize( descriptor );
        final ApplicationDescriptor parsed = YmlApplicationDescriptorParser.parse( yaml, MYAPP ).build();

        assertTrue( yaml.contains( "i18n: \"app.title\"" ), yaml );
        assertEquals( "My App", parsed.getTitle() );
        assertEquals( "app.title", parsed.getTitleI18nKey() );
        assertEquals( "", parsed.getDescription() );
        assertEquals( "app.description", parsed.getDescriptionI18nKey() );
    }

    @Test
    void empty_fields_are_omitted()
    {
        final String yaml = YmlApplicationDescriptorSerializer.serialize(
            ApplicationDescriptor.create().key( MYAPP ).description( "" ).vendorName( "" ).build() );

        assertFalse( yaml.contains( "description" ), yaml );
        assertFalse( yaml.contains( "vendorName" ), yaml );
        assertFalse( yaml.contains( "config" ), yaml );
    }
}