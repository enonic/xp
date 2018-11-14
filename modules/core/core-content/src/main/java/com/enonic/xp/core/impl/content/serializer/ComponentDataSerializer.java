package com.enonic.xp.core.impl.content.serializer;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;

@Beta
abstract class ComponentDataSerializer<DATA extends Component>
    extends AbstractDataSetSerializer<DATA>
{
    public static final String COMPONENTS = "components";

    public static final String TYPE = "type";

    public static final String PATH = "path";

    @Override
    public void toData( final DATA component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( COMPONENTS );

        asData.setString( TYPE, component.getType().toString() );
        asData.setString( PATH, component.getPath() != null ? component.getPath().toString() : "/" );

        applyComponentToData( component, asData );
    }

    @Override
    public abstract DATA fromData( final PropertySet asData );

    protected abstract void applyComponentToData( final DATA component, final PropertySet asData );
}
