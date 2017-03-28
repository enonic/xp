package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public class IssueNotificationsSender
{
    private MailService mailService;

    private SecurityService securityService;

    private ExecutorService sendMailExecutor;

    private final static Logger LOG = LoggerFactory.getLogger( IssueNotificationsSender.class );

    public IssueNotificationsSender()
    {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().
            setNameFormat( "issue-mail-sender-thread-%d" ).
            setUncaughtExceptionHandler( ( t, e ) -> LOG.error( "Message sending failed", e ) ).
            build();

        this.sendMailExecutor = Executors.newCachedThreadPool( threadFactory );
    }

    public void notifyIssueCreated( final Issue issue )
    {
        final User creator = securityService.getUser( issue.getCreator() ).get();

        issue.getApproverIds().stream().map( principalKey -> securityService.getUser( principalKey ).get() ).forEach(
            approver -> sendMailExecutor.execute( () -> mailService.send( generateCreateIssueMessage( issue, creator, approver ) ) ) );
    }

    private MailMessage generateCreateIssueMessage( final Issue issue, final User creator, final User approver )
    {
        return msg ->
        {
            msg.setFrom( new InternetAddress( creator.getEmail(), creator.getEmail() ) );
            msg.setRecipients( Message.RecipientType.TO, approver.getEmail() );
            msg.setSubject( generateMessageSubject( issue ) );
            msg.setContent( genereateMessageBody( issue ), "text/html" );
        };
    }

    private String generateMessageSubject( final Issue issue )
    {
        return "You were assigned to a new issue \"" + issue.getTitle() + "\" (#" + issue.getId() + ")";
    }

    private String genereateMessageBody( final Issue issue )
    {
        return "<div>" + issue.getDescription() + "</div>" + "<div><button><span>Issue details</span></button><div>" +
            "<div>Issue Items (" + issue.getItemIds().getSize() + ")</div>";
    }

    public void setMailService( final MailService mailService )
    {
        this.mailService = mailService;
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
