package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.text.StrSubstitutor;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.mail.MailMessage;

public abstract class IssueMailMessageGenerator<P extends IssueMailMessageParams>
{
    protected final P params;

    public IssueMailMessageGenerator( final P params )
    {
        this.params = params;
    }

    public MailMessage generateMessage()
    {
        return msg ->
        {
            msg.setFrom( new InternetAddress( getSender(), "Issue Manager" ) );
            msg.addRecipients( Message.RecipientType.TO, generateRecipients() );
            msg.addRecipients( Message.RecipientType.CC, getCopyRecepients() );
            msg.setSubject( generateMessageSubject() );
            msg.setContent( generateMessageBody(), "text/html" );
        };
    }

    protected abstract String generateMessageSubject();

    protected abstract String generateRecipients();

    protected abstract String getSender();

    protected abstract String getCopyRecepients();

    protected String getApproverEmails()
    {
        return params.getApprovers().stream().map( approver -> approver.getEmail() ).reduce(
            ( email1, email2 ) -> email1 + "," + email2 ).get();
    }

    protected String getCreatorEmail()
    {
        return params.getCreator().getEmail();
    }

    private String generateMessageBody()
    {
        final Map messageParams = Maps.newHashMap();
        final int itemCount = params.getIssue().getPublishRequest().getItems().getSize();
        final String description = params.getIssue().getDescription();

        messageParams.put( "id", params.getIssue().getId().toString() );
        messageParams.put( "index", params.getIssue().getIndex() );
        messageParams.put( "idShort", params.getIssue().getId().toString().substring( 0, 9 ) );
        messageParams.put( "title", params.getIssue().getTitle() );
        messageParams.put( "status", params.getIssue().getStatus() );
        messageParams.put( "statusBgColor", params.getIssue().getStatus() == IssueStatus.Open ? "#609e24" : "#777" );
        messageParams.put( "creator", params.getIssue().getCreator().getId() );
        messageParams.put( "issuesNum", itemCount );
        messageParams.put( "description", description );
        messageParams.put( "url", params.getUrl() + "#/issue/" + params.getIssue().getId().toString() );
        messageParams.put( "items", generateItemsHtml() );
        messageParams.put( "description-block-visibility", description.length() == 0 ? "none" : "block" );
        messageParams.put( "issue-block-visibility", itemCount == 0 ? "none" : "block" );
        messageParams.put( "no-issues-block-visibility", itemCount == 0 ? "block" : "none" );

        return new StrSubstitutor( messageParams ).replace( load( "email.html" ) );
    }

    private String load( final String name )
    {
        try
        {
            return Resources.toString( IssueMailMessageGenerator.class.getResource( name ), Charsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new RuntimeException(
                "Cannot load resource with name [" + name + "] in [" + IssueMailMessageGenerator.class.getPackage() + "]" );
        }
    }

    private String generateItemsHtml()
    {
        final String itemTemplate = load( "item.html" );

        final StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for ( final Content item : params.getItems() )
        {
            final boolean even = i % 2 == 0;
            stringBuilder.append( generateItemHtml( item, itemTemplate, even ) );
            i++;
        }

        return stringBuilder.toString();
    }

    private String generateItemHtml( final Content item, final String template, final boolean even )
    {
        final CompareStatus status = params.getCompareResults().getCompareContentResultsMap().get( item.getId() ).getCompareStatus();
        final boolean isOnline = status.equals( CompareStatus.EQUAL );

        final Map itemParams = Maps.newHashMap();
        itemParams.put( "displayName", item.getDisplayName() );
        itemParams.put( "path", item.getPath() );
        itemParams.put( "icon", params.getIcons().getOrDefault( item.getId(), "" ) );
        itemParams.put( "status", status.getFormattedStatus() );
        itemParams.put( "bgcolor", even ? "#f5f5f5" : "initial" );
        itemParams.put( "statusColor", isOnline ? "#609e24" : "initial" );

        return new StrSubstitutor( itemParams ).replace( template );
    }
}
