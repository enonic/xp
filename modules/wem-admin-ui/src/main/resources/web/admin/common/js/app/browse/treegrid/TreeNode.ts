module api.app.browse.treegrid {

    import Item = api.item.Item;

    export class TreeNode<T extends Item> {

        private item:T;

        private expanded:boolean;

        private selected:boolean;

        private parent:TreeNode<T>;

        private children:TreeNode<T>[];

        constructor(item?:T, parent?:TreeNode<T>,
                    expanded:boolean = false,
                    selected:boolean = false) {
            this.item = item;
            this.parent = parent;
            this.children = [];
            this.expanded = expanded;
            this.selected = selected;
        }

        isExpanded():boolean {
            return this.expanded;
        }

        setExpanded(expanded:boolean = true) {
            this.expanded = expanded;
        }

        isSelected():boolean {
            return this.selected;
        }

        setSelected(selected:boolean = true) {
            this.selected = selected;
        }

        getItem():T {
            return this.item;
        }

        setItem(item:T) {
            this.item = item;
        }

        getParent():TreeNode<T> {
            return this.parent;
        }

        setParent(parent:TreeNode<T>) {
            this.parent = parent;
        }

        getChildren():TreeNode<T>[] {
            return this.children;
        }

        setChildren(children:TreeNode<T>[]) {
            this.children = children;

            this.children.forEach((child) => {
                child.setParent(this);
            });
        }

        setChildrenFromItems(children:T[]) {
            this.children = [];

            children.forEach((child) => {
                this.children.push(new TreeNode<T>(child, this));
            });
        }

        /*
         Element is visible, if all parents are expanded
         */
        isVisible():boolean {
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
            @expanded - determines to display only reachable nodes.
            @selected - determines to display only seleted nodes.
         */
        treeToList(expanded:boolean = true, selected: boolean = false):TreeNode<T>[] {
            var list:TreeNode<T>[] = [];

            if (this.selected === true || selected === false) {
                list.push(this);
            }

            if (this.expanded === true || expanded === false) {
                this.children.forEach((child) => {
                    list = list.concat(child.treeToList(expanded, selected));
                });
            }

            return list;
        }

        /*
         Maps Node's list to Item's list.
         */
        treeToItemList(expanded:boolean = true, selected: boolean = false):T[] {
            var list:T[] = this.treeToList(expanded, selected)
                .map((node) => { return node.getItem(); })
                .filter((item) => { return item != null; });

            return list;
        }

        findNode(item:T):TreeNode<T> {

            if (this.item && this.item.getId() === item.getId()) {
                return this;
            }

            for (var i = 0; i < this.children.length; i++) {
                var node:TreeNode<T> = this.children[i].findNode(item);
                if (node) {
                    return node;
                }
            }

            return null;
        }

        calcLevel():number {
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
