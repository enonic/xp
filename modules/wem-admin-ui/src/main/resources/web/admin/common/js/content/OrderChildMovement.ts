module api.content {

    import ReorderChildContentJson =  api.content.json.ReorderChildContentJson;

    export class OrderChildMovement {

        private contentId: ContentId;

        private moveBefore: ContentId;

        constructor(id: ContentId, moveBeforeId: ContentId) {
            this.contentId = id;
            this.moveBefore = moveBeforeId;
        }

        getContentId(): ContentId {
            return this.contentId;
        }

        getMoveBefore(): ContentId {
            return this.moveBefore;
        }

        toJson(): ReorderChildContentJson {
            return {
                "contentId": this.contentId.toString(),
                "moveBefore": !!this.moveBefore ? this.moveBefore.toString() : ""
            };
        }


    }

}