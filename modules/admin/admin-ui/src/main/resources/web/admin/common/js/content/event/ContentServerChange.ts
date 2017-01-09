module api.content.event {

    import NodeEventJson = api.event.NodeEventJson;
    import NodeEventNodeJson = api.event.NodeEventNodeJson;
    import NodeServerChange = api.event.NodeServerChange;
    import NodeServerChangeType = api.event.NodeServerChangeType;
    import NodeServerChangeItem = api.event.NodeServerChangeItem;

    export class ContentServerChangeItem extends NodeServerChangeItem<ContentPath> {

        contentId: api.content.ContentId;

        constructor(contentPath: api.content.ContentPath, branch: string, contentId: api.content.ContentId) {
            super(contentPath, branch);
            this.contentId = contentId;
        }

        getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static fromJson(node: NodeEventNodeJson): ContentServerChangeItem {
            return new ContentServerChangeItem(api.content.ContentPath.fromString(node.path.substr("/content".length)),
                node.branch, new api.content.ContentId(node.id));
        }
    }

    export class ContentServerChange extends NodeServerChange<ContentPath> {

        protected changeItems: ContentServerChangeItem[];

        protected newContentPaths: ContentPath[];

        constructor(type: NodeServerChangeType, changeItems: ContentServerChangeItem[], newContentPaths?: ContentPath[]) {
            super(type, changeItems, newContentPaths);
        }

        getChangeItems(): ContentServerChangeItem[] {
            return <ContentServerChangeItem[]>this.changeItems;
        }

        getNewContentPaths(): ContentPath[] {
            return this.newContentPaths;
        }

        toString(): string {
            return NodeServerChangeType[this.type] + ": <" +
                   this.changeItems.map((item) => item.getPath().toString()).join(", ") + !!this.newContentPaths
                ? this.newContentPaths.map((contentPath) => contentPath.toString()).join(", ")
                : "" +
                  ">";
        }

        static fromJson(nodeEventJson: NodeEventJson): ContentServerChange {

            let changeItems = nodeEventJson.data.nodes.
                filter((node) => node.path.indexOf("/content") === 0).
                map((node: NodeEventNodeJson) => ContentServerChangeItem.fromJson(node));

            if (changeItems.length === 0) {
                return null;
            }

            let nodeEventType = this.getNodeServerChangeType(nodeEventJson.type);

            if (NodeServerChangeType.MOVE == nodeEventType || NodeServerChangeType.RENAME == nodeEventType) {

                let newContentPaths = nodeEventJson.data.nodes.
                    filter((node) => node.newPath.indexOf("/content") === 0).
                    map((node: NodeEventNodeJson) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));

                return new ContentServerChange(nodeEventType, changeItems, newContentPaths);
            } else {
                return new ContentServerChange(nodeEventType, changeItems);
            }
        }
    }
}