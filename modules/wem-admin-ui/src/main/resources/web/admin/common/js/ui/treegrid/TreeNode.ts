module api.ui.treegrid {

    export class TreeNode<NODE extends TreeItem> {

        private id: string;

        private data: NODE;

        private expanded: boolean;

        private selected: boolean;

        private pinned: boolean;

        private parent: TreeNode<NODE>;

        private children: TreeNode<NODE>[];

        constructor(builder: TreeNodeBuilder<NODE>) {
            this.id = Math.random().toString(36).substring(2);
            this.data = builder.getData();
            this.parent = builder.getParent();
            this.setChildren(builder.getChildren());
            this.expanded = builder.isExpanded();
            this.selected = builder.isSelected();
            this.pinned = builder.isPinned();
            if (this.pinned) {
                this.pinToRoot();
            }
        }

        getId(): string {
            return this.id;
        }

        regenerateIds(): void {
            this.id = Math.random().toString(36).substring(2);
            this.children.forEach((elem) => {
                elem.regenerateIds();
            });
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

        isPinned(): boolean {
            return this.pinned;
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
            if (this.pinned) {
                this.pinToRoot();
            }
        }

        getRoot(): TreeNode<NODE> {
            var root = this,
                parent = this.getParent();
            while (parent) {
                root = parent;
                parent = parent.getParent();
            }

            return root;
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
                this.children.push(new TreeNodeBuilder<NODE>().setData(child).setParent(this).build());
            });
        }

        addChild(child: TreeNode<NODE>) {
            this.children = this.children || [];
            this.children.push(child);
            child.setParent(this);
        }

        removeChild(child: TreeNode<NODE>) {
            var children: TreeNode<NODE>[] = [];
            for (var i = 0; i < this.children.length; i++) {
                if (this.children[i].getId() !== child.getId()) {
                    children.push(this.children[i]);
                }
            }
            this.children = children;
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

        pinToRoot() {
            // Not already in the root
            if (this.calcLevel() > 1 && this.data && this.parent) {
                var duplicated = false;
                var relatives = this.getRoot().getChildren();
                // check if duplicate is already in root
                for (var i = 0; i < relatives.length; i++) {
                    if (relatives[i].getData() && relatives[i].getData().getId() === this.getData().getId()) {
                        duplicated = true;
                        break;
                    }
                }

                if (!duplicated) {
                    if (this.pinned) {
                        this.parent.removeChild(this);
                        this.getRoot().addChild(this);
                    } else {
                        new TreeNodeBuilder<NODE>(this).setPinned(true).build();
                    }
                }
            }
        }
    }
}
