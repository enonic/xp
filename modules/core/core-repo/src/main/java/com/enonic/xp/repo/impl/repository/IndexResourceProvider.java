package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;

public interface IndexResourceProvider
{
    IndexMapping getMapping( IndexType indexType );

    IndexSettings getSettings( IndexType indexType );
}
