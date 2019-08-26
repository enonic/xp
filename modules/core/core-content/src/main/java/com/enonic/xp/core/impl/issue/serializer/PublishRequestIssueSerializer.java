package com.enonic.xp.core.impl.issue.serializer;

import com.enonic.xp.core.impl.issue.PublishRequestIssuePropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.PublishRequestIssueSchedule;

public class PublishRequestIssueSerializer
{

    public void toCreateNodeData( final CreatePublishRequestIssueParams params, final PropertySet data )
    {
        insertSchedule( params.getSchedule(), data );
    }

    public void toUpdateNodeData( final PublishRequestIssue issue, final PropertySet data )
    {
        insertSchedule( issue.getSchedule(), data );
    }

    public void fromData( final PropertySet data, final PublishRequestIssue.Builder builder )
    {
        extractSchedule( data, builder );
    }

    private void insertSchedule( final PublishRequestIssueSchedule schedule, final PropertySet data )
    {
        if ( schedule != null )
        {
            final PropertySet scheduleSet = data.addSet( PublishRequestIssuePropertyNames.SCHEDULE );
            scheduleSet.addInstant( PublishRequestIssuePropertyNames.SCHEDULE_FROM, schedule.getFrom() );
            scheduleSet.addInstant( PublishRequestIssuePropertyNames.SCHEDULE_TO, schedule.getTo() );
        }
    }

    private void extractSchedule( final PropertySet data, final PublishRequestIssue.Builder builder )
    {
        final PropertySet scheduleSet = data.getSet( PublishRequestIssuePropertyNames.SCHEDULE );
        if ( scheduleSet != null )
        {
            final PublishRequestIssueSchedule schedule = PublishRequestIssueSchedule.create().
                from( scheduleSet.getInstant( PublishRequestIssuePropertyNames.SCHEDULE_FROM ) ).
                to( scheduleSet.getInstant( PublishRequestIssuePropertyNames.SCHEDULE_TO ) ).
                build();
            builder.schedule( schedule );
        }
    }
}
