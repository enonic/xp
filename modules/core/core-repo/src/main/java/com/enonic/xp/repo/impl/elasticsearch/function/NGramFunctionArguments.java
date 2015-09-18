package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.builder.function.AbstractSimpleQueryStringFunction;
import com.enonic.xp.repo.impl.entity.NodeConstants;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

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
        return IndexQueryFieldNameResolver.resolveNGramFieldName( baseFieldName );
    }
}
