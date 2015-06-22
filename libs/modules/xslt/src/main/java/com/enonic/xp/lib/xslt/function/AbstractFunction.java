package com.enonic.xp.lib.xslt.function;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

abstract class AbstractFunction
    extends ExtensionFunctionDefinition
{
    private final static String NAMESPACE_URI = "urn:enonic:xp:portal:1.0";

    private final StructuredQName name;

    private SequenceType resultType;

    private SequenceType[] argumentTypes;

    private int minArguments = 0;

    private int maxArguments = 0;

    public AbstractFunction( final String localName )
    {
        this.name = new StructuredQName( "", NAMESPACE_URI, localName );
        this.resultType = SequenceType.SINGLE_ATOMIC;
        this.argumentTypes = new SequenceType[0];
    }

    @Override
    public final StructuredQName getFunctionQName()
    {
        return this.name;
    }

    @Override
    public final SequenceType getResultType( final SequenceType[] types )
    {
        return this.resultType;
    }

    @Override
    public final SequenceType[] getArgumentTypes()
    {
        return this.argumentTypes;
    }

    @Override
    public int getMinimumNumberOfArguments()
    {
        return this.minArguments;
    }

    @Override
    public int getMaximumNumberOfArguments()
    {
        return this.maxArguments;
    }

    protected final void setResultType( final SequenceType resultType )
    {
        this.resultType = resultType;
    }

    protected final void setArgumentTypes( final SequenceType... types )
    {
        this.argumentTypes = types;
    }

    protected final void setMinimumNumberOfArguments( final int num )
    {
        this.minArguments = num;
    }

    protected final void setMaximumNumberOfArguments( final int args )
    {
        this.maxArguments = args;
    }
}
