package com.enonic.xp.core.impl.content.serializer;


import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;

@Beta
public abstract class ComponentDataSerializer<TO_DATA_INPUT extends Component, FROM_DATA_OUTPUT extends Component>
    extends AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public static final String COMPONENTS = "components";

    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String PATH = "path";

    @Override
    public abstract void toData( final TO_DATA_INPUT component, final PropertySet parent );

    @Override
    public abstract FROM_DATA_OUTPUT fromData( final SerializedData data );

    protected void applyComponentToData( final Component component, final PropertySet asData )
    {
        asData.setString( NAME, component.getName() != null ? component.getName().toString() : null );
        asData.setString( TYPE, component.getType().getComponentClass().getSimpleName() );
        asData.setString( PATH, component.getPath() != null ? component.getPath().toString() : null );
    }

    protected void applyComponentFromData( final Component.Builder component, final PropertySet asData )
    {
        component.name( asData.isNotNull( "name" ) ? new ComponentName( asData.getString( "name" ) ) : null );
    }

    public static List<PropertySet> getChildren( final SerializedData data )
    {
        return data.getComponentsAsData().stream().filter( item -> isItemChildOf( item, data.getAsData() ) ).collect( Collectors.toList() );
    }

    private static boolean isItemChildOf( final PropertySet item, final PropertySet parent )
    {
        final String itemPath = item.getString( ComponentDataSerializer.PATH );
        final String parentPath = parent.getString( ComponentDataSerializer.PATH );

        if ( parentPath == null && itemPath == null )
        {
            return false;
        }

        if ( parentPath == null )
        {
            return getLevel( itemPath ) == 1;
        }

        if ( itemPath == null )
        {
            return false;
        }

        return itemPath.startsWith( parentPath ) && ( getLevel( itemPath ) - getLevel( parentPath ) == 1 );
    }

    private static int getLevel( final String path )
    {
        return path.split( "/" ).length;
    }
}
