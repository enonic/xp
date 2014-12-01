package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data2.PropertySet;

public abstract class AbstractDescriptorBasedPageComponentDataSerializer<TO_DATA_INPUT extends DescriptorBasedPageComponent, FROM_DATA_OUTPUT extends DescriptorBasedPageComponent>
    extends AbstractPageComponentDataSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    protected void applyPageComponentToData( final AbstractDescriptorBasedPageComponent component, final PropertySet asData )
    {
        super.applyPageComponentToData( component, asData );
        asData.ifNotNull().setString( "template", component.getDescriptor() != null ? component.getDescriptor().toString() : null );
        asData.addSet( "config", component.getConfig().getRoot().copy( asData.getTree() ) );
    }

    protected void applyPageComponentFromData( final AbstractDescriptorBasedPageComponent.Builder component, final PropertySet asData )
    {
        super.applyPageComponentFromData( component, asData );
        if ( asData.hasProperty( "template" ) )
        {
            component.descriptor( toDescriptorKey( asData.getString( "template" ) ) );
        }

        component.config( asData.getSet( "config" ).toTree() );
    }

    protected abstract DescriptorKey toDescriptorKey( final String s );
}
