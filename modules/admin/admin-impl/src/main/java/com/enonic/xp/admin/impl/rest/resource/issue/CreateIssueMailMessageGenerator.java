package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.text.StrSubstitutor;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Contents;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.security.User;

public class CreateIssueMailMessageGenerator
{
    private final Issue issue;

    private final User creator;

    private final List<User> approvers;

    private final Contents items;

    private final String url;

    private final CompareContentResults compareResults;

    public CreateIssueMailMessageGenerator( final Builder builder )
    {
        this.issue = builder.issue;
        this.url = builder.url;
        this.creator = builder.creator;
        this.approvers = builder.approvers;
        this.items = builder.items;
        this.compareResults = builder.compareResults;
    }

    public MailMessage generateMessage()
    {
        return msg ->
        {
            msg.setFrom( new InternetAddress( creator.getEmail(), creator.getEmail() ) );
            msg.setRecipients( Message.RecipientType.TO, generateRecipients() );
            msg.setSubject( generateMessageSubject() );
            msg.setContent( genereateMessageBody(), "text/html" );
        };
    }

    private String generateRecipients()
    {
        return approvers.stream().map( approver -> approver.getEmail() ).reduce( ( email1, email2 ) -> email1 + "," + email2 ).get();
    }

    private String generateMessageSubject()
    {
        return "You were assigned to a new issue \"" + issue.getTitle() + "\" (#" + issue.getId() + ")";
    }

    private String genereateMessageBody()
    {
        final Map params = Maps.newHashMap();
        params.put( "id", issue.getId().toString() );
        params.put( "idShort", issue.getId().toString().substring( 0, 9 ) );
        params.put( "creator", issue.getCreator().getId() );
        params.put( "issuesNum", issue.getPublishRequest().getItems().getSize() );
        params.put( "description", issue.getDescription() );
        params.put( "url", url + "#/issue/" + issue.getId().toString() );
        params.put( "items", generateItemsHtml() );

        return new StrSubstitutor( params ).replace( load( "email.html" ) );
    }

    private String load( final String name )
    {
        try
        {
            return Resources.toString( CreateIssueMailMessageGenerator.class.getResource( name ), Charsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new RuntimeException(
                "Cannot load resource with name [" + name + "] in [" + CreateIssueMailMessageGenerator.class.getPackage() + "]" );
        }
    }

    private String generateItemsHtml()
    {
        final String itemTemplate = load( "item.html" );

        final StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        for ( final Content item : items )
        {
            final boolean even = i % 2 == 0;
            stringBuilder.append( generateItemHtml( item, itemTemplate, even ) );
            i++;
        }

        return stringBuilder.toString();
    }

    private String generateItemHtml( final Content item, final String template, final boolean even )
    {
        final CompareStatus status = compareResults.getCompareContentResultsMap().get( item.getId() ).getCompareStatus();
        final boolean isOnline = status.equals( CompareStatus.EQUAL );

        final Map params = Maps.newHashMap();
        params.put( "displayName", item.getDisplayName() );
        params.put( "path", item.getPath() );
        params.put( "status", status.getFormattedStatus() );
        params.put( "bgcolor", even ? "#f5f5f5" : "initial" );
        params.put( "statusColor", isOnline ? "#609e24" : "initial" );

        return new StrSubstitutor( params ).replace( template );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Issue issue;

        private User creator;

        private List<User> approvers;

        private Contents items;

        private String url;

        private CompareContentResults compareResults;

        private Builder()
        {
        }

        public Builder issue( final Issue issue )
        {
            this.issue = issue;
            return this;
        }

        public Builder creator( final User creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder approvers( final List<User> approvers )
        {
            this.approvers = approvers;
            return this;
        }

        public Builder items( final Contents items )
        {
            this.items = items;
            return this;
        }

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public Builder compareResults( final CompareContentResults compareResults )
        {
            this.compareResults = compareResults;
            return this;
        }

        public CreateIssueMailMessageGenerator build()
        {
            return new CreateIssueMailMessageGenerator( this );
        }
    }

}
