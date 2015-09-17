module api.schema.relationshiptype {

    export interface RelationshipTypeJson extends api.schema.SchemaJson {

        fromSemantic:string;

        toSemantic:string;

        allowedFromTypes:string[];

        allowedToTypes:string[];
    }
}