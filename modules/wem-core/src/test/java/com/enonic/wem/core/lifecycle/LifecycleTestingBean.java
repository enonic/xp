package com.enonic.wem.core.lifecycle;

final class LifecycleTestingBean
    extends LifecycleBean
{
    protected int startCount;

    protected int stopCount;

    protected boolean exceptionOnStart;

    protected boolean exceptionOnStop;

    public LifecycleTestingBean( final RunLevel level )
    {
        super( level );
        this.startCount = this.stopCount = 0;
        this.exceptionOnStart = this.exceptionOnStop = false;
    }

    @Override
    protected void doStart()
        throws Exception
    {
        this.startCount++;

        if ( this.exceptionOnStart )
        {
            throw new RuntimeException();
        }
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.stopCount++;

        if ( this.exceptionOnStop )
        {
            throw new RuntimeException();
        }
    }
}
