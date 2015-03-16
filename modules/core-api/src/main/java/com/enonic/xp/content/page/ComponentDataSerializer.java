package com.enonic.xp.content.page;


import com.enonic.xp.content.page.region.Component;
import com.enonic.xp.content.page.region.ComponentName;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.support.serializer.AbstractDataSetSerializer;

public abstract class ComponentDataSerializer<TO_DATA_INPUT extends Component, FROM_DATA_OUTPUT extends Component>
    extends AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    @Override
    public abstract void toData( final TO_DATA_INPUT component, final PropertySet parent );

    @Override
    public abstract FROM_DATA_OUTPUT fromData( final PropertySet asData );

    protected void applyComponentToData( final Component component, final PropertySet asData )
    {
        asData.setString( "name", component.getName() != null ? component.getName().toString() : null );
    }

    protected void applyComponentFromData( final Component.Builder component, final PropertySet asData )
    {
        component.name( asData.isNotNull( "name" ) ? new ComponentName( asData.getString( "name" ) ) : null );
    }
}
