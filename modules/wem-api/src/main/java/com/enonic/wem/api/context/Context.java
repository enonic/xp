package com.enonic.wem.api.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.session.Session;
import com.enonic.wem.api.workspace.Workspace;

public interface Context
{
    public RepositoryId getRepositoryId();

    public Workspace getWorkspace();

    public AuthenticationInfo getAuthInfo();

    public Session getSession();

    public Object getAttribute( String key );

    public <T> T getAttribute( Class<T> type );

    public Map<String, Object> getAttributes();

    public void runWith( Runnable runnable );

    public <T> T callWith( Callable<T> runnable );
}
