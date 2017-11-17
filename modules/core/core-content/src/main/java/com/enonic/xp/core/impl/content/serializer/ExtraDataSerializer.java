package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.schema.mixin.MixinName;

import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;

public final class ExtraDataSerializer
    extends AbstractDataSetSerializer<ExtraDatas, ExtraDatas>
{
    private static final String CONTROLLER = "controller";

    private static final String TEMPLATE = "template";

    private static final String CONFIG = "config";

    private static final String REGION = "region";

    private static final String CUSTOMIZED = "customized";

    private static final String FRAGMENT = "fragment";

    private final RegionDataSerializer regionDataSerializer = new RegionDataSerializer();

    private final ComponentDataSerializerProvider componentDataSerializerProvider = new ComponentDataSerializerProvider();

    @Override
    public void toData( final ExtraDatas extraDatas, final PropertySet parent )
    {
        final PropertySet metaSet = parent.addSet( EXTRA_DATA );
        for ( final ExtraData extraData : extraDatas )
        {

            final String xDataApplicationPrefix = extraData.getApplicationPrefix();
            PropertySet xDataApplication = metaSet.getSet( xDataApplicationPrefix );
            if ( xDataApplication == null )
            {
                xDataApplication = metaSet.addSet( xDataApplicationPrefix );
            }
            xDataApplication.addSet( extraData.getName().getLocalName(), extraData.getData().getRoot().copy( metaSet.getTree() ) );
        }
    }

    @Override
    public ExtraDatas fromData( final PropertySet metadataSet )
    {
        if ( metadataSet != null )
        {
            final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
            for ( final String metadataApplicationPrefix : metadataSet.getPropertyNames() )
            {
                final PropertySet xDataApplication = metadataSet.getSet( metadataApplicationPrefix );
                for ( final String metadataLocalName : xDataApplication.getPropertyNames() )
                {

                    final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( metadataApplicationPrefix );

                    final MixinName metadataName = MixinName.from( applicationKey, metadataLocalName );
                    extradatasBuilder.add( new ExtraData( metadataName, xDataApplication.getSet( metadataLocalName ).toTree() ) );
                }
            }

            return extradatasBuilder.build();
        }
        return null;
    }

}
