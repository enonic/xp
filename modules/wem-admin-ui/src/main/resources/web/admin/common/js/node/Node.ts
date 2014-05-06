module api.node{

    export interface Node extends api.item.Item {

        hasChildren():boolean;
    }
}