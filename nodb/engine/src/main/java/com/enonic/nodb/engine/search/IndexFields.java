package com.enonic.nodb.engine.search;

import java.util.Set;

/**
 * The nodb path's PHYSICAL field vocabulary — the one place where NoDB's index layout diverges
 * from XP's ES layout (BUILD-PHASE-4.md D1, D1b, D10).
 *
 * <p>This class is the whole divergence. XP ships documents in its own canonical vocabulary
 * (the nine documented index-value-type postfixes, of which {@code text} is the only one whose
 * postfix is empty); NoDB renames on the way in and resolves the same way on the way out, so
 * from Gate C the translator's field-name resolution and the indexer's document projection read
 * one table instead of two. XP's ES resources and code are TEXTUALLY UNTOUCHED — the
 * byte-identical rule is satisfied by construction, not by testing.
 *
 * <h2>{@code ""} → {@code _text} (D1 / BLOCKER 1)</h2>
 * {@code StaticIndexValueType.STRING} has postfix {@code ""} and {@code IndexItemFactory.create()}
 * ALWAYS emits the string variant, so XP produces a bare field alongside its dotted sub-fields:
 * {@code data.x} together with {@code data.x._fulltext}, {@code data.x._orderby}, … ES 2.4 with
 * {@code -Dmapper.allow_dots_in_name=true} treated those as independent FLAT names; OpenSearch
 * expands dots into an object hierarchy, so {@code data.x} would have to be a leaf AND an object
 * at once — {@code can't merge a non object mapping [data.x] with an object mapping}. It fires
 * on the FIRST document of every repo. Giving the text type a real postfix makes every emitted
 * field a sibling leaf under a consistent object tree.
 *
 * <p>{@code _text} rather than {@code _string}: the postfix set is XP's documented public
 * vocabulary (nine index types — text, number, datetime, geoPoint, ngram, analyzed, stemmed,
 * path, orderby) and eight of the nine already had matching postfixes. {@code text} was the only
 * gap, which is exactly this blocker. Note the vocabulary collision, which is a difference of
 * language and not an error: OpenSearch's {@code text} means ANALYZED, so XP's {@code _text} maps
 * to an engine {@code keyword}, while XP's {@code _fulltext} is itself an engine {@code text}.
 *
 * <p>Safe by XP's own rules: {@code .}, {@code _} and {@code []} are ILLEGAL in property keys
 * (dot IS the path separator, underscore is the system-reserved prefix), so no user property can
 * collide with a {@code _}-prefixed sub-field, and every dot in a physical name is a genuine
 * PropertyPath separator — the OpenSearch object tree therefore mirrors XP's PropertyTree
 * exactly.
 *
 * <h2>{@code _analyzed} → {@code _fulltext} (D1b)</h2>
 * Rides D1 at zero incremental cost: same table, same mapping file. The coherence evidence is
 * four-to-one — the index-config directive is {@code fulltext}, the query function is
 * {@code fulltext()}, and the dynamic template was already literally named
 * {@code template_fulltext}; only the physical field said {@code _analyzed}.
 *
 * <h2>Composite document id (D10)</h2>
 * See {@link #documentId}.
 */
public final class IndexFields
{
    /** The text value type's postfix on the nodb path (empty on the ES path). */
    public static final String TEXT_POSTFIX = "_text";

    /** The analyzed/fulltext value type's postfix on the nodb path ({@code _analyzed} on ES). */
    public static final String FULLTEXT_POSTFIX = "_fulltext";

    /** XP's postfix for the same thing, as it arrives on the wire. */
    public static final String XP_FULLTEXT_POSTFIX = "_analyzed";

    /** Every postfix XP may emit, in canonical (wire) form. Order is irrelevant; disjointness is not. */
    private static final Set<String> XP_POSTFIXES =
        Set.of( "_datetime", "_number", "_ngram", XP_FULLTEXT_POSTFIX, "_orderby", "_geopoint", "_path" );

    private static final String ORDERBY_LOCALE_PREFIX = "_orderby_";

    private static final String STEMMED_PREFIX = "_stemmed_";

    /**
     * The branch a document belongs to, as an ordinary {@code keyword} field.
     *
     * <p>Port-list item 3: ES 2.4 used ONE MAPPING TYPE PER BRANCH ({@code _type} = the branch
     * name), and mapping types are gone. Branch therefore becomes a field. The knock-on is
     * {@link #documentId}.
     *
     * <p>Not {@code _branch} spelled some other way and not a nested object: it must be
     * queryable as an exact term with the branch's ORIGINAL CASE preserved. Today's ES code
     * matches branch names with an {@code IdFilter} on {@code _type} specifically to avoid
     * {@code ValueFilter}'s lowercasing, so lowercasing here would be a silent behaviour change.
     */
    public static final String BRANCH = "_branch";

