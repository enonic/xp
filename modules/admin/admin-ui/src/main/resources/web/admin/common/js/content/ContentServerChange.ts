module api.content {

    export enum ContentServerChangeType {
        UNKNOWN,
        PUBLISH,
        DUPLICATE,
        CREATE,
        UPDATE,
        DELETE,
        PENDING,
        RENAME,
        SORT
    }

    export class ContentServerChange {

        private contentPaths: api.content.ContentPath[];

        private type: ContentServerChangeType;

        constructor(contentPaths: api.content.ContentPath[], type: ContentServerChangeType) {
            this.contentPaths = contentPaths;
            this.type = type;
        }

        getContentPaths(): api.content.ContentPath[] {
            return this.contentPaths;
        }

        getChangeType(): ContentServerChangeType {
            return this.type;
        }

        toString(): string {
            return ContentServerChangeType[this.type] + ": <" +
                   this.contentPaths.map((contentPath) => contentPath.toString()).join(", ") +
                   ">";
        }

        static fromJson(event2Json: api.app.Event2Json): ContentServerChange[] {
            var contentEventType;

            var contentPaths = event2Json.data.nodes.
            filter((node) => node.path.indexOf("/content") === 0).
            map((node) => api.content.ContentPath.fromString(node.path.substr("/content".length)));

            if (contentPaths.length === 0) {
                return [];
            }

            switch (event2Json.type) {
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
                var newContentPaths = event2Json.data.nodes.
                filter((node) => node.newPath.indexOf("/content") === 0).
                map((node) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                var deletedContentServerChange = new ContentServerChange(contentPaths, ContentServerChangeType.DELETE);
                var createdContentServerChange = new ContentServerChange(newContentPaths, ContentServerChangeType.CREATE);
                return [deletedContentServerChange, createdContentServerChange];
            case 'node.renamed':
                var newContentPaths = event2Json.data.nodes.
                filter((node) => node.newPath.indexOf("/content") === 0).
                map((node) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                var renamedContentServerChange = new ContentServerChange(contentPaths, ContentServerChangeType.RENAME);
                var deletedContentServerChange = new ContentServerChange(contentPaths, ContentServerChangeType.DELETE);
                var createdContentServerChange = new ContentServerChange(newContentPaths, ContentServerChangeType.CREATE);
                return [renamedContentServerChange, deletedContentServerChange, createdContentServerChange];
            case 'node.sorted':
                contentEventType = ContentServerChangeType.SORT;
                break;
            default:
                contentEventType = ContentServerChangeType.UNKNOWN;
            }

            return [new ContentServerChange(contentPaths, contentEventType)];
        }
    }
}