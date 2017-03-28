package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IssueNotificationsSenderTest
{
    private MailService mailService;

    private SecurityService securityService;

    private IssueNotificationsSender issueNotificationsSender;

    @Before
    public void setUp()
    {
        mailService = Mockito.mock( MailService.class );
        securityService = Mockito.mock( SecurityService.class );
        issueNotificationsSender = new IssueNotificationsSender();

        issueNotificationsSender.setSecurityService( securityService );
        issueNotificationsSender.setMailService( mailService );
    }

    @Test
    public void testNotifyIssueCreatedSingleApprover()
        throws InterruptedException
    {
        final User creator = generateUser();
        final User approver = generateUser();
        final Issue issue = createIssue( creator.getKey(), PrincipalKeys.from( approver.getKey() ) );

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        Mockito.when( securityService.getUser( issue.getApproverIds().first() ) ).thenReturn( Optional.of( approver ) );

        issueNotificationsSender.notifyIssueCreated( issue );

        Thread.sleep( 1000 ); // giving a chance to run threads that send mails

        verify( securityService, times( 2 ) ).getUser( Mockito.any() );
        verify( mailService, times( 1 ) ).send( Mockito.any() );
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

        Mockito.when( securityService.getUser( issue.getCreator() ) ).thenReturn( Optional.of( creator ) );
        approvers.stream().forEach(
            approver -> Mockito.when( securityService.getUser( approver.getKey() ) ).thenReturn( Optional.of( approver ) ) );

        issueNotificationsSender.notifyIssueCreated( issue );

        Thread.sleep( 1000 ); // giving a chance to run threads that send mails

        verify( securityService, times( 4 ) ).getUser( Mockito.any() );
        verify( mailService, times( 3 ) ).send( Mockito.any() );
    }

    private Issue createIssue( final PrincipalKey creator, final PrincipalKeys approvers )
    {
        return Issue.create().id( IssueId.create() ).title( "title" ).description( "description" ).addItemId(
            ContentId.from( "itemId" ) ).creator( creator ).addApproverIds( approvers ).build();
    }

    private User generateUser()
    {
        final String userId = UUID.randomUUID().toString();
        return User.create().key( PrincipalKey.ofUser( UserStoreKey.createDefault(), userId ) ).login( userId ).email(
            userId + "@gmail.com" ).build();
    }
}
