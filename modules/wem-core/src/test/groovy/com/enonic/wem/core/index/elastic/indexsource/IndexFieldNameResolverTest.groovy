package com.enonic.wem.core.index.elastic.indexsource

import com.enonic.wem.api.data.Value
import com.enonic.wem.core.index.document.*
import org.joda.time.DateTime
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class IndexFieldNameResolverTest extends Specification
{
    def "resolve name for item-type #item.getIndexBaseType()"( )
    {
        expect:
        resolvedName == IndexFieldNameResolver.resolve( item )

        where:
        item                                                                                                  | resolvedName
        new IndexDocumentStringItem( IndexDocumentItemPath.from( "a/b/c" ), "myString" )                      | "a_b_c"
        new IndexDocumentNumberItem( IndexDocumentItemPath.from( "a/b/c" ), 1 )                               | "a_b_c._number"
        new IndexDocumentAnalyzedItem( IndexDocumentItemPath.from( "a/b/c" ), "myString" )                    | "a_b_c._analyzed"
        new IndexDocumentTokenizedItem( IndexDocumentItemPath.from( "a/b/c" ), "myString" )                   | "a_b_c._tokenized"
        new IndexDocumentGeoPointItem( IndexDocumentItemPath.from( "a/b/c" ), new Value.GeoPoint( "80,80" ) ) | "a_b_c._geopoint"
        new IndexDocumentOrderbyItem( IndexDocumentItemPath.from( "a/b/c" ), "orderBy" )                      | "a_b_c._orderby"
        new IndexDocumentDateItem( IndexDocumentItemPath.from( "a/b/c" ), DateTime.now() )                    | "a_b_c._datetime"
    }


    def "resolve name from path #pathAsString"( )
    {
        expect:
        resolvedName == IndexFieldNameResolver.resolve( new IndexDocumentStringItem( IndexDocumentItemPath.from( pathAsString ), "myValue" ) )

        where:
        pathAsString | resolvedName
        "a"          | "a"
        "a/b"        | "a_b"
        "a/b/c"      | "a_b_c"
        "a/b/c/d"    | "a_b_c_d"
    }


}
