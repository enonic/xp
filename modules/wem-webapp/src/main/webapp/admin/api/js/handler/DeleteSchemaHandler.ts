module api_handler {

    export class DeleteSchemaHandler {

        public static CONTENT_TYPE = "ContentType";
        public static RELATIONSHIP_TYPE = "RelationshipType";
        public static MIXIN = "Mixin";

        doDelete(deleteSchemaParam:api_handler.DeleteSchemaParam, success:(result) => void,
                 failure?:(result:api_remote.FailureResult) => void) {

            switch (deleteSchemaParam.type) {
            case DeleteSchemaHandler.CONTENT_TYPE:
                api_remote_contenttype.RemoteContentTypeService.contentType_delete({
                    qualifiedContentTypeNames: deleteSchemaParam.qualifiedNames
                }, success, failure);
                break;
            case DeleteSchemaHandler.RELATIONSHIP_TYPE:
                api_remote_relationshiptype.RemoteRelationshipTypeService.relationshipType_delete({
                    qualifiedRelationshipTypeNames: deleteSchemaParam.qualifiedNames
                }, success, failure);
                break;
            case DeleteSchemaHandler.MIXIN:
                api_remote_mixin.RemoteMixinService.mixin_delete({
                    qualifiedMixinNames: deleteSchemaParam.qualifiedNames
                }, success, failure);
                break;
            }


        }
    }
}