package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

public abstract class PageComponentDataSerializer<TO_DATA_INPUT extends PageComponent, FROM_DATA_OUTPUT extends PageComponent>
    extends AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public static PageComponentDataSerializer get( final DataSet dataSet )
    {
        return PageComponentType.bySimpleClassName( dataSet.getName() ).getDataSerializer();
    }

    public static PageComponentDataSerializer get( final PageComponent component )
    {
        return component.getType().getDataSerializer();
    }

    public abstract DataSet toData( final TO_DATA_INPUT component );

    public abstract FROM_DATA_OUTPUT fromData( final DataSet asData );

    protected void applyPageComponentToData( final AbstractDescriptorBasedPageComponent component, final DataSet asData )
    {
        asData.setProperty( "name", Value.newString( component.getName().toString() ) );
        if ( component.getDescriptor() != null )
        {
            asData.setProperty( "template", Value.newString( component.getDescriptor().toString() ) );
        }

        if ( component.hasConfig() )
        {
            asData.add( component.getConfig().toDataSet( "config" ) );
        }
    }

    protected void applyPageComponentFromData( final AbstractDescriptorBasedPageComponent.Builder component, final DataSet asData )
    {
        component.name( new ComponentName( asData.getProperty( "name" ).getString() ) );
        if ( asData.hasData( "template" ) )
        {
            component.descriptor( toDescriptorkey( asData.getProperty( "template" ).getString() ) );
        }
        if ( asData.hasData( "config" ) )
        {
            component.config( asData.getData( "config" ).toDataSet().toRootDataSet() );
        }
    }

    protected abstract DescriptorKey toDescriptorkey( final String s );
}
