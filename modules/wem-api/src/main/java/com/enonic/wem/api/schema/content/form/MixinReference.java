package com.enonic.wem.api.schema.content.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public class MixinReference
    extends FormItem
{
    private final QualifiedMixinName qualifiedMixinName;

    private final Class mixinClass;

    private final Occurrences occurrences;

    private final boolean immutable;

    private MixinReference( Builder builder )
    {
        super( builder.name );

        Preconditions.checkNotNull( builder.qualifiedMixinName, "qualifiedMixinName is required" );
        this.qualifiedMixinName = builder.qualifiedMixinName;

        Preconditions.checkNotNull( builder.mixinClass, "mixinClass is required" );
        Preconditions.checkArgument( builder.mixinClass.equals( Input.class ) || builder.mixinClass.equals( FormItemSet.class ),
                                     "mixinClass must be of type Input or FormItemSet" );
        mixinClass = builder.mixinClass;

        this.occurrences = builder.occurrences;
        this.immutable = builder.immutable;
    }

    public QualifiedMixinName getQualifiedMixinName()
    {
        return qualifiedMixinName;
    }

    public Class getMixinClass()
    {
        return mixinClass;
    }

    public static Builder newMixinReference()
    {
        return new Builder();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    @Override
    public MixinReference copy()
    {
        return newMixinReference( this ).build();
    }

    public static Class resolveMixinClass( final String classSimpleName )
    {
        final String packageName = MixinReference.class.getPackage().getName();
        final String className = packageName + "." + classSimpleName;
        try
        {
            return Class.forName( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new IllegalArgumentException( "Mixin class not found: " + className, e );
        }
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

        private Class mixinClass;

        private Occurrences occurrences;

        private boolean immutable;

        public Builder()
        {
            // default;
        }

        public Builder( MixinReference source )
        {
            this.name = source.getName();
            this.qualifiedMixinName = source.qualifiedMixinName;
            this.mixinClass = source.mixinClass;
            this.occurrences = source.occurrences;
            this.immutable = source.immutable;
        }

        public Builder( final Mixin mixin )
        {
            this.mixinClass = mixin.getFormItem().getClass();
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
            this.mixinClass = mixin.getFormItem().getClass();
            return this;
        }

        public Builder mixin( String qualifiedName )
        {
            this.qualifiedMixinName = new QualifiedMixinName( qualifiedName );
            return this;
        }

        public Builder mixin( QualifiedMixinName qualifiedName )
        {
            this.qualifiedMixinName = qualifiedName;
            return this;
        }

        public Builder type( String value )
        {
            this.mixinClass = resolveMixinClass( value );
            return this;
        }

        public Builder type( Class value )
        {
            this.mixinClass = value;
            return this;
        }

        public Builder typeInput()
        {
            this.mixinClass = Input.class;
            return this;
        }

        public Builder typeFormItemSet()
        {
            this.mixinClass = FormItemSet.class;
            return this;
        }

        public Builder occurrences( final Occurrences occurrences )
        {
            this.occurrences = occurrences;
            return this;
        }

        public Builder immutable( final boolean value )
        {
            this.immutable = value;
            return this;
        }

        public MixinReference build()
        {
            return new MixinReference( this );
        }
    }
}
