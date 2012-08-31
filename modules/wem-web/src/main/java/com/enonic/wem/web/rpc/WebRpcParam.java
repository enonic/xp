package com.enonic.wem.web.rpc;

import org.codehaus.jackson.node.ObjectNode;

public interface WebRpcParam
{
    public String getName();

    public boolean isNull();

    public WebRpcParam required()
        throws WebRpcException;

    public String asString();

    public String asString( String defValue );

    public String[] asStringArray();

    public Integer asInteger();

    public Integer asInteger( Integer defValue );

    public Integer[] asIntegerArray();

    public Double asDouble();

    public Double asDouble( Double defValue );

    public Double[] asDoubleArray();

    public Boolean asBoolean();

    public Boolean asBoolean( Boolean defValue );

    public Boolean[] asBooleanArray();

    public ObjectNode asObject();

    public ObjectNode asObject( ObjectNode defValue );

    public ObjectNode[] asObjectArray();
}
