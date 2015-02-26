module api.ui.treegrid {

    export class TreeNode<DATA> {

        private id: string;

        private dataId: string;

        private data: DATA;

        private expanded: boolean;

        private selected: boolean;

        private pinned: boolean;

        private maxChildren: number;

        private parent: TreeNode<DATA>;

        private children: TreeNode<DATA>[];

        /**
         * A cache for stashing viewers by name, so that they can be reused.
         */
        private viewersByName: {[s:string] : api.ui.Viewer<any>; } = {};

        constructor(builder: TreeNodeBuilder<DATA>) {
            this.id = Math.random().toString(36).substring(2);
            this.dataId = builder.getDataId();
            this.data = builder.getData();
            this.parent = builder.getParent();
            this.setChildren(builder.getChildren());
            this.maxChildren = builder.getMaxChildren();
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

        hasData(): boolean {
            return !!this.data;
        }

        getDataId(): string {
            return this.dataId;
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

        getMaxChildren(): number {
            return this.maxChildren;
        }

        setMaxChildren(maxChildren: number) {
            this.maxChildren = maxChildren;
        }

        getData(): DATA {
            return this.data;
        }

        setData(data: DATA) {
            this.data = data;
        }

        setDataId(dataId: string) {
            this.dataId = dataId;
        }

        setViewer(name: string, viewer: api.ui.Viewer<any>) {
            this.viewersByName[name] = viewer;
        }

        clearViewers() {
            this.viewersByName = {};
        }

        getViewer(name: string): api.ui.Viewer<any> {
            return this.viewersByName[name];
        }

        getParent(): TreeNode<DATA> {
            return this.parent;
        }

        setParent(parent: TreeNode<DATA>) {
            this.parent = parent;
            if (this.pinned) {
                this.pinToRoot();
            }
        }

        hasParent(): boolean {
            return !!this.parent;
        }

        getRoot(): TreeNode<DATA> {
            var root = this,
                parent = this.getParent();
            while (parent) {
                root = parent;
                parent = parent.getParent();
            }

            return root;
        }

        getChildren(): TreeNode<DATA>[] {
            return this.children;
        }

        setChildren(children: TreeNode<DATA>[]) {
            this.children = children;

            this.children.forEach((child) => {
                child.setParent(this);
            });
        }

        hasChildren(): boolean {
            return this.children.length > 0;
        }

        regenerateId(): void {
            this.id = Math.random().toString(36).substring(2);
        }

        regenerateIds(): void {
            this.regenerateId();
            this.children.forEach((elem) => {
                elem.regenerateIds();
            });
        }

        addChild(child: TreeNode<DATA>, isToBegin?: boolean) {
            this.children = this.children || [];
            if (isToBegin) {
                this.children.unshift(child);
            } else {
                this.children.push(child);
            }
            this.clearViewers();
            child.setParent(this);
        }

        removeChild(child: TreeNode<DATA>) {
            var children: TreeNode<DATA>[] = [];
            for (var i = 0; i < this.children.length; i++) {
                if (this.children[i].getId() !== child.getId()) {
                    children.push(this.children[i]);
                }
            }
            this.children = children;

            if (this.children.length === 0) {
                this.expanded = false;
            }
        }

        remove() {
            if (this.parent) {
                this.parent.removeChild(this);
                this.parent.clearViewers();
                this.parent = null;
            }
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

        /**
         * Transforms tree into the list of nodes with current node as root.
         * @param empty    - determines to get nodes with empty data.
         * @param expanded - determines to display only reachable nodes.
         * @param selected - determines to display only seleted nodes.
         */
        treeToList(empty: boolean = false, expanded: boolean = true, selected: boolean = false): TreeNode<DATA>[] {
            var list: TreeNode<DATA>[] = [];

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

        findNode(dataId: string): TreeNode<DATA> {

            if (this.hasData() && this.getDataId() === dataId) {
                return this;
            }

            for (var i = 0; i < this.children.length; i++) {
                var child = this.children[i].findNode(dataId);
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
                    if (relatives[i].getData() && relatives[i].getDataId() === this.getDataId()) {
                        duplicated = true;
                        break;
                    }
                }

                if (!duplicated) {
                    if (this.pinned) {
                        this.parent.removeChild(this);
                        this.getRoot().addChild(this);
                    } else {
                        new TreeNodeBuilder<DATA>(this).setPinned(true).build();
                    }
                }
            }
        }
    }
}