    /**
     * The repository a document belongs to, as a {@code keyword} field. Redundant within one
     * index, load-bearing across a multi-index query: hit attribution must ride explicit
     * response fields, because today's ES code derives repo by string-slicing {@code _index} and
     * branch from {@code _type}, and under generational names ({@code <tenant>-<repo>+g<N>}) that
     * breaks and violates DESIGN §5's "nothing parses a name back".
     */
    public static final String REPO = "_repo";

    /** XP's per-document analyzer override, same field name the ES path uses. */
    public static final String DOCUMENT_ANALYZER = "_document_analyzer";

    /**
     * The read-keys field, in CANONICAL form. Physically it becomes
     * {@code _permissions.read._text}, because XP emits the ACL entries as string-typed items
     * and {@code _permissions.read} is the {@code NodeIndexPath} constant (dot included — so
     * {@code _permissions} is an object and {@code read} an object under it, never a leaf, which
     * is why the ACL fields never hit blocker 1 themselves).
     */
    public static final String PERMISSIONS_READ = "_permissions.read";

    /**
     * The principal injected into every document's read keys (DESIGN §7.2 / Gate 0(b)).
     *
     * <p>Today {@code AclFilterBuilderFactory} returns null and applies NO FILTER AT ALL when
     * the principal set contains {@code role:system.admin} — and 22 non-test sites construct
     * admin contexts, so it fires constantly. The replacement is the inverse: the filter is
     * never absent, and the indexer injects the admin key into every document instead. That is a
     * genuine behaviour change with one sharp edge — a document missing the injected key (indexed
     * before a projection bump, or with an empty ACL) silently VANISHES from admin queries rather
     * than erroring. Which is why {@link IndexDocumentProjection#VERSION} is recorded per index
     * generation in {@code search_index}: "which projection built this generation" must be
     * answerable, and the fix for a bump must be a generational rebuild.
     */
    public static final String ADMIN_PRINCIPAL = "role:system.admin";

    /**
     * OpenSearch metadata field names that are rejected outright as document fields (ES 2.4
     * tolerated {@code _id} and {@code _source}: {@code failed to parse field [_id] of type
     * [_id]}). {@code NodeIndexPath} declares both, and {@code NodeStoreDocumentFactory} does not
     * currently emit either — this set makes that a checked invariant rather than an assumption
     * that holds until someone adds a field.
     */
    private static final Set<String> RESERVED = Set.of( "_id", "_source", "_index", "_type", "_routing", "_version", "_seq_no",
                                                        "_primary_term", "_field_names", "_ignored", "_nested_path", "_doc_count",
                                                        "_size", "_feature", "_tier", "_data_stream_timestamp" );

    private IndexFields()
    {
    }

    /**
     * Canonical (XP-shipped) field name → physical (OpenSearch) field name.
     *
     * <p>Two rules, applied to the last dot-separated segment only:
     * <ol>
     * <li>a name whose last segment is not a known postfix is the bare text variant → append
     *     {@code ._text};</li>
     * <li>a trailing {@code ._analyzed} becomes {@code ._fulltext}.</li>
     * </ol>
     * Everything else passes through unchanged.
     *
     * <p>The "last segment" test only counts when there IS a dot, and that is not a
     * micro-optimization. {@code IndexItem.getPath()} is {@code path + "." + postfix}, so a
     * postfix ALWAYS follows a dot — which means a dotless name can never be a postfixed field
     * even when it happens to spell one. {@code NodeIndexPath.PATH} is exactly that case: XP
     * emits both {@code _path} (the string variant) and {@code _path._path} (the path variant)
     * for the same value, and treating the bare {@code _path} as "already postfixed" would leave
     * it a leaf under an object of the same name — blocker 1, re-created by the very code meant
     * to fix it.
     */
    /**
     * The field an {@code exists} check must name: the OBJECT path, not a typed leaf.
     * <p>
     * Phase 4 Gate F (nodb/BUILD-PHASE-4.md). ES 2.4 answered {@code exists} from
     * {@code _field_names}, which contained OBJECT paths, so {@code exists(data.myField)} matched a
     * document whose {@code myField} is a PropertySet with any leaf inside it. Modern
     * {@code _field_names} holds leaves only, so resolving the name to {@code data.myfield._text}
     * (which is what the base-text rule does, and what {@code like} still needs) matched nothing at
     * all for a set -- silently, no error, the family of failure this phase keeps finding.
     * <p>
     * Naming the object path restores the ES-2.4 semantics exactly, for BOTH shapes, because of how
     * the nodb layout is built: a scalar {@code data.title} is stored only as its typed sub-fields
     * ({@code ._text}, {@code ._fulltext}, ...), so {@code data.title} is itself an object node, and
     * OpenSearch's {@code exists} on an object expands to "any leaf below it". A set with no leaves
     * has no such fields and correctly does not match. Server-injected leaves ({@code _branch},
     * {@code _repo}) and an explicitly postfixed name are passed through {@link #physicalName}
     * unchanged -- they ARE leaves.
     */
    public static String existsFieldName( String canonicalName )
    {
        String physical = physicalName( canonicalName );
        if ( isNodbInjected( canonicalName ) )
        {
            return physical;
        }
        int lastDot = canonicalName.lastIndexOf( '.' );
        String lastSegment = lastDot < 0 ? canonicalName : canonicalName.substring( lastDot + 1 );
        if ( XP_FULLTEXT_POSTFIX.equals( lastSegment ) || isPostfix( lastSegment ) )
        {
            return physical;
        }
        return canonicalName;
    }

