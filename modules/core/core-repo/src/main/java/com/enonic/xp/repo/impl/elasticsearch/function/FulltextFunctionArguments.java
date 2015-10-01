package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.entity.NodeConstants;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class FulltextFunctionArguments
    extends AbstractSimpleQueryStringFunction
{
    private final String functionName = "fulltext";

    @Override
    protected String getDefaultAnalyzer()
    {
        return NodeConstants.DEFAULT_FULLTEXT_SEARCH_ANALYZER;
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
