package com.enonic.wem.core.index.query

import com.enonic.wem.api.data.Value
import com.enonic.wem.query.queryfilter.QueryFilter
import org.elasticsearch.index.query.FilterBuilder

class FilterBuilderFactoryTest extends BaseTestQueryBuilderFactory
{
    def "create string value filter"( )
    {
        given:
        def queryFilter = QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.String( "myValue" ) ).
                add( new Value.String( "mySecondValue" ) ).
                build()
        def expected = this.getClass().getResource( "filter_values_string.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number value filter"( )
    {
        given:
        def queryFilter = QueryFilter.newValueQueryFilter().
                fieldName( "myField" ).
                add( new Value.Double( 1.0 ) ).
                add( new Value.Double( 2.0 ) ).
                build()
        def expected = this.getClass().getResource( "filter_values_number.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

    def "create number exists filter"( )
    {
        given:
        def queryFilter = QueryFilter.newExistsFilter( "myField" );
        def expected = this.getClass().getResource( "filter_exists.json" ).text
        FilterBuilderFactory factory = new FilterBuilderFactory();

        when:
        def FilterBuilder filterBuilder = factory.create( queryFilter )

        then:
        cleanString( expected ) == cleanString( filterBuilder.toString() )
    }

}
