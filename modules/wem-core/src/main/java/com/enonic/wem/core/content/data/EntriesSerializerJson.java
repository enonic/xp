package com.enonic.wem.core.content.data;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldPath;
import com.enonic.wem.core.content.type.configitem.SubType;

public class EntriesSerializerJson
{
    public static void generate( final Entries entries, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "path", entries.getPath().toString() );
        g.writeArrayFieldStart( "entries" );
        for ( final Entry entry : entries )
        {
            if ( entry instanceof SubTypeEntry )
            {
                EntriesSerializerJson.generate( ( (SubTypeEntry) entry ).getEntries(), g );
            }
            else if ( entry instanceof Value )
            {
                ValueSerializerJson.generate( entry, g );
            }
        }
        g.writeEndArray();
        g.writeEndObject();
    }

    public static Entries parse( final JsonNode entriesNode, final ConfigItems configItems )
    {
        final JsonNode entriesArray = entriesNode.get( "entries" );
        final ValuePath entriesPath = new ValuePath( JsonParserUtil.getStringValue( "path", entriesNode ) );

        final Entries entries = newEntries( entriesPath, configItems );
        final Iterator<JsonNode> entryIt = entriesArray.getElements();
        while ( entryIt.hasNext() )
        {
            final JsonNode entryNode = entryIt.next();
            final ValuePath path = new ValuePath( JsonParserUtil.getStringValue( "path", entryNode ) );

            if ( configItems == null )
            {
                if ( isSubTypeEntry( entryNode ) )
                {

                    final Entries childEntries = EntriesSerializerJson.parse( entryNode, null );
                    final SubTypeEntry entry = new SubTypeEntry( path, childEntries );
                    entries.add( entry );
                }
                else
                {
                    final Entry entry = ValueSerializerJson.parse( entryNode, null );
                    entries.add( entry );
                }
            }
            else
            {
                final FieldPath fieldPath = path.resolveFieldPath();

                final ConfigItem item = configItems.getConfig( fieldPath.getLastElement() );

                if ( item == null )
                {
                    //
                }
                else if ( item.getItemType() == ConfigItemType.FIELD )
                {
                    final Entry entry = ValueSerializerJson.parse( entryNode, item );
                    entries.add( entry );
                }
                else if ( item.getItemType() == ConfigItemType.SUB_TYPE )
                {
                    final SubType subType = (SubType) item;
                    final Entries childEntries = EntriesSerializerJson.parse( entryNode, subType.getConfigItems() );
                    final SubTypeEntry entry = new SubTypeEntry( subType, path, childEntries );
                    entries.add( entry );
                }
            }
        }

        return entries;
    }

    private static boolean isSubTypeEntry( JsonNode node )
    {
        return node.get( "entries" ) != null;
    }

    private static Entries newEntries( final ValuePath path, ConfigItems configItems )
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
