package com.enonic.nodb.engine.model;

/** Pagination window for children listings: {@code OFFSET from LIMIT size}. */
public record Page(int from, int size)
{
}
