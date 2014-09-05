package com.enonic.wem.core.index.function;

import java.util.List;

import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.core.elasticsearch.query.builder.function.AbstractSimpleQueryStringFunction;
import com.enonic.wem.core.index.query.IndexQueryFieldNameResolver;

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
