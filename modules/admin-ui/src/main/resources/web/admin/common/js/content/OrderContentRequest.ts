module api.content {

    export class OrderContentRequest extends ContentResourceRequest<any, any> {

        private silent: boolean = false;

        private contentId: ContentId;

        private childOrder: ChildOrder;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setContentId(value: ContentId): OrderContentRequest {
            this.contentId = value;
            return this;
        }

        setChildOrder(value: ChildOrder): OrderContentRequest {
            this.childOrder = value;
            return this;
        }

        setSilent(silent: boolean): OrderContentRequest {
            this.silent = silent;
            return this;
        }

        getParams(): Object {
            return this.contentToJson();
        }

        private contentToJson(): json.SetChildOrderJson {
            return ChildOrder.toSetChildOrderJson(this.contentId, this.childOrder, this.silent);
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "setChildOrder");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {

                    return this.fromJsonToContent(response.getResult());

                });
        }

    }
}