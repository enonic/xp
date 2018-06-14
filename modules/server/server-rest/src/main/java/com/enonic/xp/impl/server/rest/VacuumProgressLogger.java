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

    private Segment segment;

    private RepositoryId repositoryId;

    private long total;

    private long currentCount;

    @Override
    public void vacuumingBlobSegment( final Segment segment )
    {
        this.lastProgressLogged = null;
        this.segment = segment;
        this.currentCount = 0;
        LOG.info( "Starting vacuum of blob store segment '" + segment + "'" );
    }

    @Override
    public void vacuumingBlob( final long count )
    {
        final Instant now = Instant.now();
        currentCount += count;

        if ( lastProgressLogged == null || Math.abs( ChronoUnit.SECONDS.between( now, lastProgressLogged ) ) > REPORTING_PERIOD_SEC )
        {
            lastProgressLogged = now;
            LOG.info( "Vacuum '" + segment + "' blob store: " + currentCount + " blobs processed" );
        }
    }

    @Override
    public void vacuumingVersionRepository( final RepositoryId repository, final long total )
    {
        this.lastProgressLogged = null;
        this.repositoryId = repository;
        this.total = total;
        this.currentCount = 0;
        LOG.info( "Starting of versions in repository '" + repositoryId + "'" );
    }

    @Override
    public void vacuumingVersion( final long count )
    {
        final Instant now = Instant.now();
        currentCount += count;

        if ( lastProgressLogged == null || ( count == total ) ||
            Math.abs( ChronoUnit.SECONDS.between( now, lastProgressLogged ) ) > REPORTING_PERIOD_SEC )
        {
            lastProgressLogged = now;
            LOG.info(
                "Vacuum Versions in repository '" + repositoryId + "': " + percentage( currentCount, total ) + "% versions processed" );
        }
    }

    private int percentage( long value, long total )
    {
        return (int) Math.floor( (double) value / (double) total * 100 );
    }
}
