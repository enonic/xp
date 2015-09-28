module api.ui.treegrid {

    export class TreeNodeBuilder<NODE> {

        private dataId: string;

        private data: NODE;

        private expanded: boolean;

        private selected: boolean;

        private pinned: boolean;

        private maxChildren: number;

        private parent: TreeNode<NODE>;

        private children: TreeNode<NODE>[];

        constructor(node?: TreeNode<NODE>) {
            if (node) {
                this.data = node.getData();
                this.parent = node.getParent();
                this.children = node.getChildren() || [];
                this.maxChildren = node.getMaxChildren();
            } else {
                this.children = [];
                this.maxChildren = 0;
            }
            this.pinned = false;
            this.expanded = false;
            this.selected = false;
        }

        isExpanded(): boolean {
            return this.expanded;
        }

        setExpanded(expanded: boolean = true): TreeNodeBuilder<NODE> {
            this.expanded = expanded;
            return this;
        }

        isSelected(): boolean {
            return this.selected;
        }

        setSelected(selected: boolean = true): TreeNodeBuilder<NODE> {
            this.selected = selected;
            return this;
        }

        isPinned(): boolean {
            return this.pinned;
        }

        setPinned(pinned: boolean = true): TreeNodeBuilder<NODE> {
            this.pinned = pinned;
            return this;
        }

        getMaxChildren(): number {
            return this.maxChildren;
        }

        setMaxChildren(maxChildren: number) {
            this.maxChildren = maxChildren;
        }

        getData(): NODE {
            return this.data;
        }

        getDataId(): string {
            return this.dataId;
        }

        setData(data: NODE, dataId: string): TreeNodeBuilder<NODE> {
            this.data = data;
            this.dataId = dataId;
            return this;
        }

        getParent(): TreeNode<NODE> {
            return this.parent;
        }

        setParent(parent: TreeNode<NODE>): TreeNodeBuilder<NODE> {
            this.parent = parent;
            return this;
        }

        getChildren(): TreeNode<NODE>[] {
            return this.children;
        }

        setChildren(children: TreeNode<NODE>[]): TreeNodeBuilder<NODE> {
            this.children = children;
            return this;
        }

        build(): TreeNode<NODE> {
            return new TreeNode<NODE>(this);
        }
    }
}
