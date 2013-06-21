module api_handler {

    export class DeleteContentParamFactory {

        static create(content:api_model.ContentModel[]):api_handler.DeleteContentParam {
            var contentIds:string[] = [];
            for (var i = 0; i < content.length; i++) {
                contentIds[i] = content[i].data.id;
            }

            return  {
                contentIds: contentIds
            };
        }
    }
}