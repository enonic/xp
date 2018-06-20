package com.enonic.xp.repo.impl.vacuum.binary;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.AbstractVacuumTask;
import com.enonic.xp.repo.impl.vacuum.EntryState;
import com.enonic.xp.repo.impl.vacuum.VacuumException;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class UnusedBinaryFileCleanerTask
    extends AbstractVacuumTask
    implements VacuumTask
{
    private BlobStore blobStore;

    private static final Logger LOG = LoggerFactory.getLogger( UnusedBinaryFileCleanerTask.class );

    @Override
    public int order()
    {
        return 200;
    }

    @Override
    public String name()
    {
        return "UnusedBinaryFilesCleaner";
    }

    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        final VacuumTaskResult.Builder result = VacuumTaskResult.create().taskName( this.name() );

        final BinaryNodeStateResolver stateResolver = new BinaryNodeStateResolver( getAllBinaryReferences() );
        doExecute( result, stateResolver, params.getAgeThreshold(), params.getListener() );

        return result.build();
    }

    private void doExecute( final VacuumTaskResult.Builder result, final BinaryNodeStateResolver stateResolver, final long ageThreshold,
                            final VacuumListener listener )
    {
        LOG.info( "Traversing binary-folder....." );
        final List<BlobKey> toBeDeleted = Lists.newArrayList();

        if ( listener != null )
        {
            listener.vacuumingBlobSegment( NodeConstants.BINARY_SEGMENT );
        }

        this.blobStore.list( NodeConstants.BINARY_SEGMENT ).
            forEach( rec -> {
                if ( includeRecord( rec, ageThreshold ) )
                {
                    handleEntry( rec, result, stateResolver, toBeDeleted );
                }

                if ( listener != null )
                {
                    listener.vacuumingBlob( 1L );
                }
            } );

        toBeDeleted.forEach( entry -> this.blobStore.removeRecord( NodeConstants.BINARY_SEGMENT, entry ) );
    }

    private void handleEntry( final BlobRecord record, final VacuumTaskResult.Builder result, final BinaryNodeStateResolver stateResolver,
                              final List<BlobKey> toBeDeleted )
    {
        final EntryState state = stateResolver.resolve( record.getKey().toString() );
        report( state, result );

        if ( state.equals( EntryState.NOT_IN_USE ) )
        {
            toBeDeleted.add( record.getKey() );
        }
    }

    private Set<String> getAllBinaryReferences()
    {
        LOG.info( "Calculating all existing binary references....." );

        final Set<String> binaryReferences = Sets.newHashSet();

        this.blobStore.list( NodeConstants.NODE_SEGMENT ).
            forEach( record -> binaryReferences.addAll( getBinaryReference( record ) ) );

        return binaryReferences;
    }

    private Set<String> getBinaryReference( final BlobRecord record )
    {
        if ( record == null )
        {
            throw new VacuumException( "Record is null" );
        }

        final CharSource source = record.getBytes().asCharSource( Charsets.UTF_8 );

        try
        {
            return BinaryReferenceMatcher.matches( source.read() );
        }
        catch ( IOException e )
        {
            LOG.error( "Cannot check file [" + record.getKey() + "] for binary reference", e );
            throw new VacuumException( "Cannot check file [" + record.getKey() + "]", e );
        }
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

}
