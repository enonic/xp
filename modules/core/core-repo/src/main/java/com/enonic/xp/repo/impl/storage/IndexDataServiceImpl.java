package com.enonic.xp.repo.impl.storage;

import java.util.Collection;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.document.IndexDocument;
import com.enonic.xp.storage.spi.IndexDocumentRecord;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.ReturnValues;

/**
 * Thin adapter from the ES-free {@link IndexDataService} contract onto the
 * {@link NodeSearchIndex} storage SPI (Phase 0, Gate C — see {@code nodb/BUILD-PHASE-0.md}).
 * The only translation needed is flattening core-repo's {@link IndexDocument} (the real,
 * ES-multi-field-shaped document — see {@link IndexDocumentRecord}'s javadoc for why it does
 * NOT move into the SPI as-is) into the SPI's opaque {@link IndexDocumentRecord}.
 */
@Component
public class IndexDataServiceImpl
    implements IndexDataService
{
    private final NodeSearchIndex nodeSearchIndex;

    @Activate
    public IndexDataServiceImpl( @Reference final NodeSearchIndex nodeSearchIndex )
    {
        this.nodeSearchIndex = nodeSearchIndex;
    }

    @Override
    public ReturnValues get( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context )
    {
        return nodeSearchIndex.get( context.getRepositoryId(), context.getBranch(), nodeId.toString(), returnFields,
                                     SearchPreferences.toSpi( context.getSearchPreference() ) );
    }

    @Override
    public void delete( final Collection<NodeId> nodeIds, final InternalContext context )
    {
        nodeSearchIndex.delete( context.getRepositoryId(), context.getBranch(),
                                 nodeIds.stream().map( NodeId::toString ).collect( Collectors.toList() ) );
    }

    @Override
    public void store( final IndexDocument indexDocument, final InternalContext context )
    {
        nodeSearchIndex.index( context.getRepositoryId(), context.getBranch(), toRecord( indexDocument ) );
    }

    private static IndexDocumentRecord toRecord( final IndexDocument indexDocument )
    {
        return new IndexDocumentRecord( indexDocument.id(), indexDocument.analyzer(), indexDocument.data().asValuesMap() );
    }
}
