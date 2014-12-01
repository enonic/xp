package com.enonic.wem.repo.internal.elasticsearch.query.builder

import com.enonic.wem.api.data2.Value
import com.enonic.wem.api.query.filter.BooleanFilter
import com.enonic.wem.api.query.filter.ExistsFilter
import com.enonic.wem.api.query.filter.Filters
import com.enonic.wem.api.query.filter.ValueFilter
import org.elasticsearch.index.query.FilterBuilder

class FilterBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    def "create string value filter"()
    {
        given:
        def queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( Value.newString( "myValue1" ) ).
            addValue( Value.newString( "myValue2" ) ).
            build()
        def expected = this.getClass().getResource( "filter_values_string.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( Filters.from( queryFilter ) )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number value filter"()
    {
        given:
        def queryFilter = ValueFilter.create().
            fieldName( "myField" ).
            addValue( Value.newDouble( 1.0 ) ).
            addValue( Value.newDouble( 2.0 ) ).
            build()
        def expected = this.getClass().getResource( "filter_values_number.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( Filters.from( queryFilter ) )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number exists filter"()
    {
        given:
        def queryFilter = ExistsFilter.create().
            fieldName( "myField" ).
            build();
        def expected = this.getClass().getResource( "filter_exists.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( Filters.from( queryFilter ) )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "boolean filter"()
    {
        given:
        BooleanFilter.Builder builder = BooleanFilter.create();

        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.must( ExistsFilter.create().fieldName( "MyMust" ).build() );
        builder.mustNot( ExistsFilter.create().fieldName( "MyMustNot" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );
        builder.should( ExistsFilter.create().fieldName( "MyOptional" ).build() );

        def expected = this.getClass().getResource( "filter_boolean.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( Filters.from( builder.build() ) )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }


}
