package com.enonic.xp.core.impl.export;

import com.enonic.xp.core.node.InsertManualStrategy;

public class ProcessNodeSettings
{
    private final InsertManualStrategy insertManualStrategy;

    private final long manualOrderValue;

    private ProcessNodeSettings( Builder builder )
    {
        insertManualStrategy = builder.insertManualStrategy;
        manualOrderValue = builder.manualOrderValue;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public static final class Builder
    {
        private InsertManualStrategy insertManualStrategy;

        private long manualOrderValue;

        private Builder()
        {
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder manualOrderValue( long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public ProcessNodeSettings build()
        {
            return new ProcessNodeSettings( this );
        }
    }
}
