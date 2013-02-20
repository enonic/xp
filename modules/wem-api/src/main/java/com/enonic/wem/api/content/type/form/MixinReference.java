package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;

public class MixinReference
    extends HierarchicalFormItem
{
    private QualifiedMixinName qualifiedMixinName;

    private Class mixinClass;

    private Occurrences occurrences;

    private boolean immutable;

    protected MixinReference()
    {
        super();
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

    @Override
    public MixinReference copy()
    {
        MixinReference mixinReference = (MixinReference) super.copy();
        mixinReference.qualifiedMixinName = this.qualifiedMixinName;
        mixinReference.mixinClass = this.mixinClass;
        return mixinReference;
    }

    public static Builder newMixinReference( final Mixin mixin )
    {
        Builder builder = new Builder();
        builder.mixinClass = mixin.getFormItem().getClass().getSimpleName();
        builder.qualifiedMixinName = mixin.getQualifiedName();
        return builder;
    }

    public static class Builder
    {
        private QualifiedMixinName qualifiedMixinName;

        private String name;

        private String mixinClass;

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder mixin( final Mixin mixin )
        {
            this.qualifiedMixinName = mixin.getQualifiedName();
            this.mixinClass = mixin.getFormItem().getClass().getSimpleName();
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
            this.mixinClass = value;
            return this;
        }

        public Builder type( Class value )
        {
            this.mixinClass = value.getSimpleName();
            return this;
        }

        public Builder typeInput()
        {
            this.mixinClass = Input.class.getSimpleName();
            return this;
        }

        public Builder typeFormItemSet()
        {
            this.mixinClass = FormItemSet.class.getSimpleName();
            return this;
        }

        public MixinReference build()
        {
            Preconditions.checkNotNull( qualifiedMixinName, "qualifiedMixinName is required" );
            Preconditions.checkNotNull( mixinClass, "mixinClass is required" );
            Preconditions.checkNotNull( name, "name is required" );

            final MixinReference mixinReference = new MixinReference();
            mixinReference.setName( name );
            mixinReference.qualifiedMixinName = qualifiedMixinName;
            try

            {
                final String packageName = this.getClass().getPackage().getName();
                mixinReference.mixinClass = Class.forName( packageName + "." + mixinClass );
            }
            catch ( ClassNotFoundException e )
            {
                e.printStackTrace();
            }
            return mixinReference;
        }
    }
}
