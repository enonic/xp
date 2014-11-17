package com.enonic.wem.repo.internal.elasticsearch

import com.enonic.wem.api.index.IndexDocumentItemPath
import com.enonic.wem.repo.internal.elasticsearch.document.*
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

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
        new StoreDocumentStringItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" )   | "a_b_c"
        new StoreDocumentNumberItem( IndexDocumentItemPath.from( "a.b.c" ), 1 )            | "a_b_c._number"
        new StoreDocumentAnalyzedItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" ) | "a_b_c._analyzed"
        new StoreDocumentNGramItem( IndexDocumentItemPath.from( "a.b.c" ), "myString" )    | "a_b_c._ngram"
        new StoreDocumentGeoPointItem( IndexDocumentItemPath.from( "a.b.c" ), "80,80" )    | "a_b_c._geopoint"
        new StoreDocumentOrderbyItem( IndexDocumentItemPath.from( "a.b.c" ), "orderBy" )   | "a_b_c._orderby"
        new StoreDocumentDateItem( IndexDocumentItemPath.from( "a.b.c" ), Instant.now() )  | "a_b_c._datetime"
    }

    @Unroll
    def "resolve name from path #pathAsString"()
    {
        expect:
        resolvedName == FieldNameResolver.resolve( new StoreDocumentStringItem( IndexDocumentItemPath.from( pathAsString ), "myValue" ) )

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
