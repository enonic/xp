module api_handler {

    export class DeleteSpacesHandler {

        doDelete(deleteSpaceParam:api_handler.DeleteSpaceParam, success:(result:api_remote_space.DeleteResult) => void,
                 failure?:(error:api_remote.FailureResult) => void) {

            api_remote.RemoteSpaceService.space_delete(deleteSpaceParam, success, failure);
        }
    }
}