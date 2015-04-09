package com.enonic.xp.schema.relationship;

import java.util.function.Supplier;

import com.google.common.annotations.Beta;

@Beta
public interface RelationshipTypeProvider
    extends Supplier<RelationshipTypes>
{
}
