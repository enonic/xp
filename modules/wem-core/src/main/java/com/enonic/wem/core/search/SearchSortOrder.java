package com.enonic.wem.core.search;

public enum SearchSortOrder
{
    ASC {
        @Override public String toString() {
            return "asc";
        }
    },
    DESC {
        @Override public String toString() {
            return "desc";
        }
    }
}