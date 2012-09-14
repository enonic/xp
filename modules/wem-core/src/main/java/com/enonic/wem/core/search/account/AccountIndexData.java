package com.enonic.wem.core.search.account;

import org.elasticsearch.common.xcontent.XContentBuilder;

public interface AccountIndexData
{
    AccountKey getKey();

    XContentBuilder getData();
}
