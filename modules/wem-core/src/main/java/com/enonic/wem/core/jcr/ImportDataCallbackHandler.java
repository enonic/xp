package com.enonic.wem.core.jcr;

import java.util.Map;

public interface ImportDataCallbackHandler
{

    void processDataEntry( Map<String, Object> data );

}
