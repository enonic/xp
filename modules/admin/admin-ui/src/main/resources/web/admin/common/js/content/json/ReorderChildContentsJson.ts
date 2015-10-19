module api.content.json {

    export interface ReorderChildContentsJson extends SetOrderUpdateJson {

        manualOrder: boolean;

        childOrder: ChildOrderJson;

        reorderChildren: ReorderChildContentJson[];

    }
}