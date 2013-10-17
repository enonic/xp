module api_schema_relationshiptype_json {

    export interface RelationshipTypeJson extends api_item.ItemJson{

        iconUrl:string;

        name:string;

        displayName:string;

        fromSemantic:string;

        toSemantic:string;

        allowedFromTypes:string[];

        allowedToTypes:string[];
    }
}