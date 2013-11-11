module api_schema_relationshiptype_json {

    export interface RelationshipTypeJson extends api_schema.SchemaJson {

        fromSemantic:string;

        toSemantic:string;

        allowedFromTypes:string[];

        allowedToTypes:string[];
    }
}