package com.enonic.wem.api.schema;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.Name;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

public abstract class BaseSchema<T extends ModuleBasedQualifiedName>
    implements Schema
{
    private final Name name;

    private final String displayName;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final UserKey creator;

    private final UserKey modifier;

    private final Icon icon;

    private final ModuleName moduleName;

    protected BaseSchema( Builder builder )
    {
        this.name = builder.name;
        this.moduleName = builder.moduleName;
        this.displayName = builder.displayName;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.icon = builder.icon;
    }

    public String getName()
    {
        return name != null ? name.toString() : null;
    }

    @Override
    public ModuleName getModuleName()
    {
        return moduleName;
    }

    public abstract T getQualifiedName();

    public String getDisplayName()
    {
        return displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public static class Builder<T extends Builder>
    {
        private Name name;

        private ModuleName moduleName;

        private String displayName;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private UserKey creator;

        private UserKey modifier;

        private Icon icon;

        public Builder()
        {

        }

        public Builder( final BaseSchema schema )
        {
            Preconditions.checkNotNull( schema, "schema cannot be null" );
            this.name = schema.name;
            this.moduleName = schema.getModuleName();
            this.displayName = schema.displayName;
            this.createdTime = schema.createdTime;
            this.modifiedTime = schema.modifiedTime;
            this.creator = schema.creator;
            this.modifier = schema.modifier;
        }

        private T getThis()
        {
            return (T) this;
        }

        public T name( final String name )
        {
            this.name = name != null ? Name.from( name ) : null;
            return getThis();
        }

        public T module( ModuleName value )
        {
            this.moduleName = value;
            return getThis();
        }

        public T qualifiedName( final QualifiedContentTypeName qualifiedContentTypeName )
        {
            this.name = Name.from( qualifiedContentTypeName.getContentTypeName() );
            this.moduleName = qualifiedContentTypeName.getModuleName();
            return getThis();
        }

        public T displayName( String value )
        {
            this.displayName = value;
            return getThis();
        }

        public T createdTime( DateTime value )
        {
            this.createdTime = value;
            return getThis();
        }

        public T modifiedTime( DateTime value )
        {
            this.modifiedTime = value;
            return getThis();
        }

        public T creator( UserKey value )
        {
            this.creator = value;
            return getThis();
        }

        public T modifier( UserKey value )
        {
            this.modifier = value;
            return getThis();
        }

        public T icon( Icon icon )
        {
            this.icon = icon;
            return getThis();
        }
    }
}
