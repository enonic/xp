package com.enonic.xp.core.impl.hazelcast;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.internal.Condition;

@Component(enabled = false, property = Condition.CONDITION_ID + "=HazelcastActivatorActivated")
public class HazelcastActivatorActivatedCondition
    implements Condition
{
}
