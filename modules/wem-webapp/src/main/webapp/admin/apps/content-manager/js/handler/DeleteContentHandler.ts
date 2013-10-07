module admin_app_handler {

    export class DeleteContentHandler {

        doDelete(contentModels, success:(result:api_remote_content.DeleteResult) => void,
                 failure?:(result:api_remote.FailureResult) => void) {

            var contentPaths = Ext.Array.map([].concat(contentModels), function (item) {
                return item.get('path');
            });

            api_remote_content.RemoteContentService.content_delete({'contentPaths': contentPaths }, success, failure);

        }

    }
}
