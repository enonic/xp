module api_handler {

    export class DeleteSpacesHandler {

        doDelete(deleteSpaceParam:api_handler.DeleteSpaceParam, callback:(thisArg, success, result) => void) {

            api_remote.RemoteService.space_delete(deleteSpaceParam, function (response) {
                if (response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete space.');
                }
            });
        }
    }
}