package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

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
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public class IssueNotificationParamsFactory
{
    private final SchemaImageHelper HELPER = new SchemaImageHelper();

    private SecurityService securityService;

    private ContentService contentService;

    private ContentTypeIconResolver contentTypeIconResolver;

    private Issue issue;

    private List<IssueComment> comments;

    private PrincipalKeys recipients;

    private String url;

    private IssueNotificationParamsFactory( Builder builder )
    {
        this.securityService = builder.securityService;
        this.contentService = builder.contentService;
        this.contentTypeIconResolver = new ContentTypeIconResolver( builder.contentTypeService );
        this.issue = builder.issue;
        this.comments = builder.comments;
        this.recipients = builder.recipients;
        this.url = builder.url;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
    }

    public IssueNotificationParams createdParams()
    {
        return createMessageParams();
    }

    public IssueUpdatedNotificationParams updatedParams()
    {
        return IssueUpdatedNotificationParams.create( getCurrentUser(), createMessageParams() ).build();
    }

    public IssuePublishedNotificationParams publishedParams()
    {
        return IssuePublishedNotificationParams.create( getCurrentUser(), createMessageParams() ).build();
    }

    IssueCommentedNotificationParams commentedParams()
    {
        return IssueCommentedNotificationParams.create( getCurrentUser(), createMessageParams() ).build();
    }

    private IssueNotificationParams createMessageParams()
    {
        final User creator = securityService.getUser( issue.getCreator() ).orElse( null );
        final ContentIds contentIds = ContentIds.from(
            issue.getPublishRequest().getItems().stream().map( PublishRequestItem::getId ).collect( Collectors.toList() ) );
        final Contents contents = contentService.getByIds( new GetContentByIdsParams( contentIds ) );
        final CompareContentResults compareResults =
            contentService.compare( new CompareContentsParams( contentIds, ContentConstants.BRANCH_MASTER ) );
        final List<User> approvers = ( recipients != null ? recipients : issue.getApproverIds() ).
            stream().
            map( principalKey -> securityService.getUser( principalKey ).orElse( null ) ).
            filter( Objects::nonNull ).
            collect( Collectors.toList() );
        final Map<ContentId, String> icons = getIcons( contents );

        return IssueNotificationParams.create().
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
            final Icon icon = contentTypeIconResolver.resolveIcon( content.getType() );
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


    public static class Builder
    {
        private SecurityService securityService;

        private ContentService contentService;

        private ContentTypeService contentTypeService;

        private Issue issue;

        private List<IssueComment> comments;

        private PrincipalKeys recipients;

        private String url;

        private Builder()
        {
        }

        public Builder issue( final Issue issue )
        {
            this.issue = issue;
            return this;
        }

        public Builder comments( final List<IssueComment> comments )
        {
            this.comments = comments;
            return this;
        }

        public Builder recipients( final PrincipalKeys recipients )
        {
            this.recipients = recipients;
            return this;
        }

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public Builder securityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return this;
        }

        public Builder contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder contentTypeService( final ContentTypeService contentTypeService )
        {
            this.contentTypeService = contentTypeService;
            return this;
        }

        public IssueNotificationParamsFactory build()
        {
            return new IssueNotificationParamsFactory( this );
        }
    }
}
