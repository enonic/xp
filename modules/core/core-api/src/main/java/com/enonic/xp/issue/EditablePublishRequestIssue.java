package com.enonic.xp.issue;

public final class EditablePublishRequestIssue
    extends EditableIssue
{
    public PublishRequestIssueSchedule schedule;

    public EditablePublishRequestIssue( final PublishRequestIssue source )
    {
        super( source );
        this.schedule = source.getSchedule();
    }

    @Override
    public Issue.Builder builder()
    {
        return PublishRequestIssue.create().
            id( source.getId() ).
            index( source.getIndex() ).
            name( name ).
            title( title ).
            description( description ).
            status( issueStatus ).
            modifiedTime( source.getModifiedTime() ).
            modifier( source.getModifier() ).
            createdTime( source.getCreatedTime() ).
            creator( source.getCreator() ).
            addApproverIds( approverIds ).
            setPublishRequest( publishRequest ).
            schedule( schedule );
    }
}
