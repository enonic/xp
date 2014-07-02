module api.ui.treegrid {

    export interface TreeItem {

        getId(): string;

        hasChildren(): boolean;

    }
}
