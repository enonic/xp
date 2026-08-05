package com.enonic.xp.core.nodb.corpus;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

/**
 * Turns literal DSL JSON into the expression objects the query path accepts.
 * <p>
 * The corpus needs the DSL family (not only NoQL) because the two families resolve field names
 * differently: the expression tree types from the {@code Value}, while the DSL only recognises
 * {@code Number} -&gt; {@code ._number} and requires an explicit {@code "type": "dateTime"} to
 * reach {@code ._datetime} (Gate 0(c), rule 3 divergence). Pinning both sides here is what makes
 * the divergence visible in the baseline instead of at Gate C.
 */
final class CorpusDsl
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP = new TypeReference<>()
    {
    };

    private CorpusDsl()
    {
    }

    static ConstraintExpr dsl( final String json )
    {
        return DslExpr.from( tree( json ) );
    }

    static OrderExpr dslOrder( final String json )
    {
        return DslOrderExpr.from( tree( json ) );
    }

    private static PropertyTree tree( final String json )
    {
        try
        {
            final JsonNode node = MAPPER.readTree( json );
            return PropertyTree.fromMap( MAPPER.convertValue( node, MAP ) );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Not valid DSL JSON: " + json, e );
        }
    }
}
