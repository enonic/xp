package com.enonic.wem.core.module;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNames;
import com.enonic.wem.api.xml.mapper.XmlFormMapper;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlVendor;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;

final class ModuleXmlBuilder
{
    public void toModule( final String xml, final ModuleBuilder builder, final ModuleKey moduleKey )
    {
        final XmlModule object = XmlSerializers2.module().parse( xml );
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

        if ( xml.getMetadataSchemas() != null )
        {
            MetadataSchemaNames metadataSchemaNames = MetadataSchemaNames.empty();
            for ( XmlMetadataSchema xmlMetadataSchema : xml.getMetadataSchemas().getMetadataSchema() )
            {
                MetadataSchemaName metadataSchemaName = MetadataSchemaName.from( moduleKey, xmlMetadataSchema.getName() );
                metadataSchemaNames = metadataSchemaNames.add( metadataSchemaName );
            }
            builder.metadataSchemaNames( metadataSchemaNames );
        }

        final XmlForm config = xml.getConfig();
        if ( config != null )
        {
            builder.config( XmlFormMapper.fromXml( config ) );
        }
    }
}
