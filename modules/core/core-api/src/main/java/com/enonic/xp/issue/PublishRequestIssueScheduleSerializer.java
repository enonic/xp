package com.enonic.xp.issue;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

public class PublishRequestIssueScheduleSerializer
{
    private static final String DATA_SCHEDULE = "schedule";

    private static final String DATA_SCHEDULE_FROM = "from";

    private static final String DATA_SCHEDULE_TO = "to";

    public void toData( final PublishRequestIssueSchedule schedule, PropertyTree data )
    {
        if ( schedule == null )
        {
            return;
        }

        final PropertySet scheduleSet = data.addSet( DATA_SCHEDULE );
        scheduleSet.addInstant( DATA_SCHEDULE_FROM, schedule.getFrom() );
        scheduleSet.addInstant( DATA_SCHEDULE_TO, schedule.getFrom() );
    }

    public PublishRequestIssueSchedule fromData( final PropertyTree data )
    {
        final PropertySet scheduleSet = data.getSet( DATA_SCHEDULE );

        if ( scheduleSet == null )
        {
            return null;
        }

        return PublishRequestIssueSchedule.create().
            from( scheduleSet.getInstant( DATA_SCHEDULE_FROM ) ).
            to( scheduleSet.getInstant( DATA_SCHEDULE_TO ) ).
            build();
    }
}
