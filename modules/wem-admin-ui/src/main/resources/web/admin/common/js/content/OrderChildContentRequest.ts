module api.content {

    export class OrderChildContentRequest extends ContentResourceRequest<any, any> {

        private contentMovements: OrderChildMovements;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setContentMovements(value: OrderChildMovements): OrderChildContentRequest {
            this.contentMovements = value;
            return this;
        }

        getParams(): json.ReorderChildContentsJson {
            return this.contentMovements.toJson();
        }


        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "reorderChildren");
        }

        sendAndParse(): wemQ.Promise<any> {

            return this.send();
        }

    }
}