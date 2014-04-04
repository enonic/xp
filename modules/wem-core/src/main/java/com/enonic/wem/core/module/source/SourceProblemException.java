package com.enonic.wem.core.module.source;

import java.util.List;

public final class SourceProblemException
    extends RuntimeException
{
    public ModuleSource getSource()
    {
        return null;
    }

    public int getLineNumber()
    {
        return 0;
    }

    public List<String> getCallStack()
    {
        return null;
    }

    public SourceProblemException getInnerException()
    {
        return null;
    }
}
