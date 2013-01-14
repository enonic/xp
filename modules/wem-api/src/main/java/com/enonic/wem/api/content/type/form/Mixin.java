package com.enonic.wem.api.content.type.form;


import org.joda.time.DateTime;

import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.api.module.ModuleName;

public class Mixin
    implements BaseType
{
    private final FormItem formItem;

    private final ModuleName moduleName;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    Mixin( final Builder builder )
    {
        this.moduleName = builder.moduleName;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.formItem = builder.formItem;
    }

    public String getName()
    {
        return formItem.getName();
    }

    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public QualifiedMixinName getQualifiedName()
    {
        return new QualifiedMixinName( moduleName, getName() );
    }

    public FormItem getFormItem()
    {
        return formItem;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public FormItem toFormItem( final MixinReference mixinReference )
    {
        final FormItem newformItem = this.formItem.copy();
        newformItem.setName( mixinReference.getName() );

        if ( newformItem instanceof FormItemSet )
        {
            final FormItemSet newFormItemSet = (FormItemSet) newformItem;
            newFormItemSet.setPath( mixinReference.getPath() );
        }
        else if ( newformItem instanceof Input )
        {
            final Input newInput = (Input) newformItem;
            newInput.setPath( mixinReference.getPath() );
        }
        return newformItem;
    }

    public static Builder newMixin()
    {
        return new Builder();
    }

    public static Builder newMixin( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static class Builder
    {
        private String displayName;

        private ModuleName moduleName;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private FormItem formItem;

        public Builder()
        {

        }

        public Builder( final Mixin mixin )
        {
            this.displayName = mixin.displayName;
            this.moduleName = mixin.moduleName;
            this.createdTime = mixin.createdTime;
            this.modifiedTime = mixin.modifiedTime;
            this.formItem = mixin.formItem;
        }

        public Builder displayName( String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder module( ModuleName value )
        {
            this.moduleName = value;
            return this;
        }

        public Builder createdTime( DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder formItem( FormItem value )
        {
            this.formItem = value;
            return this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }


    }
}
