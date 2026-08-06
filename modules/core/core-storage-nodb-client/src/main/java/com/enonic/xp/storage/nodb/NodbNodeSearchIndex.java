package com.enonic.xp.storage.nodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.proto.v1.AwaitRefreshRequest;
import com.enonic.nodb.proto.v1.DeleteDocumentsRequest;
import com.enonic.nodb.proto.v1.Explanation;
import com.enonic.nodb.proto.v1.HighlightedProperty;
import com.enonic.nodb.proto.v1.IndexDoc;
import com.enonic.nodb.proto.v1.IndexDocumentsRequest;
import com.enonic.nodb.proto.v1.IndexField;
import com.enonic.nodb.proto.v1.IndexValue;
import com.enonic.nodb.proto.v1.RepositoryExistsRequest;
import com.enonic.nodb.proto.v1.ReturnValue;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.sortvalues.SortValuesProperty;
import com.enonic.xp.storage.spi.IndexDocumentRecord;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.ReturnValues;
import com.enonic.xp.storage.spi.SearchHit;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.storage.spi.UpdateIndexSettings;

/**
 * gRPC-backed {@link NodeSearchIndex}: the search half of the nodb backend. Registered with
 * {@code storage.backend=nodb} and a positive {@code service.ranking}, so unmodified
 * core-repo consumers rebind to it — the same selection mechanism {@link NodbStorageClient}
 * documents for the storage half.
 * <p>
 * <b>Index lifecycle is not this class's job.</b> {@code RepositoryAdmin.CreateRepository}
 * already creates the repository's OpenSearch index and alias, and
 * {@code NodeRepositoryServiceImpl} calls the storage admin first, so by the time
 * {@link #createIndex} runs the index exists. Making it a second create would either race or
 * fail on ALREADY_EXISTS; it is therefore a documented no-op, as is {@link #deleteIndex}
 * (deletion is index-then-Postgres inside {@code DeleteRepository}).
 */
