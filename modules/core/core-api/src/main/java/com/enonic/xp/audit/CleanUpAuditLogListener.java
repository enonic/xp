package com.enonic.xp.audit;

public interface CleanUpAuditLogListener
{
    void start( int batchSize );

    void processed();

    void finished();
}
