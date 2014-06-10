module app.browse.grid {

    import Item = api.item.Item;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DataView = api.ui.grid.DataView;
    import Grid = api.ui.grid.Grid;

    export class ContentGridCache2 {

        private grid:Grid<ContentSummary>;

        private expanded:ContentGridCacheItem2[];

        private selected:string[];

        constructor(contentGrid: Grid<ContentSummary>) {
            this.expanded = [];
            this.grid = contentGrid;
        }

        /*
         * Create a tree of expanded elements with `path` as root path.
         */
        getExpandedTree(path: ContentPath): ContentGridCacheItem2[] {
            var tree:ContentGridCacheItem2[] = [];

            this.expanded.forEach((elem:ContentGridCacheItem2) => {
                if (elem.getPath().isChildOf(path) || elem.getPath().toString() === path.toString()) {
                    tree.push(elem);
                } else {
                    for (var i = 0; i < tree.length; i++) {
                        if (elem.getPath().isChildOf(tree[i].getPath()) && tree[i].isExpanded()) {
                            tree.push(elem);
                            break;
                        }
                    }
                }
            });

            return tree;
        }

        expand(item?:ContentSummary):ContentGridCacheItem2[] {
            this.addExpanded(item);
            return this.getExpandedTree(item ? item.getPath() : ContentPath.ROOT);
        }

        collapse(item: Item) {
            for (var i = 0; i < this.expanded.length; i++) {
                if (this.expanded[i].getId() === item.getId()) {
                    this.expanded[i].setExpanded(false);
                    break;
                }
            }
        }

        /*
         Adds new expanded element, sort and ensure it is unique
         Expanded elements can contain root path
         */
        addExpanded(item?:ContentSummary) {
            var itemId, itemPath;
            if (item) {
                itemId = item.getId();
                itemPath = item.getPath();
            } else {
                itemId = "";
                itemPath = new ContentPath([]);
            }
            this.expanded.push(new ContentGridCacheItem2(itemId, itemPath));

            // Sort
            this.expanded = this.expanded.sort((a, b) => {
                var left = a.getPath().toString(),
                    right = b.getPath().toString();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });
            // Filter - remove duplicates
            var filtered = [];
            this.expanded.forEach((elem, index) => {
                if (index === 0 || elem.getId() !== this.expanded[index-1].getId()) {
                    filtered.push(elem);
                }
            });
            this.expanded = filtered;
        }

        /*
         Removes specific element
         */
        removeExpanded(item: Item) {
            for (var i = 0; i < this.expanded.length; i++) {
                if (item.getId() === this.expanded[i].getId()) {
                    this.expanded = this.expanded.slice(0, i).concat(this.expanded.slice(i+1));
                    break;
                }
            }
        }

        isExpanded(item: Item): boolean {
            for (var i = 0; i < this.expanded.length; i++) {
                if (this.expanded[i].getId() === item.getId()) {
                    return this.expanded[i].isExpanded();
                }
            }
            return false;
        }

        getSelected():string[] {
            return this.selected;
        }
        /*
         Saves path of the selected items
         */
        saveSelected() {
            this.selected = [];
            this.grid.getDataView().getItems().forEach((elem)=>{
                if (this.grid.getSelectedRows().indexOf(this.grid.getDataView().getRowById(elem.getId())) >= 0) {
                    this.selected.push(elem.getPath().toString());
                }
            });
        }

        /*
         Loads and updates selected rows
         Selected rows may change order with update, so they need check by item path.
         */
        loadSelected() {
            var selectedRows:number[] = [];
            this.grid.getDataView().getItems().forEach((elem) => {
                if (this.selected.indexOf(elem.getPath().toString()) >= 0) {
                    selectedRows.push(this.grid.getDataView().getRowById(elem.getId()));
                }
            });
            this.grid.setSelectedRows(selectedRows);
        }


    }
}
