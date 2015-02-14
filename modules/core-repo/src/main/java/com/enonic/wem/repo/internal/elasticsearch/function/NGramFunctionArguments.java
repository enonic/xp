package com.enonic.wem.repo.internal.elasticsearch.function;

import java.util.List;

import com.enonic.xp.core.query.expr.ValueExpr;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.function.AbstractSimpleQueryStringFunction;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class NGramFunctionArguments
    extends AbstractSimpleQueryStringFunction
{
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
