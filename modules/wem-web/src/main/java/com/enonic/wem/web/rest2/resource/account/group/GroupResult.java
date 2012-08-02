package com.enonic.wem.web.rest2.resource.account.group;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class GroupResult
    extends JsonResult
{
    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        return json;
    }
}
