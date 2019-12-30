package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;

@Component
public class IssueNotificationsSenderImpl
    implements IssueNotificationsSender
{

    private MailService mailService;

    private ExecutorService sendMailExecutor;

    private final static Logger LOG = LoggerFactory.getLogger( IssueNotificationsSenderImpl.class );

    public IssueNotificationsSenderImpl()
    {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().
            setNameFormat( "issue-mail-sender-thread-%d" ).
            setUncaughtExceptionHandler( ( t, e ) -> LOG.error( "Message sending failed", e ) ).
            build();

        this.sendMailExecutor = Executors.newCachedThreadPool( threadFactory );
    }

    @Override
    public void notifyIssueCreated( final IssueNotificationParams params )
    {
        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueCreatedMailMessageGenerator( params ).generateMessage();
            if ( mailMessage != null )
            {
                sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
            }
        }
    }

    @Override
    public void notifyIssuePublished( final IssuePublishedNotificationParams params )
    {
        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssuePublishedMailMessageGenerator( params ).generateMessage();
            if ( mailMessage != null )
            {
                sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
            }
        }
    }

    @Override
    public void notifyIssueUpdated( final IssueUpdatedNotificationParams params )
    {
        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueUpdatedMailMessageGenerator( params ).generateMessage();
            if ( mailMessage != null )
            {
                sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
            }
        }
    }

    @Override
    public void notifyIssueCommented( final IssueCommentedNotificationParams params )
    {
        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueCommentedMailMessageGenerator( params ).generateMessage();
            if ( mailMessage != null )
            {
                sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
            }
        }
    }

    private boolean isRecipientsPresent( final IssueNotificationParams params )
    {
        if ( params.hasValidCreator() )
        {
            return true;
        }

        if ( params.getApprovers().isEmpty() )
        {
            return false;
        }

        return params.getApprovers().stream().anyMatch( user -> user.getEmail() != null );
    }

    @Reference
    public void setMailService( final MailService mailService )
    {
        this.mailService = mailService;
    }

}
