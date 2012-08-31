package com.enonic.wem.web.json.result;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public final class JsonErrorResult
    extends JsonDataResult
{
    private final ArrayNode errors;

    public JsonErrorResult()
    {
        super( false );
        this.errors = JsonNodeFactory.instance.arrayNode();
    }

    public JsonErrorResult error( final String id, final String message, final Object... data )
    {
        final ObjectNode node = this.errors.addObject();
        node.put( "id", id );
        node.put( "msg", message );

        if (data != null) {
            final ArrayNode array = node.putArray( "data" );
            for (final Object o : data) {
                array.add( o.toString() );
            }
        }

        return this;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "errors", this.errors );
    }
}
