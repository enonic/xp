package com.enonic.wem.core.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.UpdateScript;

public class UpdateScriptXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{

    public static XContentBuilder create( final UpdateScript updateScript )
    {
        try
        {
            final XContentBuilder xContentBuilder = startBuilder();
            addField( xContentBuilder, "script", updateScript.getScript() );
            addParams( updateScript, xContentBuilder );
            endBuilder( xContentBuilder );
            return xContentBuilder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for UpdateScript", e );
        }
    }

    private static void addParams( final UpdateScript updateScript, final XContentBuilder xContentBuilder )
        throws Exception
    {
        xContentBuilder.startObject( "params" );

        final ImmutableMap<String, Value> parameters = updateScript.getParams();
        for ( final String param : parameters.keySet() )
        {
            final Value value = parameters.get( param );
            addField( xContentBuilder, param, ElasticsearchValueConverter.convert( value ) );
        }

        xContentBuilder.endObject();
    }


}
