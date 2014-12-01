package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data2.PropertySet;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public abstract class AbstractPageComponentDataSerializer<TO_DATA_INPUT extends PageComponent, FROM_DATA_OUTPUT extends PageComponent>
    extends AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract void toData( final TO_DATA_INPUT component, final PropertySet parent );

    public abstract FROM_DATA_OUTPUT fromData( final PropertySet asData );

    protected void applyPageComponentToData( final AbstractPageComponent component, final PropertySet asData )
    {
        asData.setString( "name", component.getName().toString() );
    }

    protected void applyPageComponentFromData( final AbstractPageComponent.Builder component, final PropertySet asData )
    {
        component.name( new ComponentName( asData.getString( "name" ) ) );
    }
}
