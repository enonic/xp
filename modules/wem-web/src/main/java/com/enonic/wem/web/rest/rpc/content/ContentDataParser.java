package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.form.FormItemPath;
import com.enonic.wem.api.schema.content.form.Input;

final class ContentDataParser
{
    private ContentType contentType;

    ContentDataParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    ContentDataParser()
    {
    }

    ContentData parse( final ObjectNode data )
    {
        final ContentData contentData = new ContentData();

        final Iterator<String> fieldNames = data.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final DataPath path = DataPath.from( fieldName );

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
                    contentData.setProperty( path, value );
                }
                else
                {
                    contentData.setProperty( path, new Value.Text( fieldValue ) );
                }
            }
        }

        return contentData;
    }

}
