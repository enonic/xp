package com.enonic.wem.api.schema.content.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public class MixinReference
    extends FormItem
{
    private final QualifiedMixinName qualifiedMixinName;

    private MixinReference( Builder builder )
    {
        super( builder.name );

        Preconditions.checkNotNull( builder.qualifiedMixinName, "qualifiedMixinName is required" );
        this.qualifiedMixinName = builder.qualifiedMixinName;
    }

    public QualifiedMixinName getQualifiedMixinName()
    {
        return qualifiedMixinName;
    }

    public static Builder newMixinReference()
    {
        return new Builder();
    }

    @Override
    public MixinReference copy()
    {
        return newMixinReference( this ).build();
    }

    public static Builder newMixinReference( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static Builder newMixinReference( final MixinReference mixinReference )
    {
        return new Builder( mixinReference );
    }

    public static class Builder
    {
        private String name;

        private QualifiedMixinName qualifiedMixinName;

        public Builder()
        {
            // default;
        }

        public Builder( MixinReference source )
        {
            this.name = source.getName();
            this.qualifiedMixinName = source.qualifiedMixinName;
        }

        public Builder( final Mixin mixin )
        {
            this.qualifiedMixinName = mixin.getQualifiedName();
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder mixin( final Mixin mixin )
        {
            this.qualifiedMixinName = mixin.getQualifiedName();
            return this;
        }

        public Builder mixin( String qualifiedName )
        {
            this.qualifiedMixinName = QualifiedMixinName.from( qualifiedName );
            return this;
        }

        public Builder mixin( QualifiedMixinName qualifiedName )
        {
            this.qualifiedMixinName = qualifiedName;
            return this;
        }

        public MixinReference build()
        {
            return new MixinReference( this );
        }
    }
}
