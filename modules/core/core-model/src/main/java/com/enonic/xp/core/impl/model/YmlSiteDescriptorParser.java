package com.enonic.xp.core.impl.model;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;

final class YmlSiteDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( SiteDescriptor.Builder.class, SiteDescriptorBuilderMixIn.class );

        PARSER.addMixIn( XDataMapping.class, SiteDescriptorBuilderMixIn.XDataMappingMixIn.class );
        PARSER.addMixIn( XDataMapping.Builder.class, SiteDescriptorBuilderMixIn.XDataMappingMixIn.Builder.class );

        PARSER.addMixIn( ResponseProcessorDescriptor.class, SiteDescriptorBuilderMixIn.ResponseProcessorDescriptorMixIn.class );
        PARSER.addMixIn( ResponseProcessorDescriptor.Builder.class,
                         SiteDescriptorBuilderMixIn.ResponseProcessorDescriptorMixIn.Builder.class );

        PARSER.addMixIn( ControllerMappingDescriptor.class, SiteDescriptorBuilderMixIn.ControllerMappingDescriptorMixIn.class );
        PARSER.addMixIn( ControllerMappingDescriptor.Builder.class,
                         SiteDescriptorBuilderMixIn.ControllerMappingDescriptorMixIn.Builder.class );
    }

    static SiteDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, SiteDescriptor.Builder.class, currentApplication );
    }
}
