///<reference path='../TestCase.d.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/relationship/module.ts' />
///<reference path='../../../../../../main/webapp/admin2/api/js/relationship/json/module.ts' />

TestCase("Relationship", {

    "test relationship": function () {

        var json:api_relationship_json.RelationshipJson = {
            id: 'test-relationship',
            type: 'relationship',
            fromContent: 'fromContent',
            toContent: 'toContent',
            managingData: 'data',
            properties: {},
            editable: true,
            deletable: false,

            modifiedTime: '02/02/2013',
            createdTime: '01/01/2013',
            creator: 'creator',
            modifier: 'modifier'
        };

        var relationship:api_relationship.Relationship = new api_relationship.Relationship(json);

        assertEquals(relationship.getType(), 'relationship');
        assertEquals(relationship.getCreator(), 'creator');
        assertEquals(relationship.getModifier(), 'modifier');
        assertEquals(relationship.getCreatedTime(), new Date('01/01/2013'));
        assertEquals(relationship.getModifiedTime(), new Date('02/02/2013'));
        assertEquals(relationship.getId(), 'test-relationship');
        assertEquals(relationship.getProperties(), {});
        assertEquals(relationship.getFromContent(), 'fromContent');
        assertEquals(relationship.getToContent(), 'toContent');


    }
});