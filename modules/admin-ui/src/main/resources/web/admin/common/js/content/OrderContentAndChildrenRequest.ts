module api.content {

    export class OrderContentAndChildrenRequest extends ContentResourceRequest<any, any> {

        private silent: boolean = false;

        private contentId: ContentId;

        private childOrder: ChildOrder;

        private contentMovements: OrderChildMovements;

        constructor() {
            super();
            super.setMethod("POST");

            this.silent = false;
            this.contentMovements;
        }

        setContentId(value: ContentId): OrderContentAndChildrenRequest {
            this.contentId = value;
            return this;
        }

        setChildOrder(value: ChildOrder): OrderContentAndChildrenRequest {
            this.childOrder = value;
            return this;
        }

        setContentMovements(value: OrderChildMovements): OrderContentAndChildrenRequest {
            this.contentMovements = value;
            return this;
        }

        setSilent(silent: boolean): OrderContentAndChildrenRequest {
            this.silent = silent;
            return this;
        }


        getParams(): Object {
            return this.contentToJson();
        }

        private contentToJson(): json.SetChildOrderAndReorderJson {
            return {
                "silent": this.silent,
                "contentId": this.contentId.toString(),
                "childOrder": this.childOrder.toJson(),
                "reorderChildren": this.contentMovements.toArrayJson()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "setAndReorderChildren");
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().
                then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {

                    return this.fromJsonToContent(response.getResult());

                });
        }

    }
}