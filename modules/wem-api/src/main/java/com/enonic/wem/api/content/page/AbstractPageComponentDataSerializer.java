package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public abstract class AbstractPageComponentDataSerializer<TO_DATA_INPUT extends PageComponent, FROM_DATA_OUTPUT extends PageComponent>
    extends AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract DataSet toData( final TO_DATA_INPUT component );

    public abstract FROM_DATA_OUTPUT fromData( final DataSet asData );

    protected void applyPageComponentToData( final AbstractPageComponent component, final DataSet asData )
    {
        asData.setProperty( "name", Value.newString( component.getName().toString() ) );
    }

    protected void applyPageComponentFromData( final AbstractPageComponent.Builder component, final DataSet asData )
    {
        component.name( new ComponentName( asData.getProperty( "name" ).getString() ) );
    }
}
