package com.enonic.nodb.engine.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The nodb-vs-ES field-name divergence, pinned (D1, D1b, D10). These are the exact shapes the
 * mapping's {@code path_match} patterns expect, so a change here without a matching change to
 * {@code index-template.json} is a silent zero-hits bug — the same class of failure as blocker 2.
 */
class IndexFieldsTest
{
    /** BLOCKER 1: the bare string variant that XP always emits gets a real postfix. */
    @Test
    void bareTextVariantGainsTheTextPostfix()
    {
        assertEquals( "data.title._text", IndexFields.physicalName( "data.title" ) );
        assertEquals( "data.a.b.c._text", IndexFields.physicalName( "data.a.b.c" ) );
    }

    /**
     * {@code NodeIndexPath.PATH} is the case that makes "a postfix always follows a dot"
     * load-bearing: XP emits {@code _path} (string variant) AND {@code _path._path} for the same
     * value, and treating the bare one as already-postfixed re-creates blocker 1 exactly.
     */
    @Test
    void dotlessSystemFieldsAreStillTextVariantsEvenWhenTheySpellAPostfix()
    {
        assertEquals( "_path._text", IndexFields.physicalName( "_path" ) );
        assertEquals( "_path._path", IndexFields.physicalName( "_path._path" ) );
        assertEquals( "_ts._text", IndexFields.physicalName( "_ts" ) );
        assertEquals( "_ts._datetime", IndexFields.physicalName( "_ts._datetime" ) );
        assertEquals( "_name._text", IndexFields.physicalName( "_name" ) );
    }

    /** D1b: the physical field finally agrees with the directive, the function and the template name. */
    @Test
    void analyzedBecomesFulltext()
    {
        assertEquals( "data.title._fulltext", IndexFields.physicalName( "data.title._analyzed" ) );
        assertEquals( "_allText._fulltext", IndexFields.physicalName( "_allText._analyzed" ) );
    }

    @Test
    void everyOtherXpPostfixPassesThroughUnchanged()
    {
        assertEquals( "data.n._number", IndexFields.physicalName( "data.n._number" ) );
        assertEquals( "data.d._datetime", IndexFields.physicalName( "data.d._datetime" ) );
        assertEquals( "data.g._geopoint", IndexFields.physicalName( "data.g._geopoint" ) );
        assertEquals( "data.t._ngram", IndexFields.physicalName( "data.t._ngram" ) );
        assertEquals( "data.t._orderby", IndexFields.physicalName( "data.t._orderby" ) );
        assertEquals( "data.t._orderby_no", IndexFields.physicalName( "data.t._orderby_no" ) );
        assertEquals( "data.t._stemmed_en", IndexFields.physicalName( "data.t._stemmed_en" ) );
        assertEquals( "data.t._stemmed_pt-br", IndexFields.physicalName( "data.t._stemmed_pt-br" ) );
    }

    /**
     * The ACL fields are the one place XP's own name already contains a dot, which is why they
     * never hit blocker 1: {@code _permissions} is only ever an object.
     */
    @Test
    void aclReadKeysBecomeADottedTextField()
    {
        assertEquals( "_permissions.read._text", IndexFields.physicalName( IndexFields.PERMISSIONS_READ ) );
    }

    @Test
    void nodbInjectedIdentityFieldsAreNotPostfixed()
    {
        assertEquals( IndexFields.BRANCH, IndexFields.physicalName( IndexFields.BRANCH ) );
        assertEquals( IndexFields.REPO, IndexFields.physicalName( IndexFields.REPO ) );
        assertEquals( IndexFields.DOCUMENT_ANALYZER, IndexFields.physicalName( IndexFields.DOCUMENT_ANALYZER ) );
    }

    /** ES 2.4 tolerated both; OpenSearch rejects them outright, so catch it here with a clear message. */
    @Test
    void openSearchMetadataFieldNamesAreRejected()
    {
        assertThrows( IllegalArgumentException.class, () -> IndexFields.physicalName( "_id" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.physicalName( "_source" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.physicalName( "_index" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.physicalName( "" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.physicalName( null ) );
    }

    @Test
    void orderByLocaleIsExtractedOnlyFromLocalisedOrderByFields()
    {
        assertEquals( "no", IndexFields.orderByLocale( "data.t._orderby_no" ) );
        assertEquals( "ducet", IndexFields.orderByLocale( "data.t._orderby_ducet" ) );
        assertEquals( "pt-br", IndexFields.orderByLocale( "data.t._orderby_pt-br" ) );
        assertNull( IndexFields.orderByLocale( "data.t._orderby" ) );
        assertNull( IndexFields.orderByLocale( "data.t" ) );
        assertNull( IndexFields.orderByLocale( "_orderby_no" ) );
    }

    /** D10: without the branch in the id, draft would silently overwrite master. */
    @Test
    void documentIdIsCompositeAndInjective()
    {
        assertEquals( "node-1@master", IndexFields.documentId( "node-1", "master" ) );
        assertEquals( "node-1@draft", IndexFields.documentId( "node-1", "draft" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.documentId( "node-1", "" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.documentId( "", "master" ) );
        assertThrows( IllegalArgumentException.class, () -> IndexFields.documentId( null, "master" ) );
    }
}
