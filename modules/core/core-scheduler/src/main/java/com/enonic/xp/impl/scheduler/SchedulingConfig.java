package com.enonic.xp.impl.scheduler;

public @interface SchedulingConfig
{
    /**
     * Whether this node accepts running the scheduler. One member of a cluster ticks the schedule,
     * and it is chosen among the members that accept it - a node that does not is left alone by the
     * schedule, but still serves the API and holds the jobs like any other. Ignored when the
     * installation is not clustered: its single node has nowhere to hand the schedule over to.
     */
    boolean acceptScheduling() default true;
}
