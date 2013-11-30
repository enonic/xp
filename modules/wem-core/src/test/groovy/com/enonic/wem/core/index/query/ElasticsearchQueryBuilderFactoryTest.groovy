package com.enonic.wem.core.index.query

import com.enonic.wem.query.parser.QueryParser
import spock.lang.Unroll

class ElasticsearchQueryBuilderFactoryTest extends BaseTestQueryBuilderFactory
{

    @Unroll
    def "create query #query"( )
    {
        given:
        def ElasticsearchQueryBuilderFactory factory = new ElasticsearchQueryBuilderFactory();

        expect:
        def expected = this.getClass().getResource( fileName ).text
        def expression = factory.create( query ).toString()

        cleanString( expected ) == cleanString( expression )


        where:
        fileName                        | query
        "function/fulltext_3_args.json" | QueryParser.parse( "fulltext('myField', 'my search phrase', 'or')" );
    }

}
