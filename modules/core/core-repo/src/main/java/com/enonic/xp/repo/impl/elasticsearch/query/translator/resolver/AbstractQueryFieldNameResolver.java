package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Locale;
import java.util.Set;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

abstract class AbstractQueryFieldNameResolver
    implements QueryFieldNameResolver
{
    private final Set<String> builtInFields;

    protected AbstractQueryFieldNameResolver( final Set<String> builtInFields )
    {
        this.builtInFields = builtInFields;
    }

    @Override
    public String resolve( final CompareExpr compareExpr )
    {
        final FieldExpr field = compareExpr.getField();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( field.getFieldPath() );

        final ValueExpr firstValue = compareExpr.getFirstValue();

        if ( firstValue == null )
        {
            return "";
        }

        return createValueTypeAwareFieldName( baseFieldName, firstValue.getValue() );
    }

    @Override
    public String resolve( final ValueFilter valueQueryFilter )
    {
        final String valueQueryFilterFieldName = valueQueryFilter.getFieldName();
        final String baseFieldName = IndexFieldNameNormalizer.normalize( valueQueryFilterFieldName );
        final Set<Value> values = valueQueryFilter.getValues();
        final Value firstValue = values.iterator().next();
        return createValueTypeAwareFieldName( baseFieldName, firstValue );
    }

    @Override
    public String resolve( final String queryFieldName )
    {
        return appendIndexValueType( queryFieldName, StaticIndexValueType.STRING );
    }


    @Override
    public String resolve( final String queryFieldName, final IndexValueType indexValueType )
    {
        return appendIndexValueType( queryFieldName, indexValueType );
    }

    @Override
    public String resolve( final String queryFieldName, final Value value )
    {
        return createValueTypeAwareFieldName( queryFieldName, value );
    }

    @Override
    public String resolveOrderByFieldName( final String queryFieldName, final Locale language )
    {
        final String normalizedFieldName = IndexFieldNameNormalizer.normalize( queryFieldName );

        if ( builtInFields.contains( normalizedFieldName ) )
        {
            return normalizedFieldName;
        }

        return appendIndexValueType( normalizedFieldName, language == null
            ? StaticIndexValueType.ORDERBY
            : IndexLanguageController.resolveOrderByIndexValueType( language ) );
    }

    private String createValueTypeAwareFieldName( final String baseFieldName, final Value value )
    {
        if ( value.isDateType() )
        {
            return appendIndexValueType( baseFieldName, StaticIndexValueType.DATETIME );
        }

        if ( value.isNumericType() )
        {
            return appendIndexValueType( baseFieldName, StaticIndexValueType.NUMBER );
        }

        if ( value.isGeoPoint() )
        {
            return appendIndexValueType( baseFieldName, StaticIndexValueType.GEO_POINT );
        }

        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }

    protected abstract String appendIndexValueType( String baseFieldName, IndexValueType indexValueType );
}
