package com.enonic.xp.core.impl.content.serializer;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.schema.xdata.MixinName;

import static com.enonic.xp.content.ContentPropertyNames.MIXIN_DATA;

final class MixinDataSerializer
    extends AbstractDataSetSerializer<Mixins>
{
    @Override
    public void toData( final Mixins mixins, final PropertySet parent )
    {
        final PropertySet metaSet = parent.addSet( MIXIN_DATA );
        for ( final Mixin mixin : mixins )
        {

            final String mixinApplicationPrefix = mixin.getApplicationPrefix();
            PropertySet application = metaSet.getSet( mixinApplicationPrefix );
            if ( application == null )
            {
                application = metaSet.addSet( mixinApplicationPrefix );
            }
            application.addSet( mixin.getName().getLocalName(), mixin.getData().getRoot().copy( metaSet.getTree() ) );
        }
    }

    @Override
    public Mixins fromData( final PropertySet metadataSet )
    {
        if ( metadataSet != null )
        {
            final Mixins.Builder builder = Mixins.create();
            for ( final String metadataApplicationPrefix : metadataSet.getPropertyNames() )
            {
                final PropertySet application = metadataSet.getSet( metadataApplicationPrefix );
                for ( final String metadataLocalName : application.getPropertyNames() )
                {

                    final ApplicationKey applicationKey = Mixin.fromApplicationPrefix( metadataApplicationPrefix );

                    final MixinName metadataName = MixinName.from( applicationKey, metadataLocalName );
                    builder.add( new Mixin( metadataName, application.getSet( metadataLocalName ).toTree() ) );
                }
            }

            return builder.build();
        }
        return null;
    }

}
