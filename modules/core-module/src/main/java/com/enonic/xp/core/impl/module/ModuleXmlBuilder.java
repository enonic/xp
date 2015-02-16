package com.enonic.xp.core.impl.module;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.xml.mapper.XmlFormMapper;
import com.enonic.xp.xml.model.XmlForm;
import com.enonic.xp.xml.model.XmlMetadata;
import com.enonic.xp.xml.model.XmlModule;
import com.enonic.xp.xml.serializer.XmlSerializers;

final class ModuleXmlBuilder
{
    private final static String SEPARATOR = ":";

    public void toModule( final String xml, final ModuleImpl module )
    {
        final XmlModule object = XmlSerializers.module().parse( xml );
        toModule( object, module );
    }

    private void toModule( final XmlModule xml, final ModuleImpl module )
    {
        if ( xml.getXData() != null )
        {
            final ImmutableList.Builder<MixinName> metaStepMixinNames = ImmutableList.builder();
            for ( XmlMetadata xmlMetaStep : xml.getXData() )
            {
                final String mixinName = xmlMetaStep.getMixin();
                final MixinName metadataSchemaName =
                    mixinName.contains( SEPARATOR ) ? MixinName.from( mixinName ) : MixinName.from( module.moduleKey, mixinName );
                metaStepMixinNames.add( metadataSchemaName );
            }
            module.metaSteps = MixinNames.from( metaStepMixinNames.build() );
        }

        final XmlForm config = xml.getConfig();
        if ( config != null )
        {
            module.config = new XmlFormMapper( module.moduleKey ).fromXml( config );
        }
    }
}
