module api.content {

    export class OrderChildContentRequest extends ContentResourceRequest<any, any> {

        private silent: boolean = false;

        private manualOrder: boolean = false;

        private contentId: ContentId;

        private contentMovements: OrderChildMovements;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setSilent(silent: boolean): OrderChildContentRequest {
            this.silent = silent;
            return this;
        }

        setManualOrder(manualOrder: boolean): OrderChildContentRequest {
            this.manualOrder = manualOrder;
            return this;
        }

        setContentId(value: ContentId): OrderChildContentRequest {
            this.contentId = value;
            return this;
        }

        setContentMovements(value: OrderChildMovements): OrderChildContentRequest {
            this.contentMovements = value;
            return this;
        }

        getParams(): json.ReorderChildContentsJson {
            return {
                "silent": this.silent,
                "manualOrder": this.manualOrder,
                "contentId": this.contentId.toString(),
                "reorderChildren": this.contentMovements.toArrayJson()
            };
        }


        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "reorderChildren");
        }

        sendAndParse(): wemQ.Promise<any> {

            return this.send();
        }

    }
}