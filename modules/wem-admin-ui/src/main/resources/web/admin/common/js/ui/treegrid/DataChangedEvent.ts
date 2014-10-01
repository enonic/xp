module api.ui.treegrid {

    export class DataChangedEvent<NODE> {

        public static ACTION_ADDED: string = 'added';
        public static ACTION_UPDATED: string = 'updated';
        public static ACTION_DELETED: string = 'deleted';

        private data: TreeNode<NODE>[];
        private action: string;

        constructor(data: TreeNode<NODE>[], action: string) {
            this.data = data;
            this.action = action;
        }

        public getData(): TreeNode<NODE>[] {
            return this.data;
        }

        public getAction(): string {
            return this.action;
        }

    }
}