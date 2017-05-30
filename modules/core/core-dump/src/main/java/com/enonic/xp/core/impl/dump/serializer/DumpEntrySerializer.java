package com.enonic.xp.core.impl.dump.serializer;

import com.enonic.xp.core.impl.dump.model.DumpEntry;

public interface DumpEntrySerializer
{
    String serialize( final DumpEntry dumpEntry );

}
