package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexLanguageController;

public class StemmedFunctionArguments
    extends AbstractSimpleQueryStringFunctionArguments
{
    private String language;

    StemmedFunctionArguments( final List<ValueExpr> arguments )
    {
        super( arguments );
    }

    @Override
    protected String resolveAnalyzer( final String language )
    {
        this.language = language;
        return IndexLanguageController.resolveAnalyzer( this.language );
    }

    @Override
    public String getFunctionName()
    {
        return "stemmed";
    }

    @Override
    public String resolveQueryFieldName( final String baseFieldName )
    {
        return IndexLanguageController.isSupported( this.language )
            ? SearchQueryFieldNameResolver.INSTANCE.resolve( baseFieldName, IndexLanguageController.resolveStemmedIndexValueType(
            this.language ) )
            : "";
    }
}
