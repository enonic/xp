package com.enonic.wem.core.index.query.function;

import java.util.List;

import com.enonic.wem.api.query.expr.ValueExpr;

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
}
