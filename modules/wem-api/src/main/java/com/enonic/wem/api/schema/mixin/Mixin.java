package com.enonic.wem.api.schema.mixin;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.content.form.FormItem;

public class Mixin
    implements Schema
{
    private final FormItem formItem;

    private final ModuleName moduleName;

    private final QualifiedMixinName qualifiedName;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final Icon icon;

    private Mixin( final Builder builder )
    {
        this.moduleName = builder.moduleName;
        this.formItem = builder.formItem;
        this.qualifiedName = new QualifiedMixinName( moduleName, getName() );
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.icon = builder.icon;
    }

    @Override
    public String getName()
    {
        return formItem.getName();
    }

    @Override
    public ModuleName getModuleName()
    {
        return moduleName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public QualifiedMixinName getQualifiedName()
    {
        return qualifiedName;
    }

    public FormItem getFormItem()
    {
        return formItem;
    }

    @Override
    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    @Override
    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public SchemaKey getSchemaKey()
    {
        return SchemaKey.from( qualifiedName );
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

        private Icon icon;

        public Builder()
        {

        }

        public Builder( final Mixin mixin )
        {
            Preconditions.checkNotNull( mixin, "mixin cannot be null" );
            this.displayName = mixin.displayName;
            this.moduleName = mixin.moduleName;
            this.createdTime = mixin.createdTime;
            this.modifiedTime = mixin.modifiedTime;
            this.formItem = mixin.formItem;
            this.icon = mixin.icon;
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

        public Builder icon( Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }


    }
}
