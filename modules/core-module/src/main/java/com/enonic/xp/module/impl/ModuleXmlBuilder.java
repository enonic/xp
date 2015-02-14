package com.enonic.xp.module.impl;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.xml.mapper.XmlFormMapper;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlMetadata;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlVendor;
import com.enonic.wem.api.xml.serializer.XmlSerializers;

final class ModuleXmlBuilder
{
    private final static String SEPARATOR = ":";

    public void toModule( final String xml, final ModuleBuilder builder, final ModuleKey moduleKey )
    {
        final XmlModule object = XmlSerializers.module().parse( xml );
        toModule( object, builder, moduleKey );
    }

    private void toModule( final XmlModule xml, final ModuleBuilder builder, final ModuleKey moduleKey )
    {
        builder.displayName( xml.getDisplayName() );
        builder.url( xml.getUrl() );

        final XmlVendor vendor = xml.getVendor();
        if ( vendor != null )
        {
            builder.vendorUrl( vendor.getUrl() );
            builder.vendorName( vendor.getName() );
        }

        if ( xml.getXData() != null )
        {
            final ImmutableList.Builder<MixinName> metaStepMixinNames = ImmutableList.builder();
            for ( XmlMetadata xmlMetaStep : xml.getXData() )
            {
                final String mixinName = xmlMetaStep.getMixin();
                final MixinName metadataSchemaName =
                    mixinName.contains( SEPARATOR ) ? MixinName.from( mixinName ) : MixinName.from( moduleKey, mixinName );
                metaStepMixinNames.add( metadataSchemaName );
            }
            builder.metaSteps( MixinNames.from( metaStepMixinNames.build() ) );
        }

        final XmlForm config = xml.getConfig();
        if ( config != null )
        {
            builder.config( new XmlFormMapper( moduleKey ).fromXml( config ) );
        }
    }
}
