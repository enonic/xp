package com.enonic.xp.init;

public class TestExternalInitializer
    extends ExternalInitializer
{
    public TestExternalInitializer( final Builder builder )
    {
        super( builder );
    }

    @Override
    protected boolean isInitialized()
    {
        return false;
    }

    @Override
    protected void doInitialize()
    {

    }

    @Override
    protected String getInitializationSubject()
    {
        return null;
    }

    public static TestExternalInitializer.Builder create()
    {
        return new TestExternalInitializer.Builder();
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        public TestExternalInitializer build()
        {
            return new TestExternalInitializer( this );
        }
    }
}
