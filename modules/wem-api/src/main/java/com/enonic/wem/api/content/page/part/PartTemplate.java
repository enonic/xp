package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class PartTemplate
    extends Template<PartTemplateName>
{
    private PartTemplate( final Builder builder )
    {
        super( builder );
    }

    private PartTemplate( final PartTemplateProperties properties )
    {
        super( properties );
    }

    public static Builder newPartTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PartTemplateName>
    {
        private Builder()
        {
        }

        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }

    public static class PartTemplateProperties
        extends TemplateProperties
    {

    }

    public static PartTemplateEditBuilder editPartTemplate( final PartTemplate toBeEdited )
    {
        return new PartTemplateEditBuilder( toBeEdited );
    }

    public static class PartTemplateEditBuilder
        extends PartTemplateProperties
        implements EditBuilder<PartTemplate>
    {
        private final PartTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private PartTemplateEditBuilder( PartTemplate original )
        {
            this.original = original;
        }

        public PartTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public PartTemplateEditBuilder descriptor( final ModuleResourceKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public PartTemplateEditBuilder config( final RootDataSet value )
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

        public PartTemplate build()
        {
            return new PartTemplate( this );
        }
    }
}
