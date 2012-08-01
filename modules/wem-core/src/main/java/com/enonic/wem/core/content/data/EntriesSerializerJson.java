package com.enonic.wem.core.content.data;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;

public class EntriesSerializerJson
{
    static void generate( final Entries entries, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "path", entries.getPath().toString() );
        g.writeArrayFieldStart( "entries" );
        for ( final Entry entry : entries )
        {
            if ( entry instanceof Entries )
            {
                EntriesSerializerJson.generate( ( (Entries) entry ), g );
            }
            else if ( entry instanceof Value )
            {
                ValueSerializerJson.generate( entry, g );
            }
        }
        g.writeEndArray();
        g.writeEndObject();
    }

    static Entries parse( final JsonNode entriesNode, final ConfigItems configItems )
    {
        final JsonNode entriesArray = entriesNode.get( "entries" );
        final EntryPath entriesPath = new EntryPath( JsonParserUtil.getStringValue( "path", entriesNode ) );

        final Entries entries = newEntries( entriesPath, configItems );
        final Iterator<JsonNode> entryIt = entriesArray.getElements();
        while ( entryIt.hasNext() )
        {
            final JsonNode entryNode = entryIt.next();
            final EntryPath path = new EntryPath( JsonParserUtil.getStringValue( "path", entryNode ) );

            if ( configItems == null )
            {
                if ( isEntriesNode( entryNode ) )
                {

                    final Entries childEntries = EntriesSerializerJson.parse( entryNode, null );
                    final Entries entry = new Entries( path, childEntries );
                    entries.add( entry );
                }
                else
                {
                    final Entry entry = ValueSerializerJson.parse( entryNode );
                    entries.add( entry );
                }
            }
            else
            {
                final ConfigItemPath configItemPath = path.resolveConfigItemPath();

                final ConfigItem item = configItems.getConfigItem( configItemPath.getLastElement() );

                if ( item == null )
                {
                    //
                }
                else if ( item.getConfigItemType() == ConfigItemType.FIELD )
                {
                    final Entry entry = ValueSerializerJson.parse( entryNode );
                    entries.add( entry );
                }
                else if ( item.getConfigItemType() == ConfigItemType.FIELD_SET )
                {
                    final FieldSet fieldSet = (FieldSet) item;
                    final Entries childEntries = EntriesSerializerJson.parse( entryNode, fieldSet.getConfigItems() );
                    final Entries entry = new Entries( path, fieldSet, childEntries );
                    entries.add( entry );
                }
            }
        }

        return entries;
    }

    private static boolean isEntriesNode( JsonNode node )
    {
        return node.get( "entries" ) != null;
    }

    private static Entries newEntries( final EntryPath path, ConfigItems configItems )
    {
        if ( configItems == null )
        {
            return new Entries( path );
        }
        else
        {
            return new Entries( path, configItems );
        }
    }
}
