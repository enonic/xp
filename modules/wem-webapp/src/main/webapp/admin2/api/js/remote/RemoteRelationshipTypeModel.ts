module api_remote {

    export interface RelationshipType extends Item {
        name:string;
        displayName:string;
        iconUrl:string;
        module:string;
        fromSemantic:string;
        toSemantic:string;
        allowedFromTypes:string[];
        allowedToTypes:string[];
    }

    export interface RemoteCallDeleteRelationshipTypeParams {
        qualifiedRelationshipTypeNames:string[];
    }

    export interface DeleteRelationshipTypeSuccess {
        qualifiedRelationshipTypeName:string;
    }

    export interface DeleteRelationshipTypeFailure {
        qualifiedRelationshipTypeName:string;
        reason:string;
    }

    export interface RemoteCallDeleteRelationshipTypeResult extends RemoteCallResultBase {
        successes:DeleteRelationshipTypeSuccess[];
        failures:DeleteRelationshipTypeFailure[];
    }

    export interface RemoteCallGetRelationshipTypeParams {
        qualifiedRelationshipTypeName:string;
        format:string;
    }

    export interface RemoteCallGetRelationshipTypeResult extends RemoteCallResultBase {
        relationshipType:RelationshipType;
    }

    export interface RemoteCallCreateOrUpdateRelationshipTypeParams {
        relationshipType:string;
        iconReference:string;
    }

    export interface RemoteCallCreateOrUpdateRelationshipTypeResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

}