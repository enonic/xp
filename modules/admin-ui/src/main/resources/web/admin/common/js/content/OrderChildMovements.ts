module api.content {

    import ReorderChildContentJson =  api.content.json.ReorderChildContentJson;
    import ReorderChildContentsJson =  api.content.json.ReorderChildContentsJson;

    export class OrderChildMovements {

        private reorderChildren: OrderChildMovement[] = [];

        getReorderChildren(): OrderChildMovement[] {
            return this.reorderChildren;
        }

        addChildMovement(movement: OrderChildMovement) {
            this.reorderChildren.push(movement);
        }

        toJson(): ReorderChildContentsJson {
            return {
                "reorderChildren": this.toArrayJson()
            };
        }

        private toArrayJson(): ReorderChildContentJson[] {
            var result: ReorderChildContentJson[] = [];
            this.reorderChildren.forEach((movement: OrderChildMovement) => {
                result.push(movement.toJson());
            });
            return result;
        }


    }

}