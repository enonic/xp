package com.enonic.xp.core.impl.content.page.region;


import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.FragmentComponent;

public class FragmentComponentDataSerializer
    extends ComponentDataSerializer<FragmentComponent, FragmentComponent>
{

    private static final String FRAGMENT_PROPERTY = "fragment";

    @Override
    public void toData( final FragmentComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( FragmentComponent.class.getSimpleName() );
        applyComponentToData( component, asData );
        if ( component.getFragment() != null )
        {
            asData.addString( FRAGMENT_PROPERTY, component.getFragment().toString() );
        }
        if ( component.hasConfig() )
        {
            asData.addSet( "config", component.getConfig().getRoot().copy( asData.getTree() ) );
        }
    }

    @Override
    public FragmentComponent fromData( final PropertySet asData )
    {
        final FragmentComponent.Builder component = FragmentComponent.create();
        applyComponentFromData( component, asData );
        if ( asData.isNotNull( FRAGMENT_PROPERTY ) )
        {
            component.fragment( ContentId.from( asData.getString( FRAGMENT_PROPERTY ) ) );
        }
        if ( asData.hasProperty( "config" ) )
        {
            component.config( asData.getSet( "config" ).toTree() );
        }
        return component.build();
    }
}
