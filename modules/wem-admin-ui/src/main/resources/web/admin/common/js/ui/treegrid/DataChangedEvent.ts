module api.ui.treegrid {

    export class DataChangedEvent<DATA> {

        public static ADDED: string = 'added';

        public static UPDATED: string = 'updated';

        public static DELETED: string = 'deleted';

        private treeNodes: TreeNode<DATA>[];

        private type: string;

        constructor(treeNode: TreeNode<DATA>[], action: string) {
            this.treeNodes = treeNode;
            this.type = action;
        }

        public getTreeNodes(): TreeNode<DATA>[] {
            return this.treeNodes;
        }

        public getType(): string {
            return this.type;
        }

    }
}