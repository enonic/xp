package com.enonic.wem.core.index;

public enum SearchSortOrder
{
    ASC
        {
            @Override
            public String toString()
            {
                return "asc";
            }
        },
    DESC
        {
            @Override
            public String toString()
            {
                return "desc";
            }
        }
}