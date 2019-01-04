package com.enonic.xp.core.impl.content.page;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.page.CreatePageParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.security.User;

final class CreatePageCommand
{
    private final CreatePageParams params;

    private final ContentService contentService;

    private final PageDefaultValuesProcessor defaultValuesProcessor;

    private CreatePageCommand( Builder builder )
    {
        params = builder.params;
        contentService = builder.contentService;
        defaultValuesProcessor =
            new PageDefaultValuesProcessor( builder.pageDescriptorService, builder.partDescriptorService, builder.layoutDescriptorService,
                                            builder.formDefaultValuesProcessor );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        this.params.validate();

        final Page page = Page.create().
            descriptor( this.params.getController() ).
            template( this.params.getPageTemplate() ).
            config( this.params.getConfig() ).
            regions( this.params.getRegions() ).
            customized( this.params.isCustomized() ).
            build();

        defaultValuesProcessor.applyDefaultValues( page );

        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.params.getContent() ).
            modifier( getCurrentUser().getKey() ).
            editor( edit -> edit.page = page );

        return this.contentService.update( params );
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }

    public static final class Builder
    {
        private CreatePageParams params;

        private ContentService contentService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

        private Builder()
        {
        }

        public Builder params( CreatePageParams params )
        {
            this.params = params;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder pageDescriptorService( final PageDescriptorService pageDescriptorService )
        {
            this.pageDescriptorService = pageDescriptorService;
            return this;
        }

        public Builder partDescriptorService( final PartDescriptorService partDescriptorService )
        {
            this.partDescriptorService = partDescriptorService;
            return this;
        }

        public Builder layoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
        {
            this.layoutDescriptorService = layoutDescriptorService;
            return this;
        }

        public Builder formDefaultValuesProcessor( final FormDefaultValuesProcessor formDefaultValuesProcessor )
        {
            this.formDefaultValuesProcessor = formDefaultValuesProcessor;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentService );
            Preconditions.checkNotNull( pageDescriptorService );
            Preconditions.checkNotNull( partDescriptorService );
            Preconditions.checkNotNull( layoutDescriptorService );
            Preconditions.checkNotNull( formDefaultValuesProcessor );
            Preconditions.checkNotNull( params );
        }

        public CreatePageCommand build()
        {
            validate();
            return new CreatePageCommand( this );
        }
    }
}
