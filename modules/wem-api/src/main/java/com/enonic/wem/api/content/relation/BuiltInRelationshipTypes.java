package com.enonic.wem.api.content.relation;


import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.module.ModuleName;

public class BuiltInRelationshipTypes
{
    public static final RelationshipType DEFAULT =
        RelationshipType.newRelationshipType().name( "default" ).fromSemantic( "relates to" ).toSemantic( "related by" ).createdTime(
            new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();

    public static final RelationshipType REQUIRE =
        RelationshipType.newRelationshipType().name( "require" ).fromSemantic( "requires" ).toSemantic( "required by" ).createdTime(
            new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();

    public static final RelationshipType LIKE =
        RelationshipType.newRelationshipType().name( "like" ).fromSemantic( "likes" ).toSemantic( "liked by" ).createdTime(
            new DateTime( 2013, 1, 17, 15, 0, 0 ) ).module( ModuleName.SYSTEM ).build();


    public static ImmutableList<RelationshipType> ALL = ImmutableList.of( DEFAULT, REQUIRE, LIKE );

}
