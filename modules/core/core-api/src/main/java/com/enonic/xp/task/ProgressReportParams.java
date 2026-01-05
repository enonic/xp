package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ProgressReportParams
{
    private final Integer current;

    private final Integer total;

    private final String info;

    private ProgressReportParams( final Builder builder )
    {
        this.current = builder.current;
        this.total = builder.total;
        this.info = builder.info;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Integer getCurrent()
    {
        return current;
    }

    public Integer getTotal()
    {
        return total;
    }

    public String getInfo()
    {
        return info;
    }

    public static final class Builder
    {
        private Integer current;

        private Integer total;

        private String info;

        private Builder()
        {
        }

        /**
         * Sets the current progress value.
         *
         * @param current current progress value. If null, current value is unmodified
         * @return this builder
         */
        public Builder current( final Integer current )
        {
            this.current = current;
            return this;
        }

        /**
         * Sets the total progress value.
         *
         * @param total total items to be processed. Initially if null, total is unknown. If total was set before, null means
         *              unchanged. Total can change during progress
         * @return this builder
         */
        public Builder total( final Integer total )
        {
            this.total = total;
            return this;
        }

        /**
         * Sets the progress info message.
         *
         * @param info a string shown in task status. If null, current status message is not modified
         * @return this builder
         */
        public Builder info( final String info )
        {
            this.info = info;
            return this;
        }

        public ProgressReportParams build()
        {
            return new ProgressReportParams( this );
        }
    }
}
