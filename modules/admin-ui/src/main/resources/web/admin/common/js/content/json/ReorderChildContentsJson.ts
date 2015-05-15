module api.content.json {

    export interface ReorderChildContentsJson extends SetOrderUpdateJson {

        childOrder: ChildOrderJson;

        reorderChildren: ReorderChildContentJson[];

    }
}