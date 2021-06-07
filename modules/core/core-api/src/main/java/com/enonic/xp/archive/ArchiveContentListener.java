package com.enonic.xp.archive;

public interface ArchiveContentListener
{
    void setTotal( int count );

    void contentArchived( int count );
}
