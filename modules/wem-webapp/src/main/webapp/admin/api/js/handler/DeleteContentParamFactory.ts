module api_handler {

    export class DeleteContentParamFactory {

        static create(content:api_model.ContentSummaryExtModel[]):api_handler.DeleteContentParam {
            var contentPaths:string[] = [];
            for (var i = 0; i < content.length; i++) {
                contentPaths[i] = content[i].data.path;
            }

            return  {
                contentPaths: contentPaths
            };
        }
    }
}