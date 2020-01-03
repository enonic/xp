package com.enonic.xp.admin.impl.rest.resource.issue;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.text.StrSubstitutor;

import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.IssueType;
import com.enonic.xp.mail.MailMessage;

import static com.google.common.base.Strings.nullToEmpty;

public abstract class IssueMailMessageGenerator<P extends IssueNotificationParams>
{
    protected final P params;

    public IssueMailMessageGenerator( final P params )
    {
        this.params = params;
    }

    public MailMessage generateMessage()
    {
        final String sender = getSender();
        final String messageSubject = generateMessageSubject();
        final String messageBody = generateMessageBody();
        final String recipients = generateRecipients();

        if ( recipients.isBlank() )
        {
            return null;
        }

        return msg -> {
            msg.setFrom( new InternetAddress( sender, "Content Studio" ) );
            msg.addRecipients( Message.RecipientType.TO, recipients );
            msg.setSubject( messageSubject );
            msg.setContent( messageBody, "text/html; charset=UTF-8" );
        };
    }

    protected abstract String generateMessageSubject();

    protected abstract String generateMessageTitle();

    protected abstract String getSender();

    protected String generateRecipients()
    {
        return String.join( ",", this.getApproverEmails() );
    }

    protected boolean shouldShowComments()
    {
        return params.getComments().size() > 0;
    }

    protected String getIssueTypeText()
    {
        final boolean isPublishRequest = isPublishRequest();
        final String key = isPublishRequest ? "issue.email.publishRequest" : "issue.email.issue";
        final String defaultValue = isPublishRequest ? "publish request" : "issue";
        return params.getLocaleMessageResolver().localizeMessage( key, defaultValue );
    }

    protected boolean isIssueOpen()
    {
        return params.getIssue().getStatus() == IssueStatus.OPEN;
    }

    protected boolean isPublishRequest()
    {
        return params.getIssue().getIssueType() == IssueType.PUBLISH_REQUEST;
    }

    protected Set<String> getApproverEmails()
    {
        return params.getApprovers().stream().
            filter( approver -> !nullToEmpty( approver.getEmail() ).isBlank() ).
            map( approver -> approver.getEmail() ).
            collect( Collectors.toSet() );
    }

    protected void filterEmail( final Set<String> emails, final String email )
    {
        emails.removeIf( eml -> eml.equals( email ) );
    }

    protected String getCreatorEmail()
    {
        if ( !params.hasValidCreator() )
        {
            return "";
        }

        return params.getCreator().getEmail();
    }

    private String generateMessageBody()
    {
        final Map messageParams = new HashMap<>();
        final boolean showComments = this.shouldShowComments();
        final String description = params.getIssue().getDescription();
        final boolean isRequest = this.isPublishRequest();
        final boolean isOpen = isIssueOpen();
        final String idString = params.getIssue().getId().toString();
        final String showDetailsCaption =
            params.getLocaleMessageResolver().localizeMessage( "issue.email.showDetailsCaption", "Show Details..." );
        final String latestCommentTitle =
            params.getLocaleMessageResolver().localizeMessage( "issue.email.latestCommentTitle", "Latest comment" );

        messageParams.put( "id", idString );
        messageParams.put( "index", params.getIssue().getIndex() );
        messageParams.put( "display-issue-icon", isRequest ? "none" : "block" );
        messageParams.put( "display-request-icon", isRequest ? "inline-block" : "none" );
        messageParams.put( "icon-color", isOpen ? "#609E24" : "#777" );
        messageParams.put( "idShort", idString.substring( 0, 9 ) );
        messageParams.put( "title", generateMessageTitle() );
        messageParams.put( "status", params.getIssue().getStatus() );
        messageParams.put( "statusBgColor", isOpen ? "#2c76e9" : "#777" );
        messageParams.put( "creator", params.getIssue().getCreator().getId() );
        messageParams.put( "description", description );
        messageParams.put( "url", params.getUrl() + "#/issue/" + idString );
        messageParams.put( "description-block-visibility", description.length() == 0 ? "none" : "block" );
        messageParams.put( "comments-block-visibility", showComments ? "block" : "none" );
        messageParams.put( "comments", generateCommentsHtml() );
        messageParams.put( "showDetailsCaption", showDetailsCaption );
        messageParams.put( "latestCommentTitle", latestCommentTitle );

        return new StrSubstitutor( messageParams ).replace( load( "email.html" ) );
    }

    private String load( final String name )
    {
        final InputStream stream =
            Objects.requireNonNull( IssueMailMessageGenerator.class.getResourceAsStream( name ), "Resource file [" + name + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException(
                "Cannot load resource with name [" + name + "] in [" + IssueMailMessageGenerator.class.getPackage() + "]", e );
        }
    }

    private String generateCommentsHtml()
    {
        if ( params.getComments().size() == 0 )
        {
            return "";
        }
        final String commentTemplate = load( "comment.html" );
        final DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT );
        final IssueComment comment = params.getComments().get( 0 );

        return generateCommentHtml( comment, commentTemplate, fmt );
    }

    private String generateCommentHtml( final IssueComment item, final String template, final DateTimeFormatter fmt )
    {
        final Map itemParams = new HashMap<>();
        itemParams.put( "displayName", item.getCreatorDisplayName() );
        itemParams.put( "shortName", makeShortName( item.getCreatorDisplayName() ) );
        itemParams.put( "icon", item.getCreator() );
        itemParams.put( "createdTime", fmt.format( item.getCreated().atZone( ZoneId.systemDefault() ) ) );
        itemParams.put( "text", item.getText() );

        return new StrSubstitutor( itemParams ).replace( template );
    }

    private String makeShortName( final String displayName )
    {
        if ( displayName == null || displayName.length() == 0 )
        {
            return "";
        }
        else if ( displayName.length() == 1 )
        {
            return displayName;
        }

        final String[] nameParts = displayName.split( " " );

        if ( nameParts.length < 2 )
        {
            return displayName.substring( 0, 2 ).toUpperCase();
        }

        return Arrays.stream( nameParts ).map( namePart -> namePart.substring( 0, 1 ).toUpperCase() ).collect( Collectors.joining() );
    }
}
