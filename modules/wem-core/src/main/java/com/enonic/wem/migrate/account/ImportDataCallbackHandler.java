package com.enonic.wem.migrate.account;

import java.util.Map;

public interface ImportDataCallbackHandler
{
    void processDataEntry( Map<String, Object> data );
}