@Component(service = NodeSearchIndex.class, property = { "storage.backend=nodb", "service.ranking:Integer=100" })
public class NodbNodeSearchIndex
    implements NodeSearchIndex
{
    private static final Logger LOG = LoggerFactory.getLogger( NodbNodeSearchIndex.class );

    private final NodbStorageClient client;

    /**
     * The highest outbox sequence this JVM has produced per repository. {@code refresh} takes
     * no sequence — its contract is "make my writes visible" — so the sequence the write path
     * was handed is remembered here and awaited. A repository this instance has not written to
     * has nothing to wait for.
     */
    private final Map<String, Long> writtenSeq = new ConcurrentHashMap<>();

    @Activate
    public NodbNodeSearchIndex( @Reference final NodbStorageClient client )
    {
        this.client = client;
    }

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        final com.enonic.nodb.proto.v1.SearchRequest request = SearchEnvelopeSerializer.serialize( searchRequest );

        final com.enonic.nodb.proto.v1.SearchResult result =
            NodbStatusMapper.repoScoped( () -> client.nodeSearch().search( request ) );

        return decode( result );
    }

    @Override
    public void index( final RepositoryId repositoryId, final Branch branch, final IndexDocumentRecord doc )
    {
        final IndexDocumentsRequest request = IndexDocumentsRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .addDocuments( toProto( doc ) )
            .build();

        recordSeq( repositoryId, NodbStatusMapper.repoScoped( () -> client.nodeSearch().indexDocuments( request ) ).getOutboxSeq() );
    }

    @Override
    public void delete( final RepositoryId repositoryId, final Branch branch, final Collection<String> nodeIds )
    {
        if ( nodeIds.isEmpty() )
        {
            return;
        }

        final DeleteDocumentsRequest request = DeleteDocumentsRequest.newBuilder()
            .setRepoId( repositoryId.toString() )
            .setBranch( branch.getValue() )
            .addAllNodeIds( nodeIds )
            .build();

        recordSeq( repositoryId, NodbStatusMapper.repoScoped( () -> client.nodeSearch().deleteDocuments( request ) ).getOutboxSeq() );
    }

    /**
     * Unimplemented on purpose: the only route to this method is
     * {@code IndexDataService#get}, which has no callers anywhere in the codebase, so there
     * is no wire form to design against. It throws rather than returning empty values, so a
     * future caller is a loud failure instead of a silently empty node.
     */
    @Override
    public ReturnValues get( final RepositoryId repositoryId, final Branch branch, final String nodeId, final ReturnFields returnFields,
                             final SearchPreference searchPreference )
    {
        throw new UnsupportedOperationException(
            "NodeSearchIndex#get has no nodb wire form; it is unreachable in XP today (IndexDataService#get has no callers)" );
    }

    @Override
    public void refresh( final RepositoryId repositoryId )
    {
        final Long seq = writtenSeq.get( repositoryId.toString() );
        if ( seq == null )
        {
            return;
        }

        final AwaitRefreshRequest request =
            AwaitRefreshRequest.newBuilder().setSeq( seq ).addRepoIds( repositoryId.toString() ).build();

        NodbStatusMapper.repoScopedVoid( () -> client.nodeSearch().awaitRefresh( request ) );
    }

    /** No-op: {@code RepositoryAdmin.CreateRepository} already created the index — see class javadoc. */
    @Override
    public void createIndex( final RepositoryId repositoryId, final IndexSettings settings, final IndexMapping mapping )
    {
        LOG.debug( "createIndex is a no-op for the nodb backend (repository [{}]): the search index is created with the repository",
                   repositoryId );
    }

    /** No-op: {@code RepositoryAdmin.DeleteRepository} already deleted the index. */
    @Override
    public void deleteIndex( final RepositoryId repositoryId )
    {
        LOG.debug( "deleteIndex is a no-op for the nodb backend (repository [{}]): the search index dies with the repository",
                   repositoryId );
    }

    @Override
    public boolean indexExists( final RepositoryId repositoryId )
    {
        final RepositoryExistsRequest request = RepositoryExistsRequest.newBuilder().setRepoId( repositoryId.toString() ).build();
        return NodbStatusMapper.existsCheck( () -> client.repositoryAdmin().repositoryExists( request ).getExists() );
    }

    /** No-op: raw ES index settings (replicas, refresh_interval) are the search backend's own concern. */
    @Override
    public void updateSettings( final RepositoryId repositoryId, final UpdateIndexSettings settings )
    {
        LOG.debug( "updateSettings is a no-op for the nodb backend (repository [{}])", repositoryId );
    }

    private void recordSeq( final RepositoryId repositoryId, final long seq )
    {
        if ( seq > 0 )
        {
            writtenSeq.merge( repositoryId.toString(), seq, Math::max );
        }
    }

    /**
     * {@code Map.copyOf} in {@link IndexDocumentRecord} leaves iteration order unspecified, so
     * field names are sorted here: the indexer's bulk body has to be byte-deterministic.
     */
    private static IndexDoc toProto( final IndexDocumentRecord doc )
    {
        final IndexDoc.Builder builder = IndexDoc.newBuilder().setId( doc.id() );
        if ( doc.analyzer() != null )
        {
            builder.setAnalyzer( doc.analyzer() );
        }

        for ( final Map.Entry<String, Collection<Object>> field : new TreeMap<>( doc.fields() ).entrySet() )
        {
            final IndexField.Builder fieldBuilder = IndexField.newBuilder().setName( field.getKey() );
            for ( final Object value : field.getValue() )
            {
                fieldBuilder.addValues( toProto( value ) );
            }
            builder.addFields( fieldBuilder.build() );
        }

        return builder.build();
    }

    private static IndexValue toProto( final Object value )
    {
        final IndexValue.Builder builder = IndexValue.newBuilder();
        if ( value instanceof java.time.Instant )
        {
            builder.setInstantMillis( ( (java.time.Instant) value ).toEpochMilli() );
        }
        else if ( value instanceof Double || value instanceof Float )
        {
            builder.setDoubleValue( ( (Number) value ).doubleValue() );
        }
        else if ( value instanceof Number )
        {
            builder.setLongValue( ( (Number) value ).longValue() );
        }
        else if ( value instanceof Boolean )
        {
            builder.setBoolValue( (Boolean) value );
        }
        else
        {
            builder.setStringValue( String.valueOf( value ) );
        }
        return builder.build();
    }

    private static SearchResult decode( final com.enonic.nodb.proto.v1.SearchResult result )
    {
        final List<SearchHit> hits = new ArrayList<>( result.getHitsCount() );
        for ( final com.enonic.nodb.proto.v1.SearchHit hit : result.getHitsList() )
        {
            hits.add( decode( hit ) );
        }

        return SearchResult.create()
            .hits( hits )
            .totalHits( result.getTotalHits() )
            .maxScore( result.getMaxScore() )
            .build();
    }

    private static SearchHit decode( final com.enonic.nodb.proto.v1.SearchHit hit )
    {
        final SearchHit.Builder builder = SearchHit.create()
            .id( hit.getId() )
            .score( hit.getScore() )
            .indexName( hit.getRepoId() )
            .indexType( hit.getBranch() )
            .returnValues( decodeReturnValues( hit.getReturnValuesList() ) );

        if ( hit.getSortValuesCount() > 0 )
        {
            builder.sortValues( SortValuesProperty.create().values( hit.getSortValuesList().toArray() ).build() );
        }
        if ( hit.getHighlightsCount() > 0 )
        {
            final HighlightedProperties.Builder highlights = HighlightedProperties.create();
            for ( final HighlightedProperty highlight : hit.getHighlightsList() )
            {
                final com.enonic.xp.highlight.HighlightedProperty.Builder property =
                    com.enonic.xp.highlight.HighlightedProperty.create().name( highlight.getName() );
                highlight.getFragmentsList().forEach( property::addFragment );
                highlights.add( property.build() );
            }
            builder.highlightedFields( highlights.build() );
        }
        if ( hit.hasExplanation() )
        {
            builder.explanation( decode( hit.getExplanation() ) );
        }

        return builder.build();
    }

    private static ReturnValues decodeReturnValues( final List<ReturnValue> values )
    {
        final ReturnValues.Builder builder = ReturnValues.create();
        for ( final ReturnValue value : values )
        {
            for ( final IndexValue indexValue : value.getValuesList() )
            {
                builder.add( value.getName(), fromProto( indexValue ) );
            }
        }
        return builder.build();
    }

    private static Object fromProto( final IndexValue value )
    {
        switch ( value.getValueCase() )
        {
            case STRING_VALUE:
                return value.getStringValue();
            case DOUBLE_VALUE:
                return value.getDoubleValue();
            case LONG_VALUE:
                return value.getLongValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            case INSTANT_MILLIS:
                return java.time.Instant.ofEpochMilli( value.getInstantMillis() );
            default:
                return null;
        }
    }

    private static QueryExplanation decode( final Explanation explanation )
    {
        final QueryExplanation.Builder builder =
            QueryExplanation.create().value( explanation.getValue() ).description( explanation.getDescription() );
        for ( final Explanation detail : explanation.getDetailsList() )
        {
            builder.addDetail( decode( detail ) );
        }
        return builder.build();
    }
}
