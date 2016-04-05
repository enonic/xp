package com.enonic.xp.core.impl.content.page.region;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.page.Page;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.region.TextComponentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.User;

final class CreateFragmentCommand
{
    private final CreateFragmentParams params;

    private final ContentService contentService;

    private CreateFragmentCommand( Builder builder )
    {
        params = builder.params;
        contentService = builder.contentService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final Component component = params.getComponent();
        final String componentName = component.getName().toString();
        String displayName = componentName;
        if ( component.getType() instanceof TextComponentType )
        {
            displayName = generateDisplayName( (TextComponent) component );
        }

        final String name = generateUniqueContentName( params.getParent(), "fragment-" + componentName );
        final CreateContentParams createContent = CreateContentParams.create().
            parent( params.getParent() ).
            displayName( displayName ).
            name( name ).
            type( ContentTypeName.fragment() ).
            contentData( new PropertyTree() ).
            build();
        final Content content = contentService.create( createContent );

        final Page page = Page.create().
            config( this.params.getConfig() ).
            fragment( this.params.getComponent() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( content.getId() ).
            modifier( getCurrentUser().getKey() ).
            editor( edit -> edit.page = page );

        return this.contentService.update( params );
    }

    private String generateDisplayName( final TextComponent textComponent )
    {
        final String html = textComponent.getText();
        String text = StringEscapeUtils.unescapeHtml( html.replaceAll( "\\<[^>]*>", "" ) ).trim();
        text = text.replaceAll( "(\\t|\\r?\\n)+", " " ).trim();
        return text.isEmpty() ? textComponent.getName().toString() : StringUtils.abbreviate( text, 40 );

    }

    private User getCurrentUser()
    {
        final Context context = ContextAccessor.current();
        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    private String generateUniqueContentName( final ContentPath parent, final String displayName )
    {
        final String baseName = NamePrettyfier.create( displayName );

        String name = baseName;
        int counter = 1;
        while ( this.contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            final String suffix = Integer.toString( ++counter );
            name = NamePrettyfier.create( baseName + "-" + suffix );
        }

        return name;
    }

    public static final class Builder
    {
        private CreateFragmentParams params;

        private ContentService contentService;

        private Builder()
        {
        }

        public Builder params( CreateFragmentParams params )
        {
            this.params = params;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentService );
            Preconditions.checkNotNull( params );
        }

        public CreateFragmentCommand build()
        {
            validate();
            return new CreateFragmentCommand( this );
        }
    }
}
