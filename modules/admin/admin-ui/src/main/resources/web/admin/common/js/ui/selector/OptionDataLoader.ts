module api.ui.selector {

    import TreeNode = api.ui.treegrid.TreeNode;

    export interface OptionDataLoader<DATA> {

        fetch(node: TreeNode<Option<DATA>>): wemQ.Promise<DATA>;

        fetchChildren(parentNode: TreeNode<Option<DATA>>, from?: number, size?: number): wemQ.Promise<OptionDataLoaderData<DATA>>;

        checkReadonly(options: DATA[]): wemQ.Promise<string[]>;
    }

    export class OptionDataLoaderData<DATA> {

        private data: DATA[];
        private hits: number;
        private totalHits: number;

        constructor(data: DATA[], hits?: number, totalHits?: number) {
            this.data = data;
            this.hits = hits;
            this.totalHits = totalHits;
        }

        public getData(): DATA[] {
            return this.data;
        }

        public getHits(): number {
            return this.hits;
        }

        public getTotalHits(): number {
            return this.totalHits;
        }
    }
}
