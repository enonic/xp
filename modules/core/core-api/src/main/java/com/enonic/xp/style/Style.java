package com.enonic.xp.style;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static com.google.common.base.Strings.nullToEmpty;

public abstract sealed class Style
    permits ImageStyle
{
    protected final String name;

    protected final String label;

    protected final String labelI18nKey;

    protected final GenericValue editor;

    protected Style( final Builder<?, ?> builder )
    {
        this.name = builder.name;
        this.label = builder.label;
        this.labelI18nKey = builder.labelI18nKey;
        this.editor = builder.editor.build();
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    public GenericValue getEditor()
    {
        return editor;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final Style that = (Style) o;
        return Objects.equals( name, that.name ) && Objects.equals( label, that.label ) &&
            Objects.equals( labelI18nKey, that.labelI18nKey ) && Objects.equals( editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, label, labelI18nKey, editor );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "name", name )
            .add( "label", label )
            .add( "labelI18nKey", labelI18nKey )
            .add( "editor", editor )
            .toString();
    }

    public abstract static class Builder<T extends Builder<T, R>, R extends Style>
    {
        private String name;

        private String label;

        private String labelI18nKey;

        private final GenericValue.ObjectBuilder editor = GenericValue.newObject();

        protected abstract T self();

        public T name( final String name )
        {
            this.name = name;
            return self();
        }

        public T label( final String label )
        {
            this.label = label;
            return self();
        }

        public T labelI18nKey( final String labelI18nKey )
        {
            this.labelI18nKey = labelI18nKey;
            return self();
        }

        public T label( final LocalizedText text )
        {
            this.label = text.text();
            this.labelI18nKey = text.i18n();
            return self();
        }

        public T editor( final GenericValue editor )
        {
            editor.properties().forEach( e -> this.editor.put( e.getKey(), e.getValue() ) );
            return self();
        }

        public final R build()
        {
            validate();
            return doBuild();
        }

        protected abstract R doBuild();

        protected void validate()
        {
            Preconditions.checkArgument( !nullToEmpty( this.name ).isBlank(), "name is required" );
        }
    }
}
