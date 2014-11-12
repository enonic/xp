package com.enonic.wem.core.repository;

import com.enonic.wem.api.repository.Repository;

public interface RepositoryInitializer
{
    public void init( final Repository repository );

    public void waitForInitialized( Repository repository );

    public boolean isInitialized( Repository repository );
}
