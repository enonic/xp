package com.enonic.xp.admin.impl.rest.resource.issue;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IssueNotificationsSenderImplTest
{
    private MailService mailService;

    private SecurityService securityService;

    private ContentService contentService;

    private IssueNotificationsSenderImpl issueNotificationsSender;

    private ContentTypeService contentTypeService;

    IssueNotificationParamsFactory.Builder notificationFactoryBuilder;

    @Before
    public void setUp()
    {
        mailService = Mockito.mock( MailService.class );
        securityService = Mockito.mock( SecurityService.class );
        contentService = Mockito.mock( ContentService.class );
        issueNotificationsSender = new IssueNotificationsSenderImpl();
        contentTypeService = Mockito.mock( ContentTypeService.class );

        issueNotificationsSender.setMailService( mailService );

        notificationFactoryBuilder =
            IssueNotificationParamsFactory.create().contentService( contentService ).securityService( securityService ).contentTypeService(
                contentTypeService );
    }

    @Test
    public void testNotifyIssueCreatedSingleApprover()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Contents contents = Contents.empty();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        IssueNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            createdParams();

        issueNotificationsSender.notifyIssueCreated( params );

        Thread.sleep( 1000 ); // giving a chance to run threads that send mails

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( mailService, times( 1 ) ).send( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testNotifyIssueCreatedMultipleApprovers()
        throws InterruptedException
    {
        final User creator = generateUser();
        final List<User> approvers = Arrays.asList( generateUser(), generateUser(), generateUser() );
        final PrincipalKeys approverIds =
            PrincipalKeys.from( approvers.stream().map( approver -> approver.getKey() ).collect( Collectors.toList() ) );
        final Issue issue = createIssue( creator.getKey(), approverIds );
        final Contents contents = Contents.empty();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        approvers.stream().forEach(
            approver -> Mockito.when( securityService.getUser( approver.getKey() ) ).thenReturn( Optional.of( approver ) ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        IssueNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            createdParams();

        issueNotificationsSender.notifyIssueCreated( params );

        Thread.sleep( 1000 ); // giving a chance to run threads that send mails

        verify( securityService, times( 4 ) ).getUser( Mockito.any() );
        verify( mailService, times( 1 ) ).send( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testNotifyIssueUpdated()
        throws Exception
    {
        final User creator = generateUser();
        final User approver = generateUserNoEmail();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = Content.create().
            id( ContentId.from( "aaa" ) ).
            type( ContentTypeName.folder() ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();
        final Contents contents = Contents.from( content );
        final CompareContentResults compareResults = CompareContentResults.create().
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "aaa" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "contentId2" ) ) ).
            build();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );
        Mockito.when( contentService.compare( Mockito.any( CompareContentsParams.class ) ) ).thenReturn( compareResults );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "image/svg+xml", Instant.now() ) ).setBuiltIn(
                true ).build() );

        IssueUpdatedNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            updatedParams();

        issueNotificationsSender.notifyIssueUpdated( params );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testNotifyIssueCommented()
        throws Exception
    {
        final User creator = generateUser();
        final User approver = generateUserNoEmail();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = Content.create().
            id( ContentId.from( "aaa" ) ).
            type( ContentTypeName.folder() ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();
        final Contents contents = Contents.from( content );
        final CompareContentResults compareResults = CompareContentResults.create().
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "aaa" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "contentId2" ) ) ).
            build();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );
        Mockito.when( contentService.compare( Mockito.any( CompareContentsParams.class ) ) ).thenReturn( compareResults );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "image/svg+xml", Instant.now() ) ).setBuiltIn(
                true ).build() );

        IssueCommentedNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            commentedParams();

        issueNotificationsSender.notifyIssueCommented( params );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testNotifyIssueUpdatedNotCalledNoRecipients()
        throws Exception
    {
        final User creator = generateUserNoEmail();
        final User approver = generateUserNoEmail();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Content content = Content.create().
            id( ContentId.from( "aaa" ) ).
            type( ContentTypeName.folder() ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();
        final Contents contents = Contents.from( content );
        final CompareContentResults compareResults = CompareContentResults.create().
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "aaa" ) ) ).
            add( new CompareContentResult( CompareStatus.NEW, ContentId.from( "contentId2" ) ) ).
            build();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );
        Mockito.when( contentService.compare( Mockito.any( CompareContentsParams.class ) ) ).thenReturn( compareResults );
        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn(
            ContentType.create().name( "mycontenttype" ).icon( Icon.from( new byte[]{1}, "image/svg+xml", Instant.now() ) ).setBuiltIn(
                true ).build() );

        IssueUpdatedNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            updatedParams();

        issueNotificationsSender.notifyIssueUpdated( params );

        verify( mailService, never() ).send( Mockito.any() );
    }

    @Test
    public void testNotifyIssuePublished()
        throws Exception
    {
        final User creator = generateUserNoEmail();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Contents contents = Contents.empty();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        IssuePublishedNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            publishedParams();

        issueNotificationsSender.notifyIssuePublished( params );

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( contentService, times( 1 ) ).getByIds( Mockito.any() );
        verify( contentService, times( 1 ) ).compare( Mockito.any( CompareContentsParams.class ) );
    }

    @Test
    public void testNotifyIssuePublishedNotCalledNoRecipients()
        throws Exception
    {
        final User creator = generateUserNoEmail();
        final User approver = generateUserNoEmail();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );
        final Contents contents = Contents.empty();

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.empty() );
        Mockito.when( contentService.getByIds( Mockito.any( GetContentByIdsParams.class ) ) ).thenReturn( contents );

        IssuePublishedNotificationParams params = notificationFactoryBuilder.
            issue( issue ).
            comments( this.createComments( creator.getKey() ) ).
            url( "url" ).
            build().
            publishedParams();

        issueNotificationsSender.notifyIssuePublished( params );

        verify( mailService, never() ).send( Mockito.any() );
    }

    private Issue createIssue( final PrincipalKey creator, final PrincipalKeys approvers )
    {
        return Issue.create().
            id( IssueId.create() ).
            title( "title" ).
            description( "description" ).
            creator( creator ).
            addApproverIds( approvers ).setPublishRequest( PublishRequest.create().addExcludeId( ContentId.from( "exclude-id" ) ).addItem(
            PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( true ).build() ).build() ).build();
    }

    private List<IssueComment> createComments( final PrincipalKey creator )
    {
        final IssueComment comment = IssueComment.create().
            text( "Comment One" ).
            creator( creator ).
            creatorDisplayName( "Creator" ).
            build();
        return Lists.newArrayList( comment );
    }

    private User generateUser()
    {
        final String userId = UUID.randomUUID().toString();
        return User.create().key( PrincipalKey.ofUser( UserStoreKey.createDefault(), userId ) ).login( userId ).email(
            "some@user.com" ).displayName( "Some User" ).build();
    }

    private User generateUserNoEmail()
    {
        final String userId = UUID.randomUUID().toString();
        return User.create().key( PrincipalKey.ofUser( UserStoreKey.createDefault(), userId ) ).login( userId ).displayName(
            "noemail" ).build();
    }
}
