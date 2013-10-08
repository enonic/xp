module api_handler {

    export class DeleteContentParamFactory {

        static create(content:api_content.ContentSummary[]):api_handler.DeleteContentParam {
            var contentPaths:string[] = [];
            for (var i = 0; i < content.length; i++) {
                contentPaths[i] = content[i].getPath().toString();
            }

            return  {
                contentPaths: contentPaths
            };
        }
    }
}