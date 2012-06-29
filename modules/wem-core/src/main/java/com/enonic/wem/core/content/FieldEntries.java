package com.enonic.wem.core.content;


import java.util.Iterator;
import java.util.LinkedHashMap;

import com.enonic.wem.core.content.config.field.ConfigItem;
import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.SubType;

public class FieldEntries
    implements Iterable<FieldEntry>
{
    private ConfigItems configItems = new ConfigItems();

    private LinkedHashMap<FieldEntryPath, FieldEntry> entries = new LinkedHashMap<FieldEntryPath, FieldEntry>();

    public FieldEntries( final ConfigItems configItems )
    {
        this.configItems = configItems;
    }

    void add( FieldEntry fieldEntry )
    {
        entries.put( fieldEntry.getPath(), fieldEntry );
    }

    public void setFieldValue( final FieldEntryPath fieldEntryPath, final Object value )
    {
        final ConfigItem foundConfig = configItems.getConfig( fieldEntryPath.resolveFieldPath() );
        if ( foundConfig == null )
        {
            throw new IllegalArgumentException( "No configuration found at: " + fieldEntryPath );
        }
        else if ( foundConfig instanceof Field )
        {
            final Field field = (Field) foundConfig;
            entries.put( fieldEntryPath, FieldValue.newBuilder().field( field ).fieldEntryPath( fieldEntryPath ).value( value ).build() );
        }
        else if ( foundConfig instanceof SubType )
        {
            final SubType subType = (SubType) foundConfig;
            final FieldEntryPath pathToSubTypeValues = fieldEntryPath.asNewUsingFirstPathElement();
            SubTypeEntries subTypeEntries = (SubTypeEntries) entries.get( pathToSubTypeValues );
            if ( subTypeEntries == null )
            {
                subTypeEntries = new SubTypeEntries( subType, pathToSubTypeValues );
                entries.put( pathToSubTypeValues, subTypeEntries );
            }
            subTypeEntries.setFieldValue( fieldEntryPath, value );
        }
        else
        {
            throw new IllegalArgumentException( "Field values can only be added to fields: " + fieldEntryPath );
        }

    }

    public Iterator<FieldEntry> iterator()
    {
        return entries.values().iterator();
    }
}
