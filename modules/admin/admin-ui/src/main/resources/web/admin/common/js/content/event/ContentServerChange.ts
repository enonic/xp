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

    export class ContentServerChange {

        private contentPaths: ContentPath[];

        private newContentPaths: ContentPath[];

        private type: ContentServerChangeType;

        constructor(type: ContentServerChangeType, contentPaths: ContentPath[], newContentPaths?: ContentPath[]) {
            this.contentPaths = contentPaths;
            this.type = type;
            this.newContentPaths = newContentPaths;
        }

        getContentPaths(): ContentPath[] {
            return this.contentPaths;
        }

        getNewContentPaths(): ContentPath[] {
            return this.newContentPaths;
        }

        getChangeType(): ContentServerChangeType {
            return this.type;
        }

        toString(): string {
            return ContentServerChangeType[this.type] + ": <" +
                   this.contentPaths.map((contentPath) => contentPath.toString()).join(", ") + !!this.newContentPaths
                ? this.newContentPaths.map((contentPath) => contentPath.toString()).join(", ")
                : "" +
                  ">";
        }

        static fromJson(nodeEventJson: NodeEventJson): ContentServerChange {
            var contentEventType;

            var contentPaths = nodeEventJson.data.nodes.
                filter((node) => node.path.indexOf("/content") === 0).
                map((node: NodeEventNodeJson) => api.content.ContentPath.fromString(node.path.substr("/content".length)));

            if (contentPaths.length === 0) {
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
                return new ContentServerChange(ContentServerChangeType.MOVE, contentPaths, newContentPaths);
            case 'node.renamed':
                var newContentPaths = nodeEventJson.data.nodes.
                    filter((node) => node.newPath.indexOf("/content") === 0).
                    map((node: NodeEventNodeJson) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                return new ContentServerChange(ContentServerChangeType.RENAME, contentPaths, newContentPaths);
            case 'node.sorted':
                contentEventType = ContentServerChangeType.SORT;
                break;
            default:
                contentEventType = ContentServerChangeType.UNKNOWN;
            }

            return new ContentServerChange(contentEventType, contentPaths);
        }
    }
}