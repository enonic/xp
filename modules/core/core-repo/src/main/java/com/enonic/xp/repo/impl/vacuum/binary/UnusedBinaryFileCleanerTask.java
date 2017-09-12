package com.enonic.xp.repo.impl.vacuum.binary;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class UnusedBinaryFileCleanerTask
    extends AbstractVacuumTask
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
        final BinaryNodeStateResolver stateResolver = new BinaryNodeStateResolver( getAllBinaryReferences() );
        final VacuumTaskResult.Builder result = VacuumTaskResult.create();
        doExecute( result, stateResolver, params.getAgeThreshold() );

        LOG.info( "Done" );
        return result.build();
    }

    private void doExecute( final VacuumTaskResult.Builder result, final BinaryNodeStateResolver stateResolver, final long ageThreshold )
    {
        LOG.info( "Traversing binary-folder....." );
        final List<BlobKey> toBeDeleted = Lists.newArrayList();

        this.blobStore.list( NodeConstants.BINARY_SEGMENT ).
            filter( rec -> includeRecord( rec, ageThreshold ) ).
            forEach( handleEntry( result, stateResolver, toBeDeleted ) );

        toBeDeleted.forEach( entry -> this.blobStore.removeRecord( NodeConstants.BINARY_SEGMENT, entry ) );
    }

    private Consumer<BlobRecord> handleEntry( final VacuumTaskResult.Builder result, final BinaryNodeStateResolver stateResolver,
                                              final List<BlobKey> toBeDeleted )
    {
        return record -> {
            final EntryState state = stateResolver.resolve( record.getKey().toString() );
            report( state, result );

            if ( state.equals( EntryState.NOT_IN_USE ) )
            {
                toBeDeleted.add( record.getKey() );
            }

        };
    }

    private Set<String> getAllBinaryReferences()
    {
        LOG.info( "Calculating all existing binary references....." );

        final Set<String> binaryReferences = Sets.newHashSet();

        this.blobStore.list( NodeConstants.NODE_SEGMENT ).forEach( record -> binaryReferences.addAll( getBinaryReference( record ) ) );

        return binaryReferences;
    }

    private Set<String> getBinaryReference( final BlobRecord record )
    {
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
