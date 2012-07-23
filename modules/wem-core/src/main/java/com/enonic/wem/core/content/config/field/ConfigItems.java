package com.enonic.wem.core.content.config.field;


import java.util.Iterator;
import java.util.LinkedHashMap;

public class ConfigItems
    implements Iterable<ConfigItem>
{
    private FieldPath path;

    private LinkedHashMap<FieldPath, ConfigItem> items = new LinkedHashMap<FieldPath, ConfigItem>();

    public ConfigItems()
    {
        path = new FieldPath();
    }

    public void setPath( final FieldPath path )
    {
        this.path = path;
    }

    public void addField( final ConfigItem item )
    {
        item.setPath( new FieldPath( path, item.getName() ) );
        items.put( item.getPath(), item );
    }

    public ConfigItem getConfig( final FieldPath fieldPath )
    {
        ConfigItem foundConfigItem = items.get( fieldPath );
        if ( foundConfigItem != null )
        {
            return foundConfigItem;
        }

        final FieldPath fieldPathToMatchWith = fieldPath.asNewUsingFirstPathElement();
        return items.get( fieldPathToMatchWith );
    }

    public Iterator<ConfigItem> iterator()
    {
        return items.values().iterator();
    }

    public int size()
    {
        return items.size();
    }
}
