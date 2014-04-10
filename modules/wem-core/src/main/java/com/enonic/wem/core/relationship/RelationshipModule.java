package com.enonic.wem.core.relationship;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.relationship.RelationshipService;

public final class RelationshipModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RelationshipService.class ).to( RelationshipServiceImpl.class ).in( Singleton.class );
    }
}
