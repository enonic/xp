package com.enonic.xp.impl.scheduler;

import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;

interface ScheduleAuditLogSupport
{
    void create( CreateScheduledJobParams params, ScheduledJob job );

    void modify( ModifyScheduledJobParams params, ScheduledJob job );

    void delete( ScheduledJobName name, boolean result );
}
