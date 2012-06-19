package com.enonic.wem.migrate.jcr;

import java.util.Map;

public interface ImportDataCallbackHandler
{

    void processDataEntry( Map<String, Object> data );

}
