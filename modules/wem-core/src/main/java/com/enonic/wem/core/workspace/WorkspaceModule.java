package com.enonic.wem.core.workspace;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.elasticsearch.ElasticsearchWorkspaceService;

public final class WorkspaceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( WorkspaceService.class ).to( ElasticsearchWorkspaceService.class ).in( Singleton.class );
    }
}
