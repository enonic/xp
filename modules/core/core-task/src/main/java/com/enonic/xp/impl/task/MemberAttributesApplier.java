package com.enonic.xp.impl.task;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.enonic.xp.app.ApplicationBundleUtils;

class MemberAttributesApplier
    extends BundleTracker<Boolean>
{
    static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    static final String SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY = "system-tasks-enabled";

    static final String TASKS_ENABLED_ATTRIBUTE_PREFIX = TASKS_ENABLED_ATTRIBUTE_KEY + "-";

    private final IMap<String, Object> localMember;

    MemberAttributesApplier( final BundleContext context, final HazelcastInstance hazelcastInstance )
    {
        super( context, Bundle.ACTIVE, null );
        this.localMember = hazelcastInstance.getMap("com.enonic.xp.impl.task");
    }

    public void activate( final TaskConfig config )
    {
        localMember.set( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ) );
        localMember.set( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) );
        super.open();
    }

    public void deactivate()
    {
        super.close();
        localMember.remove( TASKS_ENABLED_ATTRIBUTE_KEY );
        localMember.remove( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY );
    }

    public void modify( final TaskConfig config )
    {
        localMember.set( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ) );
        localMember.set( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) );
    }

    @Override
    public Boolean addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( ApplicationBundleUtils.isApplication( bundle ) )
        {
            localMember.setBooleanAttribute( TASKS_ENABLED_ATTRIBUTE_PREFIX + bundle.getSymbolicName(), true );
            return true;
        }

        return null;
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Boolean object )
    {
        localMember.removeAttribute( TASKS_ENABLED_ATTRIBUTE_PREFIX + bundle.getSymbolicName() );
    }
}
