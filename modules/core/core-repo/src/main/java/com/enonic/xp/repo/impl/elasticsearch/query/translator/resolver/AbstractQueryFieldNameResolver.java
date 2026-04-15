package com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver;

import java.util.Locale;
import java.util.Set;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.index.IndexLanguageController;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.repo.impl.index.StaticIndexValueType;

abstract class AbstractQueryFieldNameResolver
    implements QueryFieldNameResolver
{
    private static final Set<IndexPath> BUILT_IN_FIELDS = Set.of( IndexPath.from( "_score" ), IndexPath.from( "_id" ) );

    public String resolve( final IndexPath queryFieldName, final IndexValueType indexValueType )
    {
        return indexValueType.getPostfix().isEmpty()
            ? queryFieldName.getPath()
            : queryFieldName.getPath() + IndexValueType.INDEX_VALUE_TYPE_SEPARATOR + indexValueType.getPostfix();
    }

    @Override
    public String resolve( final String queryFieldName, final IndexValueType indexValueType )
    {
        return resolve( IndexPath.from( queryFieldName ), indexValueType );
    }

    @Override
    public String resolve( final IndexPath queryFieldName, final Value value )
    {
        if ( value.isDateType() )
        {
            return resolve( queryFieldName, StaticIndexValueType.DATETIME );
        }

        if ( value.isNumericType() )
        {
            return resolve( queryFieldName, StaticIndexValueType.NUMBER );
        }

        if ( value.isGeoPoint() )
        {
            return resolve( queryFieldName, StaticIndexValueType.GEO_POINT );
        }

        return queryFieldName.getPath();
    }

    @Override
    public String resolve( final String queryFieldName, final Value value )
    {
        return resolve( IndexPath.from( queryFieldName ), value );
    }

    @Override
    public String resolveOrderByFieldName( final IndexPath queryFieldName, final Locale language )
    {
        if ( BUILT_IN_FIELDS.contains( queryFieldName ) )
        {
            return queryFieldName.getPath();
        }
        return resolve( queryFieldName, language == null
            ? StaticIndexValueType.ORDERBY
            : IndexLanguageController.resolveOrderByIndexValueType( language ) );
    }
}
