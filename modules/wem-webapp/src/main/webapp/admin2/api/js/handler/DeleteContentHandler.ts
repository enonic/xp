module api_handler {

    export class DeleteContentHandler {

        doDelete(deleteContentParam:api_handler.DeleteContentParam, callback:(thisArg, success, result) => void) {
            api_remote.RemoteContentService.content_delete(deleteContentParam, function (response) {
                if (response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete content.');
                }
            })
        }
    }
}