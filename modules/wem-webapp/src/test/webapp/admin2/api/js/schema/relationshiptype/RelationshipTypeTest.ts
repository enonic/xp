///<reference path='../../TestCase.d.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/relationshiptype/json/module.ts' />
///<reference path='../../../../../../../main/webapp/admin2/api/js/schema/relationshiptype/module.ts' />

TestCase("RelationshiptType", {
    "test relationship type": function () {

        var json = {
            "allowedFromTypes": [],
            "allowedToTypes": [],
            "deletable": false,
            "displayName": "Default",
            "editable": false,
            "fromSemantic": "relates to",
            "iconUrl": "http://localhost:8080/admin/rest/schema/image/RelationshipType:system:default",
            "name": "default",
            "toSemantic": "related of",
            "createdTime": "01/01/2013",
            "modifiedTime": "02/02/2013"
        };

        var relationshipTypeJson:api_schema_relationshiptype_json.RelationshipTypeJson = json;

        var relationshipType:api_schema_relationshiptype.RelationshipType = new api_schema_relationshiptype.RelationshipType(relationshipTypeJson);

        assertEquals([], relationshipType.getAllowedFromTypes());
        assertEquals([], relationshipType.getAllowedToTypes());
        assertEquals("relates to", relationshipType.getFromSemantic());
        assertEquals("related of", relationshipType.getToSemantic());
        assertEquals("http://localhost:8080/admin/rest/schema/image/RelationshipType:system:default", relationshipType.getIcon());
        assertEquals("default", relationshipType.getName());
        assertEquals("Default", relationshipType.getDisplayName());
        assertEquals(new Date("01/01/2013"), relationshipType.getCreatedTime());
        assertEquals(new Date("02/02/2013"), relationshipType.getModifiedTime());
    }
})