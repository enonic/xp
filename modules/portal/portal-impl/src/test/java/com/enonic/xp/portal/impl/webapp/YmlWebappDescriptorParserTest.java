package com.enonic.xp.portal.impl.webapp;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.portal.impl.api.YmlApiDescriptorParserTest;
import com.enonic.xp.webapp.WebappDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YmlWebappDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/webapp-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final WebappDescriptor.Builder builder = YmlWebappDescriptorParser.parse( yaml, currentApplication );
        builder.applicationKey( currentApplication );

        final WebappDescriptor descriptor = builder.build();

        assertEquals( currentApplication, descriptor.getApplicationKey() );

        final DescriptorKeys descriptorKeys = descriptor.getApiMounts();
        final Iterator<DescriptorKey> iterator = descriptorKeys.iterator();

        assertEquals( DescriptorKey.from( "admin:widget" ), iterator.next() );
        assertEquals( DescriptorKey.from( currentApplication, "content" ), iterator.next() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlApiDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
