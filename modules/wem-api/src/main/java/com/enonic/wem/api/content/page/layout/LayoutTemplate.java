package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public class LayoutTemplate
    extends Template<LayoutTemplateName>
{
    private LayoutTemplate( final Builder builder )
    {
        super( builder );
    }

    private LayoutTemplate( final LayoutTemplateProperties properties )
    {
        super( properties );
    }

    public static LayoutTemplate.Builder newLayoutTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, LayoutTemplateName>
    {
        private Builder()
        {
        }

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }

    public static class LayoutTemplateProperties
        extends TemplateProperties
    {

    }

    public static LayoutTemplateEditBuilder editLayoutTemplate( final LayoutTemplate toBeEdited )
    {
        return new LayoutTemplateEditBuilder( toBeEdited );
    }

    public static class LayoutTemplateEditBuilder
        extends LayoutTemplateProperties
        implements EditBuilder<LayoutTemplate>
    {
        private final LayoutTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private LayoutTemplateEditBuilder( LayoutTemplate original )
        {
            this.original = original;
        }

        public LayoutTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public LayoutTemplateEditBuilder descriptor( final ModuleResourceKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public LayoutTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }
}
