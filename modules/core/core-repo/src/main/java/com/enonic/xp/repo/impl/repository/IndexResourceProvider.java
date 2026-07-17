package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;

public interface IndexResourceProvider
{
    IndexMapping getMapping( IndexType indexType );

    IndexSettings getSettings( IndexType indexType );
}
