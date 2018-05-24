package com.enonic.xp.init;

public class TestInitializer
    extends Initializer
{
    private final boolean isMaster;

    private final boolean initialized;

    private final Runnable initialization;

    public TestInitializer( final Builder builder )
    {
        super( builder );
        this.isMaster = builder.isMaster;
        this.initialized = builder.initialized;
        this.initialization = builder.initialization;
    }

    @Override
    protected boolean isMaster()
    {
        return isMaster;
    }

    @Override
    protected boolean isInitialized()
    {
        return initialized;
    }

    @Override
    protected void doInitialize()
    {
        initialization.run();
    }

    @Override
    protected String getInitializationSubject()
    {
        return "Initialization test";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Initializer.Builder<Builder>
    {
        private boolean isMaster;

        private boolean initialized;

        private Runnable initialization;

        public Builder setMaster( final boolean master )
        {
            isMaster = master;
            return this;
        }

        public Builder setInitialized( final boolean initialized )
        {
            this.initialized = initialized;
            return this;
        }

        public Builder setInitialization( final Runnable initialization )
        {
            this.initialization = initialization;
            return this;
        }

        public TestInitializer build()
        {
            return new TestInitializer( this );
        }
    }
}
