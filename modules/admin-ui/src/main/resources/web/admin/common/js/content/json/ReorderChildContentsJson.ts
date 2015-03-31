module api.content.json {

    export interface ReorderChildContentsJson extends SetOrderUpdateJson {

        reorderChildren: ReorderChildContentJson[];

    }
}