module api_node{

    export interface Node extends api_item.Item {

        hasChildren():boolean;
    }
}