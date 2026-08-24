package com.enonic.xp.core.impl.export;

import com.enonic.xp.node.InsertManualStrategy;

public class ProcessNodeSettings
{
    private final InsertManualStrategy insertManualStrategy;

    private final long manualOrderValue;

    private final boolean orderByKeys;

    private final String orderKeyAnchor;

    private ProcessNodeSettings( Builder builder )
    {
        insertManualStrategy = builder.insertManualStrategy;
        manualOrderValue = builder.manualOrderValue;
        orderByKeys = builder.orderByKeys;
        orderKeyAnchor = builder.orderKeyAnchor;
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

    public boolean isOrderByKeys()
    {
        return orderByKeys;
    }

    public String getOrderKeyAnchor()
    {
        return orderKeyAnchor;
    }

    public static final class Builder
    {
        private InsertManualStrategy insertManualStrategy;

        private long manualOrderValue;

        private boolean orderByKeys;

        private String orderKeyAnchor;

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

        public Builder orderByKeys( boolean orderByKeys )
        {
            this.orderByKeys = orderByKeys;
            return this;
        }

        public Builder orderKeyAnchor( String orderKeyAnchor )
        {
            this.orderKeyAnchor = orderKeyAnchor;
            return this;
        }

        public ProcessNodeSettings build()
        {
            return new ProcessNodeSettings( this );
        }
    }
}
