package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;

public abstract class AbstractDescriptorBasedPageComponentDataSerializer<TO_DATA_INPUT extends DescriptorBasedPageComponent, FROM_DATA_OUTPUT extends DescriptorBasedPageComponent>
    extends AbstractPageComponentDataSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    protected void applyPageComponentToData( final AbstractDescriptorBasedPageComponent component, final DataSet asData )
    {
        super.applyPageComponentToData( component, asData );
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
        super.applyPageComponentFromData( component, asData );
        if ( asData.hasData( "template" ) )
        {
            component.descriptor( toDescriptorKey( asData.getProperty( "template" ).getString() ) );
        }
        if ( asData.hasData( "config" ) )
        {
            component.config( asData.getData( "config" ).toDataSet().toRootDataSet() );
        }
    }

    protected abstract DescriptorKey toDescriptorKey( final String s );
}
