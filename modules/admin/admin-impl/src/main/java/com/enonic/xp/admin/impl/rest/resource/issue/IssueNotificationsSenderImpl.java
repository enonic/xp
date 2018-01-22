package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.enonic.xp.admin.impl.rest.resource.schema.SchemaImageHelper;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

@Component
public class IssueNotificationsSenderImpl
    implements IssueNotificationsSender
{
    private static final SchemaImageHelper HELPER = new SchemaImageHelper();

    private MailService mailService;

    private SecurityService securityService;

    private ContentService contentService;

    private ContentTypeIconResolver contentTypeIconResolver;

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
        final IssueMailMessageParams params = createMessageParams( issue, null, url );

        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueCreatedMailMessageGenerator( params ).generateMessage();
            sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
        }
    }

    public void notifyIssuePublished( final Issue issue, List<IssueComment> comments, final String url )
    {
        final User publisher = getCurrentUser();
        final IssuePublishedMailMessageParams params =
            IssuePublishedMailMessageParams.create( publisher, createMessageParams( issue, comments, url ) ).build();

        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssuePublishedMailMessageGenerator( params ).generateMessage();
            sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
        }
    }

    public void notifyIssueUpdated( final Issue issue, List<IssueComment> comments, final String url )
    {
        final User modifier = getCurrentUser();
        final IssueUpdatedMailMessageParams params =
            IssueUpdatedMailMessageParams.create( modifier, createMessageParams( issue, comments, url ) ).build();

        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueUpdatedMailMessageGenerator( params ).generateMessage();
            sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
        }
    }

    public void notifyIssueCommented( final Issue issue, List<IssueComment> comments, final String url )
    {
        final User modifier = getCurrentUser();
        final IssueCommentedMailMessageParams params =
            IssueCommentedMailMessageParams.create( modifier, createMessageParams( issue, comments, url ) ).build();

        if ( isRecipientsPresent( params ) )
        {
            final MailMessage mailMessage = new IssueCommentedMailMessageGenerator( params ).generateMessage();
            sendMailExecutor.execute( () -> mailService.send( mailMessage ) );
        }
    }

    private IssueMailMessageParams createMessageParams( final Issue issue, List<IssueComment> comments, final String url )
    {
        final User creator = securityService.getUser( issue.getCreator() ).orElse( null );
        final ContentIds contentIds = ContentIds.from(
            issue.getPublishRequest().getItems().stream().map( PublishRequestItem::getId ).collect( Collectors.toList() ) );
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( contentIds ) );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );
        final List<User> approvers =
            issue.getApproverIds().stream().map( principalKey -> securityService.getUser( principalKey ).orElse( null ) ).filter(
                Objects::nonNull ).collect( Collectors.toList() );
        final Map<ContentId, String> icons = getIcons( contents );

        return IssueMailMessageParams.create().
            issue( issue ).
            creator( creator ).
            approvers( approvers ).
            items( contents ).
            url( url ).
            icons( icons ).
            compareResults( compareResults ).
            comments( comments ).
            build();
    }

    private Map<ContentId, String> getIcons( final Contents contents )
    {
        final Map<ContentId, String> icons = Maps.newHashMap();

        contents.stream().forEach( content -> {
            final Icon icon = this.contentTypeIconResolver.resolveIcon( content.getType() );
            if ( icon != null && HELPER.isSvg( icon ) )
            {
                icons.put( content.getId(), new String( icon.toByteArray() ) );
            }
        } );

        return icons;
    }

    private User getCurrentUser()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    private boolean isRecipientsPresent( final IssueMailMessageParams params )
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

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
    }
}
