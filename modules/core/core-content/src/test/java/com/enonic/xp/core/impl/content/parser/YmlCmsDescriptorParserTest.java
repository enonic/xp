package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

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

        // verify mixin mappings
        final MixinMappings mixinMappings = siteDescriptor.getMixinMappings();
        final Iterator<MixinMapping> mixinMappingIterator = mixinMappings.iterator();

        final MixinMapping mixinMapping_1 = mixinMappingIterator.next();
        assertEquals( MixinName.from( currentApplication, "all-except-folders" ), mixinMapping_1.getMixinName() );
        assertEquals( "^(?!base:folder$).*", mixinMapping_1.getAllowContentTypes() );
        assertTrue( mixinMapping_1.getOptional() );

        final MixinMapping mixinMapping_2 = mixinMappingIterator.next();
        assertEquals( MixinName.from( currentApplication, "folders-only" ), mixinMapping_2.getMixinName() );
        assertEquals( "base:folder", mixinMapping_2.getAllowContentTypes() );
        assertFalse( mixinMapping_2.getOptional() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlSiteDescriptorParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
