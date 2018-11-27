package com.enonic.xp.schema.content;


import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.xdata.XDataNames;

@Beta
public final class ContentType
    extends BaseSchema<ContentTypeName>
{
    private final ContentTypeName superType;

    private final boolean isAbstract;

    private final boolean isFinal;

    private final boolean allowChildContent;

    private final boolean isBuiltIn;

    private final Form form;

    private final String displayNameExpression;

    private final XDataNames xData;

    ContentType( final Builder builder )
    {
        super( builder );

        if ( builder.superType == null && !builder.isBuiltIn )
        {
            throw new IllegalArgumentException( "Non-built-in content types must have a super type defined" );
        }
        else
        {
            this.superType = builder.superType;
        }
        this.isAbstract = builder.isAbstract;
        this.isFinal = builder.isFinal;
        this.allowChildContent = builder.allowChildContent;
        this.isBuiltIn = builder.isBuiltIn;
        this.form = builder.formBuilder != null ? builder.formBuilder.build() : Form.create().build();
        this.displayNameExpression = builder.displayNameExpression;
        this.xData = builder.xData;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ContentType contentType )
    {
        return new Builder( contentType );
    }

    public boolean hasSuperType()
    {
        return superType != null;
    }

    public ContentTypeName getSuperType()
    {
        return superType;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public boolean allowChildContent()
    {
        return allowChildContent;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }

    public Form getForm()
    {
        return this.form;
    }

    public String getDisplayNameExpression()
    {
        return displayNameExpression;
    }

    public XDataNames getXData()
    {
        return xData;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "displayName", getDisplayName() );
        s.add( "description", getDescription() );
        s.add( "metadata", xData );
        s.add( "superType", superType );
        s.add( "isAbstract", isAbstract );
        s.add( "isFinal", isFinal );
        s.add( "isBuiltIn", isBuiltIn );
        s.add( "allowChildContent", allowChildContent );
        s.add( "form", form );
        s.add( "icon", getIcon() );
        s.omitNullValues();
        return s.toString();
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, ContentTypeName>
    {
        private boolean isAbstract;

        private boolean isFinal;

        private boolean allowChildContent;

        private boolean isBuiltIn;

        private Form.Builder formBuilder = Form.create();

        private ContentTypeName superType;

        private String displayNameExpression;

        private XDataNames xData;

        private Builder()
        {
            super();
            formBuilder = Form.create();
            isAbstract = false;
            isFinal = true;
            allowChildContent = true;
            isBuiltIn = false;
            xData = XDataNames.empty();
        }

        private Builder( final ContentType source )
        {
            super( source );
            this.isAbstract = source.isAbstract();
            this.isFinal = source.isFinal();
            this.allowChildContent = source.allowChildContent();
            this.isBuiltIn = source.isBuiltIn();
            this.superType = source.getSuperType();
            if ( source.getForm() != null )
            {
                this.formBuilder = Form.create( source.getForm() );
            }
            this.displayNameExpression = source.getDisplayNameExpression();
            this.xData = source.xData;
        }

        @Override
        public Builder name( final ContentTypeName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( ContentTypeName.from( value ) );
            return this;
        }

        public Builder setAbstract( final boolean value )
        {
            isAbstract = value;
            return this;
        }

        public Builder setAbstract()
        {
            isAbstract = true;
            return this;
        }

        public Builder setFinal( final boolean value )
        {
            isFinal = value;
            return this;
        }

        public Builder setFinal()
        {
            isFinal = true;
            return this;
        }

        public Builder allowChildContent( final boolean value )
        {
            this.allowChildContent = value;
            return this;
        }

        public Builder setBuiltIn( final boolean builtIn )
        {
            isBuiltIn = builtIn;
            return this;
        }

        public Builder setBuiltIn()
        {
            return setBuiltIn( true );
        }

        public Builder superType( final ContentTypeName superType )
        {
            this.superType = superType;
            return this;
        }

        public Builder addFormItem( final FormItem formItem )
        {
            this.formBuilder.addFormItem( formItem );
            return this;
        }

        public Builder form( final Form form )
        {
            this.formBuilder = form != null ? Form.create( form ) : Form.create();
            return this;
        }

        public Builder displayNameExpression( final String displayNameExpression )
        {
            this.displayNameExpression = displayNameExpression;
            return this;
        }

        public Builder xData( final XDataNames xData )
        {
            this.xData = xData;
            return this;
        }

        public ContentType build()
        {
            return new ContentType( this );
        }
    }
}
