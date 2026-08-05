package com.enonic.nodb.engine.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Canonical XP-shipped document → the physical OpenSearch document the indexer sends.
 *
 * <p>Five things happen here, and every one of them is a recorded Phase-4 decision rather than
 * a formatting nicety:
 * <ol>
 * <li><b>Field renaming</b> ({@link IndexFields}): the bare text variant gains the {@code _text}
 *     postfix (D1 / blocker 1 — without it the FIRST document of every repo is rejected) and
 *     {@code ._analyzed} becomes {@code ._fulltext} (D1b).</li>
 * <li><b>ICU collation keys</b> (D8): each {@code *._orderby_<loc>} value is replaced by
 *     {@link CollationKeyResolver}'s hex key, so the field can be a plain sort-only
 *     {@code keyword} and the engine's own (older) ICU never touches the sort contract.</li>
 * <li><b>Read-keys injection</b> (DESIGN §7.2 / Gate 0(b)): {@code role:system.admin} is added to
 *     every document's {@code _permissions.read}. Today's ES path applies NO ACL FILTER AT ALL
 *     when the caller holds that role; the replacement makes the filter unconditional and puts
 *     the admin key on the documents instead.</li>
 * <li><b>Identity fields</b> (D10 / port item 3): {@code _branch} and {@code _repo}, because
 *     mapping types are gone and hit attribution must be explicit rather than string-sliced out
 *     of {@code _index}/{@code _type}.</li>
 * <li><b>Dot expansion</b>: XP's flat dotted keys are expanded into the object tree OpenSearch
 *     would build from them anyway. Doing it explicitly is what makes a leaf-vs-object collision
 *     surface HERE, as a clear exception naming both fields, instead of as OpenSearch's
 *     {@code can't merge a non object mapping} on the first document of a repo.</li>
 * </ol>
 *
 * <p><b>{@link #VERSION} is why this class is a named, versioned thing.</b> The admin-key
 * injection has a sharp edge: a document indexed WITHOUT the injected key does not error, it
 * silently vanishes from admin queries. So "which projection built this generation" must be
 * answerable, and it is — {@code search_index.projection_version} (migration 003) records it per
 * generation, and the remedy for a bump is a {@code +g(N+1)} rebuild from {@code search_document},
 * never a partial reindex. Bump {@link #VERSION} whenever the physical shape changes.
 */
public final class IndexDocumentProjection
{
    /**
     * v1 = D1 {@code _text} · D1b {@code _fulltext} · D8 hex ICU collation keys · unconditional
     * {@code role:system.admin} read key · {@code _branch}/{@code _repo} identity fields ·
     * composite {@code <nodeId>@<branch>} document id.
     */
    public static final int VERSION = 1;

    private IndexDocumentProjection()
    {
    }

    public static String documentId( SearchDocument document, String branch )
    {
        return IndexFields.documentId( document.nodeId(), branch );
    }

    public static ObjectNode project( SearchDocument document, String repoId, String branch )
    {
        Map<String, List<SearchDocument.Value>> physical = new LinkedHashMap<>();

        document.fields().forEach( ( canonicalName, values ) -> {
            String locale = IndexFields.orderByLocale( canonicalName );
            List<SearchDocument.Value> projected = locale == null ? values : collate( locale, values );
            physical.merge( IndexFields.physicalName( canonicalName ), projected, IndexDocumentProjection::concat );
        } );

        injectAdminReadKey( physical );

        physical.put( IndexFields.BRANCH, List.of( new SearchDocument.Value.Text( branch ) ) );
        physical.put( IndexFields.REPO, List.of( new SearchDocument.Value.Text( repoId ) ) );
        if ( document.analyzer() != null && !document.analyzer().isBlank() )
        {
            physical.put( IndexFields.DOCUMENT_ANALYZER, List.of( new SearchDocument.Value.Text( document.analyzer() ) ) );
        }

        return expand( physical );
    }

    /**
     * Adds {@link IndexFields#ADMIN_PRINCIPAL} to the read keys, creating the field if the node
     * has no read entries at all.
     *
     * <p>Creating it when absent is the whole point: an empty ACL is exactly the case that would
     * otherwise leave a node invisible to an admin query, and "invisible" is silent. The
     * injection is idempotent (a node whose ACL already grants the role does not get a duplicate
     * term), and it is done in CANONICAL space so the {@code _text} postfix is applied by the
     * same rule as everything else rather than hardcoded here.
     */
    private static void injectAdminReadKey( Map<String, List<SearchDocument.Value>> physical )
    {
        String readKeysField = IndexFields.physicalName( IndexFields.PERMISSIONS_READ );
        List<SearchDocument.Value> existing = physical.getOrDefault( readKeysField, List.of() );

        boolean alreadyPresent = existing.stream()
            .anyMatch( value -> value instanceof SearchDocument.Value.Text text &&
                IndexFields.ADMIN_PRINCIPAL.equalsIgnoreCase( text.value() ) );
        if ( alreadyPresent )
        {
            return;
        }

        List<SearchDocument.Value> withAdmin = new ArrayList<>( existing );
        withAdmin.add( new SearchDocument.Value.Text( IndexFields.ADMIN_PRINCIPAL ) );
        physical.put( readKeysField, List.copyOf( withAdmin ) );
    }

    private static List<SearchDocument.Value> collate( String locale, List<SearchDocument.Value> values )
    {
        List<SearchDocument.Value> collated = new ArrayList<>( values.size() );
        for ( SearchDocument.Value value : values )
        {
            if ( value instanceof SearchDocument.Value.Text text )
            {
                collated.add( new SearchDocument.Value.Text( CollationKeyResolver.collationKey( locale, text.value() ) ) );
            }
            else
            {
                // XP only ever ships strings on _orderby_<loc> (IndexItemFactory emits the
                // language variants only when the property value is a string), so this is
                // unreachable through the normal path. Pass it through rather than fail: a
                // non-string sort value is already lexi-encoded by OrderByValueResolver and
                // needs no collation.
                collated.add( value );
            }
        }
        return List.copyOf( collated );
    }

    private static List<SearchDocument.Value> concat( List<SearchDocument.Value> first, List<SearchDocument.Value> second )
    {
        List<SearchDocument.Value> merged = new ArrayList<>( first );
        merged.addAll( second );
        return List.copyOf( merged );
    }

    /**
     * Flat dotted keys → nested objects, with single values kept scalar and multi-values as
     * arrays (OpenSearch treats a scalar and a one-element array identically, so this is
     * cosmetic for the engine but keeps {@code _source} readable and diffable).
     */
    private static ObjectNode expand( Map<String, List<SearchDocument.Value>> physical )
    {
        ObjectNode root = OpenSearchClient.mapper().createObjectNode();
        for ( Map.Entry<String, List<SearchDocument.Value>> entry : physical.entrySet() )
        {
            String[] segments = entry.getKey().split( "\\." );
            ObjectNode parent = root;
            for ( int i = 0; i < segments.length - 1; i++ )
            {
                parent = objectChild( parent, entry.getKey(), segments[i] );
            }
            String leaf = segments[segments.length - 1];
            if ( parent.has( leaf ) && parent.get( leaf ).isObject() )
            {
                throw new IllegalArgumentException(
                    "Index document field '" + entry.getKey() + "' is both a leaf and an object; OpenSearch expands dots into objects, " +
                        "so the same name cannot be both (see IndexFields: this is what the _text postfix exists to prevent)" );
            }
            writeValues( parent, leaf, entry.getValue() );
        }
        return root;
    }

    private static ObjectNode objectChild( ObjectNode parent, String fullName, String segment )
    {
        if ( !parent.has( segment ) )
        {
            return parent.putObject( segment );
        }
        if ( !parent.get( segment ).isObject() )
        {
            throw new IllegalArgumentException(
                "Index document field '" + fullName + "' needs '" + segment + "' to be an object, but it is already a leaf value" );
        }
        return (ObjectNode) parent.get( segment );
    }

    private static void writeValues( ObjectNode parent, String leaf, List<SearchDocument.Value> values )
    {
        // Dedup while preserving order: XP's IndexItems already dedups _orderby, but the
        // canonical->physical rename can legitimately merge two source fields onto one physical
        // name, and a duplicated term changes term frequencies (i.e. scores).
        Set<SearchDocument.Value> distinct = new LinkedHashSet<>( values );
        if ( distinct.size() == 1 )
        {
            writeValue( parent, leaf, distinct.iterator().next() );
            return;
        }
        ArrayNode array = parent.putArray( leaf );
        for ( SearchDocument.Value value : distinct )
        {
            addValue( array, value );
        }
    }

    private static void writeValue( ObjectNode parent, String leaf, SearchDocument.Value value )
    {
        switch ( value )
        {
            case SearchDocument.Value.Text text -> parent.put( leaf, text.value() );
            case SearchDocument.Value.Number number -> parent.put( leaf, number.value() );
            case SearchDocument.Value.Integer integer -> parent.put( leaf, integer.value() );
            case SearchDocument.Value.Bool bool -> parent.put( leaf, bool.value() );
            case SearchDocument.Value.Timestamp timestamp -> parent.put( leaf, timestamp.epochMillis() );
        }
    }

    private static void addValue( ArrayNode array, SearchDocument.Value value )
    {
        switch ( value )
        {
            case SearchDocument.Value.Text text -> array.add( text.value() );
            case SearchDocument.Value.Number number -> array.add( number.value() );
            case SearchDocument.Value.Integer integer -> array.add( integer.value() );
            case SearchDocument.Value.Bool bool -> array.add( bool.value() );
            case SearchDocument.Value.Timestamp timestamp -> array.add( timestamp.epochMillis() );
        }
    }
}
