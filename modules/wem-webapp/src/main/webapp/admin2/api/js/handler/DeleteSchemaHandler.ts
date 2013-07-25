module api_handler {

    export class DeleteSchemaHandler {

        public static CONTENT_TYPE = "ContentType";
        public static RELATIONSHIP_TYPE = "RelationshipType";
        public static MIXIN = "Mixin";

        doDelete(deleteSchemaParam:api_handler.DeleteSchemaParam, callback:(thisArg, success, result) => void) {

            var callbackFn = function (response) {
                if (response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete space.');
                }
            };

            switch (deleteSchemaParam.type) {
            case DeleteSchemaHandler.CONTENT_TYPE:
                api_remote.RemoteService.contentType_delete({
                    qualifiedContentTypeNames: deleteSchemaParam.qualifiedNames
                }, callbackFn);
                break;
            case DeleteSchemaHandler.RELATIONSHIP_TYPE:
                api_remote.RemoteService.relationshipType_delete({
                    qualifiedRelationshipTypeNames: deleteSchemaParam.qualifiedNames
                }, callbackFn);
                break;
            case DeleteSchemaHandler.MIXIN:
                api_remote.RemoteService.mixin_delete({
                    qualifiedMixinNames: deleteSchemaParam.qualifiedNames
                }, callbackFn);
                break;
            }


        }
    }
}