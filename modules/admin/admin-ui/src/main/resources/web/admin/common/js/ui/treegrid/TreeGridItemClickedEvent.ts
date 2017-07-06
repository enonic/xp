module api.ui.treegrid {

    export class TreeGridItemClickedEvent extends api.event.Event {

        private node: TreeNode<any>;

        private selection: boolean;

        constructor(node: TreeNode<any>, selection: boolean = false) {
            super();
            this.selection = selection;
            this.node = node;
        }

        public hasSelection() {
            return this.selection;
        }

        public getTreeNode(): TreeNode<any> {
            return this.node;
        }

        static on(handler: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: TreeGridItemClickedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
