package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;
import com.enonic.xp.repo.impl.index.IndexValueType;

abstract class AbstractQueryFieldNameResolver
    implements QueryFieldNameResolver
{

    public String resolve( final CompareExpr compareExpr )
    {
        final FieldExpr field = compareExpr.getField();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( field.getFieldPath() );

        final ValueExpr firstValue = compareExpr.getFirstValue();

        return createValueTypeAwareFieldName( baseFieldName, firstValue.getValue() );
    }

    public String resolve( final ValueFilter valueQueryFilter )
    {
        final String valueQueryFilterFieldName = valueQueryFilter.getFieldName();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( valueQueryFilterFieldName );

        final ImmutableSet<Value> values = valueQueryFilter.getValues();
        final Value firstValue = values.iterator().next();

        return createValueTypeAwareFieldName( baseFieldName, firstValue );
    }

    public String resolve( final String queryFieldName )
    {
        return appendIndexValueType( queryFieldName, IndexValueType.STRING );
    }


    public String resolve( final String queryFieldName, final IndexValueType indexValueType )
    {
        return appendIndexValueType( queryFieldName, indexValueType );
    }

    public String resolve( final String queryFieldName, final Value value )
    {
        return createValueTypeAwareFieldName( queryFieldName, value );
    }

    public String resolveOrderByFieldName( final String queryFieldName )
    {
        final String normalizedFieldName = IndexFieldNameNormalizer.normalize( queryFieldName );

        if ( getBuiltInFields().contains( normalizedFieldName ) )
        {
            return normalizedFieldName;
        }

        return appendIndexValueType( normalizedFieldName, IndexValueType.ORDERBY );
    }

    private String createValueTypeAwareFieldName( final String baseFieldName, final Value value )
    {
        if ( value.isDateType() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.DATETIME );
        }

        if ( value.isNumericType() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.NUMBER );
        }

        if ( value.isGeoPoint() )
        {
            return appendIndexValueType( baseFieldName, IndexValueType.GEO_POINT );
        }

        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }

    protected abstract List<String> getBuiltInFields();

    protected abstract String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType );
}
