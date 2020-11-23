package com.enonic.xp.core.internal;

/**
 * Similar to OSGi R8 Condition.
 * Does not follow the standard, but allows using the Condition idea in general.
 */
public interface Condition
{
    /**
     * Service property identifying a condition's unique identifier.
     */
    String CONDITION_ID = "com.enonic.xp.condition.id";

    /**
     * A condition pre-existing instance that can be used to register {@code Condition} services.
     * <p>
     * {@code context.registerService(Condition.class, Condition.INSTANCE, ...);}
     */
    Condition INSTANCE = new Condition()
    {
    };
}
