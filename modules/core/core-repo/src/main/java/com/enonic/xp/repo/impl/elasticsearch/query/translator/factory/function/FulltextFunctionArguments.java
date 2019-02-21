package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.node.NodeConstants;

public class FulltextFunctionArguments
    extends AbstractSimpleQueryStringFunctionArguments
{
    private final String functionName = "fulltext";

    @Override
    protected String resolveAnalyzer( final String value )
    {
        return StringUtils.isBlank( value ) ? NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER : value;
    }

    public FulltextFunctionArguments( final List<ValueExpr> arguments )
    {
        super( arguments );
    }

    @Override
    public String getFunctionName()
    {
        return functionName;
    }


    @Override
    public String resolveQueryFieldName( final String baseFieldName )
    {
        return new SearchQueryFieldNameResolver().resolve( baseFieldName, IndexValueType.ANALYZED );
    }
}
