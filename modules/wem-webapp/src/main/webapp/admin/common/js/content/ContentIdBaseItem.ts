module api.content{

    export class ContentIdBaseItem  {

        private contentId:ContentId;

        static fromJsonArray(jsonArray:json.ContentIdBaseItemJson[]):ContentIdBaseItem[] {
            var array:ContentIdBaseItem[] = [];
            jsonArray.forEach((json:json.ContentIdBaseItemJson) => {
                array.push(new ContentIdBaseItem(json));
            });
            return array;
        }

        constructor(json:json.ContentIdBaseItemJson) {
            this.contentId = new ContentId( json.id );
        }

        getContentId():ContentId {
            return this.contentId;
        }
    }
}