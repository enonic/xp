package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.FilterBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.FilterBuilderFactory;

public class FilterBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    @Test
    public void createStringValueFilter()
        throws Exception
    {
        final ValueFilter queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( ValueFactory.newString( "myValue1" ) ).
            addValue( ValueFactory.newString( "myValue2" ) ).
            build();

        final String expected = load( "filter_values_string.json" );
        final FilterBuilder filterBuilder =
            new FilterBuilderFactory( new SearchQueryFieldNameResolver() ).create( Filters.from( queryFilter ) );

        Assert.assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    public void createNumberValueFilter()
        throws Exception
    {
        final ValueFilter queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( ValueFactory.newDouble( 1.0 ) ).
            addValue( ValueFactory.newDouble( 2.0 ) ).
            build();

        final String expected = load( "filter_values_number.json" );
        final FilterBuilder filterBuilder =
            new FilterBuilderFactory( new SearchQueryFieldNameResolver() ).create( Filters.from( queryFilter ) );

        Assert.assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    public void createExistsFilter()
        throws Exception
    {
        final ExistsFilter queryFilter = ExistsFilter.create().
            fieldName( "myField" ).
            build();

        final String expected = load( "filter_exists.json" );
        final FilterBuilder filterBuilder =
            new FilterBuilderFactory( new SearchQueryFieldNameResolver() ).create( Filters.from( queryFilter ) );

        Assert.assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }

    @Test
    public void createBooleanFilter()
        throws Exception
    {
        final BooleanFilter.Builder builder = BooleanFilter.create();

        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.mustNot( ExistsFilter.create().fieldName( "MyMustNot" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );

        final String expected = load( "filter_boolean.json" );
        final FilterBuilder filterBuilder =
            new FilterBuilderFactory( new SearchQueryFieldNameResolver() ).create( Filters.from( builder.build() ) );

        Assert.assertEquals( cleanString( expected ), cleanString( filterBuilder.toString() ) );
    }
}
