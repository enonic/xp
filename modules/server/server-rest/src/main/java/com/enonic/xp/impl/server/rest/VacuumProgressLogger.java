package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.vacuum.VacuumListener;

public final class VacuumProgressLogger
    implements VacuumListener
{
    private final static Logger LOG = LoggerFactory.getLogger( VacuumProgressLogger.class );

    private final static int REPORTING_PERIOD_SEC = 30;

    private Instant lastProgressLogged;

    @Override
    public void vacuumTaskStarted( final String taskName, final int taskIndex, final int taskTotal )
    {
        LOG.info( "Starting Vacuum Task (" + taskIndex + " of " + taskTotal + "): " + taskName );
        lastProgressLogged = null;
    }

    @Override
    public void vacuumingBlob( final Segment segment, final long blobCount )
    {
        final Instant now = Instant.now();
        if ( lastProgressLogged == null || Math.abs( ChronoUnit.SECONDS.between( now, lastProgressLogged ) ) > REPORTING_PERIOD_SEC )
        {
            lastProgressLogged = now;
            LOG.info( "Vacuum '" + segment + "' blob store: " + blobCount + " blobs processed" );
        }
    }

    @Override
    public void vacuumingVersion( final RepositoryId repository, final long versionIndex, final long versionTotal )
    {
        final Instant now = Instant.now();
        if ( lastProgressLogged == null || ( versionIndex == versionTotal ) ||
            Math.abs( ChronoUnit.SECONDS.between( now, lastProgressLogged ) ) > REPORTING_PERIOD_SEC )
        {
            lastProgressLogged = now;
            LOG.info( "Vacuum Versions in repository '" + repository + "': " + percentage( versionIndex, versionTotal ) +
                          "% versions processed" );
        }
    }

    private int percentage( long value, long total )
    {
        return (int) Math.floor( (double) value / (double) total * 100 );
    }
}
