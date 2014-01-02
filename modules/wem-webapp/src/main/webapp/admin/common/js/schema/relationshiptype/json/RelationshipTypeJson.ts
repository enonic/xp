module api.schema.relationshiptype.json {

    export interface RelationshipTypeJson extends api.schema.SchemaJson {

        fromSemantic:string;

        toSemantic:string;

        allowedFromTypes:string[];

        allowedToTypes:string[];
    }
}