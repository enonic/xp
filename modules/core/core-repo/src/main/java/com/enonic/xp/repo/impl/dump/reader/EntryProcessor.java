package com.enonic.xp.repo.impl.dump.reader;

public interface EntryProcessor
{
    void processLine( String line );

    EntryLoadResult getResult();
}
