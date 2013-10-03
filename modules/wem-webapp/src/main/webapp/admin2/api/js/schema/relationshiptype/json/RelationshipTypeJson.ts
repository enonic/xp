module api_schema_relationshiptype_json {

    export class RelationshipTypeJson {

        iconUrl:string;
        name:string;
        displayName:string;
        fromSemantic:string;
        toSemantic:string;
        allowedFromTypes:string[];
        allowedToTypes:string[];
        modifiedTime:string;
        createdTime:string;
        editable:boolean;
        deletable:boolean;

    }
}