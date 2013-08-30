module api_remote_relationshiptype {

    export interface RelationshipType extends api_remote.Item {
        name:string;
        displayName:string;
        iconUrl:string;
        module:string;
        fromSemantic:string;
        toSemantic:string;
        allowedFromTypes:string[];
        allowedToTypes:string[];
    }
    
    export interface DeleteParams {
        qualifiedRelationshipTypeNames:string[];
    }

    export interface DeleteRelationshipTypeSuccess {
        qualifiedRelationshipTypeName:string;
    }

    export interface DeleteRelationshipTypeFailure {
        qualifiedRelationshipTypeName:string;
        reason:string;
    }

    export interface DeleteResult {
        successes:DeleteRelationshipTypeSuccess[];
        failures:DeleteRelationshipTypeFailure[];
    }

    export interface GetParams {
        qualifiedName:string;
        format:string;
    }

    export interface GetResult {
        iconUrl:string;
        relationshipType?:RelationshipType;
        relationshipTypeXml?:string;
    }

    export interface CreateOrUpdateParams {
        name:string;
        relationshipType:string;
        iconReference:string;
    }

    export interface CreateOrUpdateResult {
        created:boolean;
        updated:boolean;
    }

}