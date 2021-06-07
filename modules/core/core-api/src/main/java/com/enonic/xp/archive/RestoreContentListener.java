package com.enonic.xp.archive;

public interface RestoreContentListener
{
    void setTotal( int count );

    void contentRestored( int count );
}
