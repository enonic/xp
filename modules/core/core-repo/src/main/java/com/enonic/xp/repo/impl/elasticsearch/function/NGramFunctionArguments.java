package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.entity.NodeConstants;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class NGramFunctionArguments
    extends AbstractSimpleQueryStringFunction
{

    @Override
    protected String getDefaultAnalyzer()
    {
        return NodeConstants.DEFAULT_NGRAM_SEARCH_ANALYZER;
    }

    @Override
    public String getFunctionName()
    {
        return "ngram";
    }

    public NGramFunctionArguments( final List<ValueExpr> arguments )
    {
        super( arguments );
    }

    @Override
    public String resolveQueryFieldName( final String baseFieldName )
    {
        return new SearchQueryFieldNameResolver().resolve( baseFieldName, IndexValueType.NGRAM );
    }
}
