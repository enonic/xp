package com.enonic.wem.core.content.page;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageDescriptorXmlTest;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.resource.Resource.newResource;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.isA;

public class GetPageDescriptorCommandTest
{
    private ModuleService moduleService;

    @Before
    public void setUp()
        throws Exception
    {
        moduleService = Mockito.mock( ModuleService.class );
    }

    @Test
    public void testGetPageDescriptor()
        throws Exception
    {
        final PageDescriptor descriptor = createDescriptor();

        // 1. Create temp dir.
        final Path tempDir = java.nio.file.Files.createTempDirectory( "tempo" );
        final File tempFile = tempDir.toFile();
        // 2. Create temp file.
        final File resourceFile = File.createTempFile( "pattern", ".suffix", tempFile );
        // 3. Delete temp file when program exits.
        resourceFile.deleteOnExit();
        // 4. Write into file.
        final BufferedWriter out = new BufferedWriter( new FileWriter( resourceFile ) );
        final String xml = readFromFile( "page-descriptor.xml" );

        out.write( xml );
        out.close();

        final Resource resource = newResource().
            name( resourceFile.getName() ).
            byteSource( Files.asByteSource( resourceFile ) ).
            size( resourceFile.length() ).
            build();

        Mockito.when( this.moduleService.getResource( isA( ModuleResourceKey.class ) ) ).thenReturn( resource );

        final PageDescriptor result = new GetPageDescriptorCommand().key( descriptor.getKey() ).moduleService( this.moduleService ).execute();

        assertEquals( "Landing page", result.getDisplayName() );
        assertEquals( "landing-page", result.getName().toString() );
    }

    private PageDescriptor createDescriptor()
        throws Exception
    {
        final ModuleKey module = ModuleKey.from( "mainmodule-1.0.0" );
        final PageDescriptorKey key = PageDescriptorKey.from( module, new ComponentDescriptorName( "landing-page" ) );

        final String xml = readFromFile( "page-descriptor.xml" );
        final PageDescriptor.Builder builder = PageDescriptor.newPageDescriptor();

        XmlSerializers.pageDescriptor().parse( xml ).to( builder );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }

    private String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = PageDescriptorXmlTest.class.getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }
        return Resources.toString( url, Charsets.UTF_8 );
    }
}
