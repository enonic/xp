package com.enonic.wem.core.elasticsearch

import com.enonic.wem.core.index.document.*
import org.joda.time.DateTime
import spock.lang.Specification
import spock.lang.Unroll

class IndexFieldNameResolverTest
    extends Specification
{
    @Unroll
    def "resolve name for item-type #item.getIndexBaseType()"()
    {
        expect:
        resolvedName == FieldNameResolver.resolve( item )

        where:
        item                                                                               | resolvedName
        new IndexDocumentStringItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" )   | "a_b_c"
        new IndexDocumentNumberItem( IndexDocumentItemPath.from( "a.b.c" ), 1 )            | "a_b_c._number"
        new IndexDocumentAnalyzedItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" ) | "a_b_c._analyzed"
        new IndexDocumentNGramItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" )    | "a_b_c._ngram"
        new IndexDocumentGeoPointItem( IndexDocumentItemPath.from( "a.b.c" ), "80,80" )    | "a_b_c._geopoint"
        new IndexDocumentOrderbyItem( IndexDocumentItemPath.from( "a.b.c" ), "orderBy" )   | "a_b_c._orderby"
        new IndexDocumentDateItem( IndexDocumentItemPath.from( "a.b.c" ), DateTime.now() ) | "a_b_c._datetime"
    }

    @Unroll
    def "resolve name from path #pathAsString"()
    {
        expect:
        resolvedName ==
            FieldNameResolver.resolve( new IndexDocumentStringItem( IndexDocumentItemPath.from( pathAsString ), "myValue" ) )

        where:
        pathAsString | resolvedName
        "a"          | "a"
        "a.b"        | "a_b"
        "a.b.c"      | "a_b_c"
        "a.b.c.d"    | "a_b_c_d"
    }


    def "dummy"()
    {
        given:

        when:
        String test = "1"

        then:
        test == "1"
    }

}
