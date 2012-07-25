package com.enonic.wem.core.content.type.configitem;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

public class ConfigItems
    implements Iterable<ConfigItem>
{
    private FieldPath path;

    private LinkedHashMap<String, ConfigItem> items = new LinkedHashMap<String, ConfigItem>();

    public ConfigItems()
    {
        path = new FieldPath();
    }

    public FieldPath getPath()
    {
        return path;
    }

    public void setPath( final FieldPath path )
    {
        this.path = path;
    }

    public void addConfig( final ConfigItem item )
    {
        item.setPath( new FieldPath( path, item.getName() ) );
        Object previous = items.put( item.getName(), item );
        Preconditions.checkArgument( previous == null, "ConfigItem already added: " + item );
    }

    public ConfigItem getConfig( final String name )
    {
        return items.get( name );
    }

    /* TODO: remove:
    public ConfigItem getConfig( final FieldPath fieldPath )
    {
        ConfigItem foundConfigItem = items.get( fieldPath );
        if ( foundConfigItem != null )
        {
            return foundConfigItem;
        }

        final FieldPath fieldPathToMatchWith = fieldPath.asNewUsingAllExceptLastPathElement();
        return items.get( fieldPathToMatchWith );
    }*/

    public Iterator<ConfigItem> iterator()
    {
        return items.values().iterator();
    }

    public int size()
    {
        return items.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( path.toString() );
        s.append( ": " );
        int index = 0;
        final int size = items.size();
        for ( ConfigItem entry : items.values() )
        {
            s.append( entry.getName() );
            if ( index < size - 1 )
            {
                s.append( "," );
            }
            index++;
        }
        return s.toString();
    }
}
