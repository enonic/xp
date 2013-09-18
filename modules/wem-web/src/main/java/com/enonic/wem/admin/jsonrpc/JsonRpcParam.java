package com.enonic.wem.admin.jsonrpc;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface JsonRpcParam
{
    public String getName();

    public boolean isNull();

    public JsonRpcParam required()
        throws JsonRpcException;

    public JsonRpcParam notBlank()
        throws JsonRpcException;

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
