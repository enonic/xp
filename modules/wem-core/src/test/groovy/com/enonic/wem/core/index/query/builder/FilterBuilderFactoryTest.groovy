package com.enonic.wem.core.index.query.builder

import com.enonic.wem.api.data.Value
import com.enonic.wem.api.query.filter.BooleanFilter
import com.enonic.wem.api.query.filter.ExistsFilter
import com.enonic.wem.api.query.filter.FieldFilter
import org.elasticsearch.index.query.FilterBuilder

class FilterBuilderFactoryTest
    extends BaseTestBuilderFactory
{
    def "create string value filter"()
    {
        given:
        def queryFilter = FieldFilter.newValueQueryFilter().
            fieldName( "myField" ).
            add( Value.newString( "myValue1" ) ).
            add( Value.newString( "myValue2" ) ).
            build()
        def expected = this.getClass().getResource( "filter_values_string.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number value filter"()
    {
        given:
        def queryFilter = FieldFilter.newValueQueryFilter().
            fieldName( "myField" ).
            add( Value.newDouble( 1.0 ) ).
            add( Value.newDouble( 2.0 ) ).
            build()
        def expected = this.getClass().getResource( "filter_values_number.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number exists filter"()
    {
        given:
        def queryFilter = ExistsFilter.newExistsFilter( "myField" );
        def expected = this.getClass().getResource( "filter_exists.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "boolean filter"()
    {
        given:
        BooleanFilter.Builder builder = BooleanFilter.newBooleanFilter()

        builder.must( ExistsFilter.newExistsFilter( "MyMust" ) );
        builder.must( ExistsFilter.newExistsFilter( "MyMust" ) );
        builder.mustNot( ExistsFilter.newExistsFilter( "MyMustNot" ) );
        builder.should( ExistsFilter.newExistsFilter( "MyOptional" ) );
        builder.should( ExistsFilter.newExistsFilter( "MyOptional" ) );
        builder.should( ExistsFilter.newExistsFilter( "MyOptional" ) );

        def expected = this.getClass().getResource( "filter_boolean.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( builder.build() )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }


}
