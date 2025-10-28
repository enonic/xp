package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    void createStringValueFilter()
    {
        final ValueFilter queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( ValueFactory.newString( "myValue1" ) ).
            addValue( ValueFactory.newString( "myValue2" ) ).
            build();

        final String expected = load( "filter_values_string.json" );
        final QueryBuilder filterBuilder =
            new FilterBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( Filters.from( queryFilter ) );

        assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    void createNumberValueFilter()
    {
        final ValueFilter queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( ValueFactory.newDouble( 1.0 ) ).
            addValue( ValueFactory.newDouble( 2.0 ) ).
            build();

        final String expected = load( "filter_values_number.json" );
        final QueryBuilder filterBuilder =
            new FilterBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( Filters.from( queryFilter ) );

        assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    void createExistsFilter()
    {
        final ExistsFilter queryFilter = ExistsFilter.create().
            fieldName( "myField" ).
            build();

        final String expected = load( "filter_exists.json" );
        final QueryBuilder filterBuilder =
            new FilterBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( Filters.from( queryFilter ) );

        assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    void createBooleanFilter()
    {
        final BooleanFilter.Builder builder = BooleanFilter.create();

        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.mustNot( ExistsFilter.create().fieldName( "MyMustNot" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );

        final String expected = load( "filter_boolean.json" );
        final QueryBuilder filterBuilder =
            new FilterBuilderFactory( SearchQueryFieldNameResolver.INSTANCE ).create( Filters.from( builder.build() ) );

        assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }
}
