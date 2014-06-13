package com.enonic.wem.core.version;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.elasticsearch.ElasticsearchVersionService;

public class VersionModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( VersionService.class ).to( ElasticsearchVersionService.class ).in( Singleton.class );
    }
}
