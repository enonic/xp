package com.enonic.xp.impl.task;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import com.enonic.xp.app.ApplicationBundleUtils;

@Component
public class MemberAttributesApplier
    extends BundleTracker<Boolean>
{
    static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    static final String TASKS_ENABLED_ATTRIBUTE_PREFIX = TASKS_ENABLED_ATTRIBUTE_KEY + "-";

    private final Member localMember;

    @Activate
    public MemberAttributesApplier( final BundleContext context, @Reference final HazelcastInstance hazelcastInstance )
    {
        super( context, Bundle.ACTIVE, null );
        this.localMember = hazelcastInstance.getCluster().getLocalMember();
    }

    @Activate
    public void activate( final TaskConfig config )
    {
        localMember.setBooleanAttribute( TASKS_ENABLED_ATTRIBUTE_KEY, config.offload_acceptInbound() );
        super.open();
    }

    @Deactivate
    public void deactivate()
    {
        super.close();
        localMember.removeAttribute( TASKS_ENABLED_ATTRIBUTE_KEY );
    }

    @Modified
    public void modify( final TaskConfig config )
    {
        localMember.setBooleanAttribute( TASKS_ENABLED_ATTRIBUTE_KEY, config.offload_acceptInbound() );
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
