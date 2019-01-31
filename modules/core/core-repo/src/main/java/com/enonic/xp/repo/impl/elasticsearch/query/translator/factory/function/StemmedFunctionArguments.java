package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexStemmedController;
import com.enonic.xp.repo.impl.index.IndexValueTypeInterface;

public class StemmedFunctionArguments
    extends AbstractSimpleQueryStringFunctionArguments
{
    private final String functionName = "stemmed";

    private String language;

    StemmedFunctionArguments( final List<ValueExpr> arguments )
    {
        super( arguments );
    }

    @Override
    protected String resolveAnalyzer( final String language )
    {
        this.language = language;
        return IndexStemmedController.resolveAnalyzer( this.language );
    }

    @Override
    public String getFunctionName()
    {
        return functionName;
    }

    @Override
    public String resolveQueryFieldName( final String baseFieldName )
    {
        final IndexValueTypeInterface type = IndexStemmedController.resolveIndexValueType( this.language );

        return type != null ? new SearchQueryFieldNameResolver().resolve( baseFieldName, type ) : "";
    }
}
