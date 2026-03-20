package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;

public class StemmedFunctionArguments
    extends AbstractSimpleQueryStringFunctionArguments
{
    private Locale language;

    StemmedFunctionArguments( final List<ValueExpr> arguments )
    {
        super( arguments );
    }

    @Override
    protected String resolveAnalyzer( final String language )
    {
        this.language = Locale.forLanguageTag( Objects.requireNonNull( language, "language is required" ) );
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
        final IndexValueType indexValueType = IndexLanguageController.resolveStemmedIndexValueType( this.language );
        if ( indexValueType == null )
        {
            throw new IllegalArgumentException( "Unsupported language for stemmed function: " + language );
        }
        return SearchQueryFieldNameResolver.INSTANCE.resolve( baseFieldName, indexValueType );
    }
}
