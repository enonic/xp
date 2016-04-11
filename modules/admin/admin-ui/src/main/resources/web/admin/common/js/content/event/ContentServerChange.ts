module api.content.event {

    export enum ContentServerChangeType {
        UNKNOWN,
        PUBLISH,
        DUPLICATE,
        CREATE,
        UPDATE,
        DELETE,
        PENDING,
        RENAME,
        SORT,
        MOVE
    }

    export class ContentServerChangeItem {

        contentPath: api.content.ContentPath;

        contentId: api.content.ContentId;

        constructor(contentPath: api.content.ContentPath, contentId: api.content.ContentId) {
            this.contentPath = contentPath;
            this.contentId = contentId;
        }

        getContentPath(): api.content.ContentPath {
            return this.contentPath;
        }

        getContentId(): api.content.ContentId {
            return this.contentId;
        }

        static fromJson(node: NodeEventNodeJson): ContentServerChangeItem {
            return new ContentServerChangeItem(api.content.ContentPath.fromString(node.path.substr("/content".length)),
                new api.content.ContentId(node.id));
        }
    }

    export class ContentServerChange {

        private changeItems: ContentServerChangeItem[];

        private newContentPaths: ContentPath[];

        private type: ContentServerChangeType;

        constructor(type: ContentServerChangeType, changeItems: ContentServerChangeItem[], newContentPaths?: ContentPath[]) {
            this.changeItems = changeItems;
            this.type = type;
            this.newContentPaths = newContentPaths;
        }

        getChangeItems(): ContentServerChangeItem[] {
            return this.changeItems;
        }

        getNewContentPaths(): ContentPath[] {
            return this.newContentPaths;
        }

        getChangeType(): ContentServerChangeType {
            return this.type;
        }

        toString(): string {
            return ContentServerChangeType[this.type] + ": <" +
                   this.changeItems.map((contentPath) => contentPath.toString()).join(", ") + !!this.newContentPaths
                ? this.newContentPaths.map((contentPath) => contentPath.toString()).join(", ")
                : "" +
                  ">";
        }

        static fromJson(nodeEventJson: NodeEventJson): ContentServerChange {
            var contentEventType;

            var changeItems = nodeEventJson.data.nodes.
                filter((node) => node.path.indexOf("/content") === 0).
                map((node: NodeEventNodeJson) => ContentServerChangeItem.fromJson(node));

            if (changeItems.length === 0) {
                return null;
            }

            switch (nodeEventJson.type) {
            case 'node.pushed':
                contentEventType = ContentServerChangeType.PUBLISH;
                break;
            case 'node.created':
                contentEventType = ContentServerChangeType.CREATE;
                break;
            case 'node.updated':
                contentEventType = ContentServerChangeType.UPDATE;
                break;
            case 'node.deleted':
                contentEventType = ContentServerChangeType.DELETE;
                break;
            case 'node.duplicated':
                contentEventType = ContentServerChangeType.DUPLICATE;
                break;
            case 'node.stateUpdated':
                contentEventType = ContentServerChangeType.PENDING;
                break;
            case 'node.moved':
                var newContentPaths = nodeEventJson.data.nodes.
                    filter((node) => node.newPath.indexOf("/content") === 0).
                    map((node: NodeEventNodeJson) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                return new ContentServerChange(ContentServerChangeType.MOVE, changeItems, newContentPaths);
            case 'node.renamed':
                var newContentPaths = nodeEventJson.data.nodes.
                    filter((node) => node.newPath.indexOf("/content") === 0).
                    map((node: NodeEventNodeJson) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                return new ContentServerChange(ContentServerChangeType.RENAME, changeItems, newContentPaths);
            case 'node.sorted':
                contentEventType = ContentServerChangeType.SORT;
                break;
            default:
                contentEventType = ContentServerChangeType.UNKNOWN;
            }

            return new ContentServerChange(contentEventType, changeItems);
        }
    }
}