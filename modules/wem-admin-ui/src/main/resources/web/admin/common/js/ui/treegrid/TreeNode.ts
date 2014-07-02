module api.ui.treegrid {

    export class TreeNode<NODE extends TreeItem> {

        private id: string;

        private data: NODE;

        private expanded: boolean;

        private selected: boolean;

        private parent: TreeNode<NODE>;

        private children: TreeNode<NODE>[];

        constructor(data?: NODE, parent?: TreeNode<NODE>, expanded: boolean = false, selected: boolean = false) {
            this.id = data ? data.getId() : "";
            this.data = data;
            this.parent = parent;
            this.children = [];
            this.expanded = expanded;
            this.selected = selected;
        }

        getId():string {
            return this.id;
        }

        isExpanded(): boolean {
            return this.expanded;
        }

        setExpanded(expanded: boolean = true) {
            this.expanded = expanded;
        }

        isSelected(): boolean {
            return this.selected;
        }

        setSelected(selected: boolean = true) {
            this.selected = selected;
        }

        getData(): NODE {
            return this.data;
        }

        setData(node: NODE) {
            this.data = node;
        }

        getParent(): TreeNode<NODE> {
            return this.parent;
        }

        setParent(parent: TreeNode<NODE>) {
            this.parent = parent;
        }

        getChildren(): TreeNode<NODE>[] {
            return this.children;
        }

        setChildren(children: TreeNode<NODE>[]) {
            this.children = children;

            this.children.forEach((child) => {
                child.setParent(this);
            });
        }

        hasChildren(): boolean {
            return this.children.length > 0;
        }

        setChildrenFromItems(children: NODE[]) {
            this.children = [];

            children.forEach((child) => {
                this.children.push(new TreeNode<NODE>(child, this));
            });
        }

        /*
         Element is visible, if all parents are expanded
         */
        isVisible(): boolean {
            var visible = true;
            var parent = this.parent;
            while (parent && visible) {
                visible = parent.isExpanded();
                parent = parent.getParent();
            }
            return visible;
        }

        /*
         Transforms tree into the list of nodes with current node as root.
         @empty    - determines to get nodes with empty data.
         @expanded - determines to display only reachable nodes.
         @selected - determines to display only seleted nodes.
         */
        treeToList(empty: boolean = false,
                   expanded: boolean = true,
                   selected: boolean = false): TreeNode<NODE>[] {
            var list: TreeNode<NODE>[] = [];

            if (this.selected === true || selected === false) {
                if (this.getData() || empty === true) {
                    list.push(this);
                }
            }

            if (this.expanded === true || expanded === false) {
                this.children.forEach((child) => {
                    list = list.concat(child.treeToList(empty, expanded, selected));
                });
            }

            return list;
        }

        /*
         Maps Node's list to Item's list.
         */
        treeToItemList(empty: boolean = false,
                       expanded: boolean = true,
                       selected: boolean = false): NODE[] {
            var list: NODE[] = this.treeToList(empty, expanded, selected)
                .map((node) => {
                    return node.getData();
                })
                .filter((item) => {
                    return item != null;
                });

            return list;
        }

        findNode(data: NODE): TreeNode<NODE> {

            if (this.data && this.data.getId() === data.getId()) {
                return this;
            }

            for (var i = 0; i < this.children.length; i++) {
                var child: TreeNode<NODE> = this.children[i].findNode(data);
                if (child) {
                    return child;
                }
            }

            return null;
        }

        calcLevel(): number {
            var parent = this.parent,
                lvl = 0;
            while (parent) {
                parent = parent.getParent();
                lvl++;
            }

            return lvl;
        }
    }
}
