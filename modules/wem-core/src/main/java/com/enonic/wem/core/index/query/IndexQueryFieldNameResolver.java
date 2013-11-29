package com.enonic.wem.core.index.query;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.IndexFieldNameNormalizer;
import com.enonic.wem.core.index.IndexValueType;
import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.FieldExpr;
import com.enonic.wem.query.expr.ValueExpr;

public class IndexQueryFieldNameResolver
{
    public static String resolve( final CompareExpr compareExpr )
    {
        final FieldExpr field = compareExpr.getField();

        final String baseFieldName = IndexFieldNameNormalizer.normalize( field.getName() );

        final ValueExpr firstValue = compareExpr.getFirstValue();

        return createValueTypeAwareFieldName( baseFieldName, firstValue );
    }

    private static String createValueTypeAwareFieldName( final String baseFieldName, final ValueExpr valueExpr )
    {
        final Value<?> value = valueExpr.getValue();

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

        return baseFieldName;
    }

    private static String appendIndexValueType( final String baseFieldName, final IndexValueType indexValueType )
    {
        return baseFieldName + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix();
    }

}

