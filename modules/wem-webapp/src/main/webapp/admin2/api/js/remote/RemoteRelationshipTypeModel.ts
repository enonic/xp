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

    export interface DeleteResult extends api_remote.BaseResult {
        successes:DeleteRelationshipTypeSuccess[];
        failures:DeleteRelationshipTypeFailure[];
    }

    export interface GetParams {
        qualifiedRelationshipTypeName:string;
        format:string;
    }

    export interface GetResult extends api_remote.BaseResult {
        iconUrl:string;
        relationshipType:RelationshipType;
    }

    export interface CreateOrUpdateParams {
        relationshipType:string;
        iconReference:string;
    }

    export interface CreateOrUpdateResult extends api_remote.BaseResult {
        created:bool;
        updated:bool;
    }

}