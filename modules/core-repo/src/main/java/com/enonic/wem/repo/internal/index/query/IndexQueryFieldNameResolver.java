package com.enonic.wem.repo.internal.index.query;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;
import com.enonic.wem.repo.internal.index.IndexValueType;

public class IndexQueryFieldNameResolver
{
    public static String resolve( final CompareExpr compareExpr )
    {
        final FieldExpr field = compareExpr.getField();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( field.getFieldPath() );

        final ValueExpr firstValue = compareExpr.getFirstValue();

        return createValueTypeAwareFieldName( baseFieldName, firstValue.getValue() );
    }

    public static String resolve( final ValueFilter valueQueryFilter )
    {
        final String valueQueryFilterFieldName = valueQueryFilter.getFieldName();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( valueQueryFilterFieldName );

        final ImmutableSet<Value> values = valueQueryFilter.getValues();
        final Value firstValue = values.iterator().next();

        return createValueTypeAwareFieldName( baseFieldName, firstValue );
    }

    public static String resolveStringFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.STRING );
    }

    public static String resolveNumericFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.NUMBER );
    }

    public static String resolveDateTimeFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.DATETIME );
    }

    public static String resolveAnalyzedFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.ANALYZED );
    }

    public static String resolveNGramFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.NGRAM );
    }

    public static String resolveOrderByFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.ORDERBY );
    }

    public static String resolveGeoPointFieldName( final String queryFieldName )
    {
        return appendIndexValueType( IndexFieldNameNormalizer.normalize( queryFieldName ), IndexValueType.GEO_POINT );
    }

    public static String createValueTypeAwareFieldName( final String baseFieldName, final Value value )
    {
        if ( value.isDateType() )
        {
            return resolveDateTimeFieldName( baseFieldName );
        }

        if ( value.isNumericType() )
        {
            return resolveNumericFieldName( baseFieldName );
        }

        if ( value.isGeoPoint() )
        {
            return resolveGeoPointFieldName( baseFieldName );
        }

        return IndexFieldNameNormalizer.normalize( baseFieldName );
    }

    private static String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType )
    {

        return baseFieldName + ( Strings.isNullOrEmpty( indexValueType.getPostfix() )
            ? ""
            : IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix() );
    }

}

