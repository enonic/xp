package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.form.FormItemPath;
import com.enonic.wem.api.content.schema.content.form.Input;

final class RootDataSetParser
{
    private ContentType contentType;

    RootDataSetParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    RootDataSetParser()
    {
    }

    RootDataSet parse( final ObjectNode data )
    {
        final RootDataSet rootDataSet = DataSet.newRootDataSet();

        final Iterator<String> fieldNames = data.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final EntryPath path = EntryPath.from( fieldName );

            final JsonNode valueNode = data.get( fieldName );

            if ( valueNode.isValueNode() )
            {
                final String fieldValue = valueNode.getTextValue();
                if ( fieldValue == null )
                {
                    continue;
                }

                final Input input = contentType.form().getInput( FormItemPath.from( path.resolvePathElementNames() ) );
                if ( input != null )
                {
                    final Value value = input.getInputType().newValue( fieldValue );
                    rootDataSet.setProperty( path, value );
                }
                else
                {
                    rootDataSet.setProperty( path, new Value.Text( fieldValue ) );
                }
            }
        }

        return rootDataSet;
    }

}
