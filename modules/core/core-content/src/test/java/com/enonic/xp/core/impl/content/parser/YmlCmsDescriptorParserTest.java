package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YmlCmsDescriptorParserTest
{
    @Test
    void test()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/cms-descriptor.yml" );

        final ApplicationKey currentApplication = ApplicationKey.from( "myapp" );

        final CmsDescriptor siteDescriptor = YmlCmsDescriptorParser.parse( yaml, currentApplication ).build();

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
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlSiteDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
