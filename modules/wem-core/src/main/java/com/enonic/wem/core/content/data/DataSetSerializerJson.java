package com.enonic.wem.core.content.data;


import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;

import com.enonic.wem.core.content.JsonParserUtil;
import com.enonic.wem.core.content.type.configitem.Component;
import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.FieldSet;

public class DataSetSerializerJson
{
    private DataSerializerJson dataSerializer = new DataSerializerJson();

    void generate( final DataSet dataSet, final JsonGenerator g, boolean wrapInObject )
        throws IOException
    {
        if ( wrapInObject )
        {
            g.writeStartObject();
        }
        g.writeStringField( "path", dataSet.getPath().toString() );
        g.writeArrayFieldStart( "entries" );
        for ( final Entry entry : dataSet )
        {
            if ( entry instanceof DataSet )
            {
                generate( ( (DataSet) entry ), g, true );
            }
            else if ( entry instanceof Data )
            {
                dataSerializer.generate( entry, g );
            }
        }
        g.writeEndArray();
        if ( wrapInObject )
        {
            g.writeEndObject();
        }
    }

    DataSet parse( final JsonNode entriesNode, final ConfigItems configItems )
    {
        final EntryPath entriesPath = new EntryPath( JsonParserUtil.getStringValue( "path", entriesNode ) );
        final JsonNode entriesArray = entriesNode.get( "entries" );

        final DataSet dataSet = new DataSet( entriesPath );
        final Iterator<JsonNode> entryIt = entriesArray.getElements();
        while ( entryIt.hasNext() )
        {
            final JsonNode entryNode = entryIt.next();
            final EntryPath path = new EntryPath( JsonParserUtil.getStringValue( "path", entryNode ) );

            if ( configItems == null )
            {
                if ( isEntriesNode( entryNode ) )
                {

                    final DataSet childDataSet = parse( entryNode, null );
                    final DataSet entry = new DataSet( path, childDataSet );
                    dataSet.add( entry );
                }
                else
                {
                    final Entry entry = dataSerializer.parse( entryNode );
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
                else if ( item instanceof Component )
                {
                    final Entry entry = dataSerializer.parse( entryNode );
                    dataSet.add( entry );
                }
                else if ( item instanceof FieldSet )
                {
                    final FieldSet fieldSet = (FieldSet) item;
                    final DataSet childDataSet = parse( entryNode, fieldSet.getConfigItems() );
                    final DataSet entry = new DataSet( path, childDataSet );
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

}
