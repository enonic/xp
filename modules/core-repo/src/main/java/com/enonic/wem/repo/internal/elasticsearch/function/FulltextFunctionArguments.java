package com.enonic.wem.repo.internal.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.function.AbstractSimpleQueryStringFunction;
import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;

public class FulltextFunctionArguments
    extends AbstractSimpleQueryStringFunction
{

    private final String functionName = "fulltext";


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
        return IndexQueryFieldNameResolver.resolveAnalyzedFieldName( baseFieldName );
    }
}
