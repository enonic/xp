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

public class DataSetSerializerJson
{
    static void generate( final DataSet dataSet, final JsonGenerator g )
        throws IOException
    {
        g.writeStartObject();
        g.writeStringField( "path", dataSet.getPath().toString() );
        g.writeArrayFieldStart( "entries" );
        for ( final Entry entry : dataSet )
        {
            if ( entry instanceof DataSet )
            {
                DataSetSerializerJson.generate( ( (DataSet) entry ), g );
            }
            else if ( entry instanceof Data )
            {
                DataSerializerJson.generate( entry, g );
            }
        }
        g.writeEndArray();
        g.writeEndObject();
    }

    static DataSet parse( final JsonNode entriesNode, final ConfigItems configItems )
    {
        final JsonNode entriesArray = entriesNode.get( "entries" );
        final EntryPath entriesPath = new EntryPath( JsonParserUtil.getStringValue( "path", entriesNode ) );

        final DataSet dataSet = newEntries( entriesPath, configItems );
        final Iterator<JsonNode> entryIt = entriesArray.getElements();
        while ( entryIt.hasNext() )
        {
            final JsonNode entryNode = entryIt.next();
            final EntryPath path = new EntryPath( JsonParserUtil.getStringValue( "path", entryNode ) );

            if ( configItems == null )
            {
                if ( isEntriesNode( entryNode ) )
                {

                    final DataSet childDataSet = DataSetSerializerJson.parse( entryNode, null );
                    final DataSet entry = new DataSet( path, childDataSet );
                    dataSet.add( entry );
                }
                else
                {
                    final Entry entry = DataSerializerJson.parse( entryNode );
                    dataSet.add( entry );
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
                    final Entry entry = DataSerializerJson.parse( entryNode );
                    dataSet.add( entry );
                }
                else if ( item.getConfigItemType() == ConfigItemType.FIELD_SET )
                {
                    final FieldSet fieldSet = (FieldSet) item;
                    final DataSet childDataSet = DataSetSerializerJson.parse( entryNode, fieldSet.getConfigItems() );
                    final DataSet entry = new DataSet( path, fieldSet, childDataSet );
                    dataSet.add( entry );
                }
            }
        }

        return dataSet;
    }

    private static boolean isEntriesNode( JsonNode node )
    {
        return node.get( "entries" ) != null;
    }

    private static DataSet newEntries( final EntryPath path, ConfigItems configItems )
    {
        if ( configItems == null )
        {
            return new DataSet( path );
        }
        else
        {
            return new DataSet( path, configItems );
        }
    }
}
