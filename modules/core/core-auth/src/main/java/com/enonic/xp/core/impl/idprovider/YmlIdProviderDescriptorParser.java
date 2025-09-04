package com.enonic.xp.core.impl.idprovider;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

class YmlIdProviderDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( IdProviderDescriptorMode.class, IdProviderDescriptorModeMapper.class );
        PARSER.addMixIn( IdProviderDescriptor.Builder.class, IdProviderDescriptorBuilderMapper.class );
    }

    public static IdProviderDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, IdProviderDescriptor.Builder.class, currentApplication );
    }
}
