module api_handler {

    export class DeleteContentHandler {

        doDelete(deleteContentParam:api_handler.DeleteContentParam, success:(result:api_remote_content.DeleteResult) => void,
                 failure?:(result:api_remote.FailureResult) => void) {

            api_remote_content.RemoteContentService.content_delete(deleteContentParam, success, failure);
        }
    }
}