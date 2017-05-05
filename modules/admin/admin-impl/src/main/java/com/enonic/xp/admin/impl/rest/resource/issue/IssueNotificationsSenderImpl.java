package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

@Component
public class IssueNotificationsSenderImpl
    implements IssueNotificationsSender
{
    private MailService mailService;

    private SecurityService securityService;

    private ContentService contentService;

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

    public void notifyIssueCreated( final Issue issue, final String url )
    {
        final User creator = securityService.getUser( issue.getCreator() ).get();
        final ContentIds contentIds = ContentIds.from(
            issue.getPublishRequest().getItems().stream().map( publishRequestItem -> publishRequestItem.getId() ).collect(
                Collectors.toList() ) );
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( contentIds ) );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );
        final List<User> approvers =
            issue.getApproverIds().stream().map( principalKey -> securityService.getUser( principalKey ).get() ).collect(
                Collectors.toList() );

        final MailMessage mailMessage =
            CreateIssueMailMessageGenerator.create().issue( issue ).creator( creator ).approvers( approvers ).items( contents ).url(
                url ).compareResults( compareResults ).build().generateMessage();

        sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
    }

    @Reference
    public void setMailService( final MailService mailService )
    {
        this.mailService = mailService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
