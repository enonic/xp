package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;

@Component
public class IssueNotificationsSenderImpl
    implements IssueNotificationsSender
{
    private final MailService mailService;

    private final Executor executor;

    @Activate
    public IssueNotificationsSenderImpl( @Reference final MailService mailService,
                                         @Reference(service = IssueMailSendExecutor.class) final Executor executor )
    {
        this.mailService = mailService;
        this.executor = executor;
    }

    @Override
    public void notifyIssueCreated( final IssueNotificationParams params )
    {
        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueCreatedMailMessageGenerator( params ).generateMessage();
            if ( mailMessage != null )
            {
                executor.execute( () -> mailService.send( mailMessage ) );
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
                executor.execute( () -> mailService.send( mailMessage ) );
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
                executor.execute( () -> mailService.send( mailMessage ) );
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
                executor.execute( () -> mailService.send( mailMessage ) );
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
}
