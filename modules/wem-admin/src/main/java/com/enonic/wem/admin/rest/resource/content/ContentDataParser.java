package com.enonic.wem.admin.rest.resource.content;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.FormItemPath;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.schema.content.ContentType;

public final class ContentDataParser
{
    private ContentType contentType;

    public ContentDataParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    public ContentData parse( final JsonNode data )
    {
        final ContentData contentData = new ContentData();

        final Iterator<String> fieldNames = data.fieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final DataPath path = DataPath.from( fieldName );

            final JsonNode valueNode = data.get( fieldName );

            if ( valueNode.isValueNode() )
            {
                final String fieldValue = valueNode.textValue();
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
                    contentData.setProperty( path, new Value.String( fieldValue ) );
                }
            }
        }

        return contentData;
    }

}
