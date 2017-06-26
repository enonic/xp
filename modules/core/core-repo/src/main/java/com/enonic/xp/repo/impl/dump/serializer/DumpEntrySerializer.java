package com.enonic.xp.repo.impl.dump.serializer;

import com.enonic.xp.repo.impl.dump.model.DumpEntry;

public interface DumpEntrySerializer
{
    String serialize( final DumpEntry dumpEntry );

    DumpEntry deSerialize( final String value );
}
