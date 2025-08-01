package com.enonic.xp.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ModifyScheduledJobParams
{
    private final ScheduledJobName name;

    private final ScheduledJobEditor editor;

    private ModifyScheduledJobParams( final Builder builder )
    {
        this.name = builder.name;
        this.editor = builder.editor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ScheduledJobName getName()
    {
        return name;
    }

    public ScheduledJobEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        private ScheduledJobName name;

        private ScheduledJobEditor editor;

        private Builder()
        {
        }

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        public Builder editor( final ScheduledJobEditor editor )
        {
            this.editor = editor;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name must be set." );
            Preconditions.checkNotNull( editor, "editor must be set." );
        }

        public ModifyScheduledJobParams build()
        {
            validate();
            return new ModifyScheduledJobParams( this );
        }
    }
}
