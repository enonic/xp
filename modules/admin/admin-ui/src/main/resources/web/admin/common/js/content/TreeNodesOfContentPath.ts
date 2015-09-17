module api.content {

    import TreeNode = api.ui.treegrid.TreeNode;

    export class TreeNodesOfContentPath {

        private path: ContentPath;

        private altPath: ContentPath;

        private nodes: TreeNode<ContentSummaryAndCompareStatus>[];

        constructor(path: ContentPath, altPath?: ContentPath) {
            this.path = path;
            this.altPath = altPath;
            this.nodes = [];
        }

        getPath(): ContentPath {
            return this.path;
        }

        getAltPath(): ContentPath {
            return this.altPath;
        }

        getNodes(): TreeNode<ContentSummaryAndCompareStatus>[] {
            return this.nodes;
        }

        hasNodes(): boolean {
            return this.nodes.length > 0;
        }

        getId(): string {
            return (this.hasNodes() && this.nodes[0].getData()) ? this.nodes[0].getData().getId() : "";
        }

        updateNodeData(data: ContentSummaryAndCompareStatus) {
            this.nodes.forEach((node) => {
                node.setData(data);
                node.clearViewers();
            });
        }
    }
}