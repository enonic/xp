package com.enonic.xp.core.impl.content.page;

import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.page.EditablePage;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageNotFoundException;
import com.enonic.xp.page.UpdatePageParams;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;

final class UpdatePageCommand
{
    private final UpdatePageParams params;

    private final ContentService contentService;

    private final PageDefaultValuesProcessor defaultValuesProcessor;

    private UpdatePageCommand( Builder builder )
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
        final ContentId contentId = this.params.getContent();
        final Content content = this.contentService.getById( contentId );

        if ( content.getPage() == null )
        {
            throw new PageNotFoundException( contentId );
        }

        final EditablePage editablePage = new EditablePage( content.getPage() );
        this.params.getEditor().edit( editablePage );
        final Page editedPage = editablePage.build();

        if ( editedPage.equals( content.getPage() ) )
        {
            return content;
        }

        defaultValuesProcessor.applyDefaultValues( editedPage, content.getPage() );

        final UpdateContentParams params = new UpdateContentParams().
            contentId( contentId ).
            editor( edit -> edit.page = editedPage );

        return this.contentService.update( params );
    }

    public static final class Builder
    {
        private UpdatePageParams params;

        private ContentService contentService;

        private PageDescriptorService pageDescriptorService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private FormDefaultValuesProcessor formDefaultValuesProcessor;

        private Builder()
        {
        }

        public Builder params( UpdatePageParams params )
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
            Objects.requireNonNull( contentService );
            Objects.requireNonNull( pageDescriptorService );
            Objects.requireNonNull( partDescriptorService );
            Objects.requireNonNull( layoutDescriptorService );
            Objects.requireNonNull( formDefaultValuesProcessor );
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public UpdatePageCommand build()
        {
            validate();
            return new UpdatePageCommand( this );
        }
    }
}
