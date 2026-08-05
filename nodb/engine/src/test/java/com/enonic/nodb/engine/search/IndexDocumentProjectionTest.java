package com.enonic.nodb.engine.search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexDocumentProjectionTest
{
    private static SearchDocument document( Map<String, List<SearchDocument.Value>> fields )
    {
        return new SearchDocument( "node-1", null, fields );
    }

    private static Map<String, List<SearchDocument.Value>> fields( Object... namesAndValues )
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        for ( int i = 0; i < namesAndValues.length; i += 2 )
        {
            fields.put( (String) namesAndValues[i], List.of( new SearchDocument.Value.Text( (String) namesAndValues[i + 1] ) ) );
        }
        return fields;
    }

    /**
     * The whole blocker-1 fix in one assertion: the document that ES 2.4 accepted as two flat names
     * becomes an object with two sibling leaves, which is what OpenSearch can actually map.
     */
    @Test
    void bareAndSubFieldBecomeSiblingLeavesUnderOneObject()
    {
        ObjectNode projected = IndexDocumentProjection.project(
            document( fields( "data.title", "Hello", "data.title._analyzed", "Hello" ) ), "repo", "master" );

        assertTrue( projected.path( "data" ).path( "title" ).isObject() );
        assertEquals( "Hello", projected.path( "data" ).path( "title" ).path( "_text" ).asText() );
        assertEquals( "Hello", projected.path( "data" ).path( "title" ).path( "_fulltext" ).asText() );
    }

    @Test
    void systemPathFieldGetsBothVariantsWithoutCollision()
    {
        ObjectNode projected =
            IndexDocumentProjection.project( document( fields( "_path", "/a/b", "_path._path", "/a/b" ) ), "repo", "master" );

        assertEquals( "/a/b", projected.path( "_path" ).path( "_text" ).asText() );
        assertEquals( "/a/b", projected.path( "_path" ).path( "_path" ).asText() );
    }

    /** DESIGN §7.2 / Gate 0(b): the filter is never absent, so the key must be on every document. */
    @Test
    void adminReadKeyIsInjectedAlongsideExistingReadKeys()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( IndexFields.PERMISSIONS_READ, List.of( new SearchDocument.Value.Text( "role:system.everyone" ) ) );

        ObjectNode projected = IndexDocumentProjection.project( document( fields ), "repo", "master" );
        var readKeys = projected.path( "_permissions" ).path( "read" ).path( "_text" );

        assertTrue( readKeys.isArray() );
        assertEquals( List.of( "role:system.everyone", IndexFields.ADMIN_PRINCIPAL ),
                      List.of( readKeys.get( 0 ).asText(), readKeys.get( 1 ).asText() ) );
    }

    /** The sharp edge: an empty ACL is exactly the case that would silently vanish from admin queries. */
    @Test
    void adminReadKeyIsCreatedWhenTheNodeHasNoAclAtAll()
    {
        ObjectNode projected = IndexDocumentProjection.project( document( fields( "data.title", "Hello" ) ), "repo", "master" );
        assertEquals( IndexFields.ADMIN_PRINCIPAL, projected.path( "_permissions" ).path( "read" ).path( "_text" ).asText() );
    }

    @Test
    void adminReadKeyIsNotDuplicatedWhenTheAclAlreadyGrantsIt()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( IndexFields.PERMISSIONS_READ, List.of( new SearchDocument.Value.Text( IndexFields.ADMIN_PRINCIPAL ) ) );

        ObjectNode projected = IndexDocumentProjection.project( document( fields ), "repo", "master" );
        assertEquals( IndexFields.ADMIN_PRINCIPAL, projected.path( "_permissions" ).path( "read" ).path( "_text" ).asText() );
    }

    /** D8, at the projection level: the shipped plain string becomes a hex key. */
    @Test
    void localisedOrderByValuesAreReplacedByCollationKeys()
    {
        ObjectNode projected =
            IndexDocumentProjection.project( document( fields( "data.title._orderby", "ærlig", "data.title._orderby_no", "ærlig" ) ),
                                             "repo", "master" );

        assertEquals( "ærlig", projected.path( "data" ).path( "title" ).path( "_orderby" ).asText(),
                      "the plain _orderby field keeps XP's lexi-sortable value" );
        assertEquals( CollationKeyResolver.collationKey( "no", "ærlig" ),
                      projected.path( "data" ).path( "title" ).path( "_orderby_no" ).asText() );
    }

    /** D10 / port item 3: branch and repo are fields now, and they carry the branch's original case. */
    @Test
    void identityFieldsAreAddedWithBranchCasePreserved()
    {
        ObjectNode projected = IndexDocumentProjection.project( document( fields( "data.a", "x" ) ), "com.enonic.cms.default", "MyBranch" );
        assertEquals( "MyBranch", projected.path( IndexFields.BRANCH ).asText() );
        assertEquals( "com.enonic.cms.default", projected.path( IndexFields.REPO ).asText() );
    }

    @Test
    void documentAnalyzerIsCarriedOnlyWhenSet()
    {
        assertFalse( IndexDocumentProjection.project( document( fields( "data.a", "x" ) ), "repo", "master" )
                         .has( IndexFields.DOCUMENT_ANALYZER ) );
        assertEquals( "keywordlowercase",
                      IndexDocumentProjection.project( new SearchDocument( "n", "keywordlowercase", fields( "data.a", "x" ) ), "repo",
                                                        "master" ).path( IndexFields.DOCUMENT_ANALYZER ).asText() );
    }

    @Test
    void typedValuesSurviveTheProjection()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.n._number", List.of( new SearchDocument.Value.Number( 42.5 ) ) );
        fields.put( "data.d._datetime", List.of( new SearchDocument.Value.Timestamp( 1_700_000_000_000L ) ) );
        fields.put( "_manualOrderValue", List.of( new SearchDocument.Value.Integer( -7L ) ) );

        ObjectNode projected = IndexDocumentProjection.project( document( fields ), "repo", "master" );
        assertEquals( 42.5, projected.path( "data" ).path( "n" ).path( "_number" ).asDouble() );
        assertEquals( 1_700_000_000_000L, projected.path( "data" ).path( "d" ).path( "_datetime" ).asLong() );
        assertEquals( -7L, projected.path( "_manualOrderValue" ).path( "_text" ).asLong() );
    }

    @Test
    void multiValuedFieldsBecomeArraysAndAreDeduplicated()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.tag", List.of( new SearchDocument.Value.Text( "a" ), new SearchDocument.Value.Text( "b" ),
                                         new SearchDocument.Value.Text( "a" ) ) );

        var values = IndexDocumentProjection.project( document( fields ), "repo", "master" ).path( "data" ).path( "tag" ).path( "_text" );
        assertTrue( values.isArray() );
        assertEquals( 2, values.size() );
        assertEquals( "a", values.get( 0 ).asText() );
        assertEquals( "b", values.get( 1 ).asText() );
    }

    /**
     * A field that is both a leaf and an object cannot be represented, and the exception must name
     * both — this is the error OpenSearch would otherwise raise on the first document of a repo,
     * where it costs a debugging session instead of a stack trace.
     */
    @Test
    void aLeafAndObjectClashFailsLoudlyWithTheFieldNamed()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( IndexFields.BRANCH, List.of( new SearchDocument.Value.Text( "x" ) ) );
        fields.put( IndexFields.BRANCH + ".child", List.of( new SearchDocument.Value.Text( "y" ) ) );

        IllegalArgumentException failure =
            assertThrows( IllegalArgumentException.class, () -> IndexDocumentProjection.project( document( fields ), "repo", "master" ) );
        assertTrue( failure.getMessage().contains( IndexFields.BRANCH ), failure.getMessage() );
    }

    @Test
    void compositeDocumentIdIsDistinctPerBranch()
    {
        SearchDocument doc = document( fields( "data.a", "x" ) );
        assertEquals( "node-1@master", IndexDocumentProjection.documentId( doc, "master" ) );
        assertEquals( "node-1@draft", IndexDocumentProjection.documentId( doc, "draft" ) );
    }

    /** The stored canonical form must round-trip exactly, or a rebuild is not a rebuild. */
    @Test
    void canonicalDocumentRoundTripsThroughJsonb()
    {
        Map<String, List<SearchDocument.Value>> fields = new LinkedHashMap<>();
        fields.put( "data.s", List.of( new SearchDocument.Value.Text( "text" ) ) );
        fields.put( "data.n._number", List.of( new SearchDocument.Value.Number( 1.5 ) ) );
        fields.put( "data.l", List.of( new SearchDocument.Value.Integer( 9L ) ) );
        fields.put( "data.b", List.of( new SearchDocument.Value.Bool( true ) ) );
        fields.put( "data.d._datetime", List.of( new SearchDocument.Value.Timestamp( 123L ) ) );

        SearchDocument original = new SearchDocument( "node-1", "keywordlowercase", fields );
        SearchDocument restored = SearchDocument.fromJson( original.nodeId(), original.analyzer(), original.toJson() );

        assertEquals( original, restored );
        assertEquals( IndexDocumentProjection.project( original, "repo", "master" ),
                      IndexDocumentProjection.project( restored, "repo", "master" ) );
    }
}