    public static String physicalName( String canonicalName )
    {
        if ( canonicalName == null || canonicalName.isEmpty() )
        {
            throw new IllegalArgumentException( "Index document field name must not be empty" );
        }
        if ( RESERVED.contains( canonicalName ) )
        {
            throw new IllegalArgumentException(
                "Index document field '" + canonicalName + "' collides with an OpenSearch metadata field and cannot be indexed" );
        }
        if ( isNodbInjected( canonicalName ) )
        {
            return canonicalName;
        }

        int lastDot = canonicalName.lastIndexOf( '.' );
        if ( lastDot < 0 )
        {
            return canonicalName + "." + TEXT_POSTFIX;
        }

        String lastSegment = canonicalName.substring( lastDot + 1 );
        if ( XP_FULLTEXT_POSTFIX.equals( lastSegment ) )
        {
            return canonicalName.substring( 0, lastDot + 1 ) + FULLTEXT_POSTFIX;
        }
        if ( isPostfix( lastSegment ) )
        {
            return canonicalName;
        }
        return canonicalName + "." + TEXT_POSTFIX;
    }

    /** The locale code of an {@code *._orderby_<loc>} field, or {@code null} if it is not one. */
    public static String orderByLocale( String canonicalName )
    {
        int lastDot = canonicalName.lastIndexOf( '.' );
        if ( lastDot < 0 )
        {
            return null;
        }
        String lastSegment = canonicalName.substring( lastDot + 1 );
        return lastSegment.startsWith( ORDERBY_LOCALE_PREFIX ) ? lastSegment.substring( ORDERBY_LOCALE_PREFIX.length() ) : null;
    }

    /**
     * The composite search-document id (D10).
     *
     * <p>ES 2.4 used the bare nodeId, unique only PER MAPPING TYPE — and the type was the branch.
     * With one implicit type per index and one index per repo, a node's draft and master
     * documents would collide on the same {@code _id}, and the second write would simply
     * overwrite the first: silent cross-branch data loss, not an error. The id must therefore
     * carry the branch.
     *
     * <p>Separator {@code @}: it is legal in an OpenSearch {@code _id}, and it cannot appear in
     * either component — XP node ids are opaque but never contain {@code @} in practice, and
     * more importantly branch names are XP {@code Branch} values ({@code ^[a-zA-Z0-9\-:_]+$}),
     * so no branch name can contain it. That makes the id injective without needing to be
     * parseable, which it deliberately is not: {@link #BRANCH} and {@link #REPO} are the fields
     * that answer "where did this hit come from".
     */
    public static String documentId( String nodeId, String branch )
    {
        if ( nodeId == null || nodeId.isEmpty() )
        {
            throw new IllegalArgumentException( "Index document node id must not be empty" );
        }
        if ( branch == null || branch.isEmpty() )
        {
            throw new IllegalArgumentException( "Index document branch must not be empty" );
        }
        return nodeId + "@" + branch;
    }

    private static boolean isNodbInjected( String name )
    {
        return BRANCH.equals( name ) || REPO.equals( name ) || DOCUMENT_ANALYZER.equals( name );
    }

    private static boolean isPostfix( String segment )
    {
        return XP_POSTFIXES.contains( segment ) || segment.startsWith( ORDERBY_LOCALE_PREFIX ) || segment.startsWith( STEMMED_PREFIX ) ||
            TEXT_POSTFIX.equals( segment ) || FULLTEXT_POSTFIX.equals( segment );
    }
}
