package com.enonic.wem.core.index.query.function;

import java.util.List;

import com.enonic.wem.api.query.expr.ValueExpr;

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

}
