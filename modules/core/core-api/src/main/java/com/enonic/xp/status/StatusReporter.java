package com.enonic.xp.status;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface StatusReporter
{
    String getName();

    ObjectNode getReport();
}
