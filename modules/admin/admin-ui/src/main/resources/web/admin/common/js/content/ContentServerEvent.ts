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

    export interface ContentServerEventItemJson {
        t: string;
        p: string[];
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

            var contentPaths = event2Json.data.nodes.map((node) => api.content.ContentPath.fromString(node.path.substr("/content".length)));

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
                var newContentPaths = event2Json.data.nodes.map((node) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
                var deletedContentServerChange = new ContentServerChange(contentPaths, ContentServerChangeType.DELETE);
                var createdContentServerChange = new ContentServerChange(newContentPaths, ContentServerChangeType.CREATE);
                return [deletedContentServerChange, createdContentServerChange];
            case 'node.renamed':
                var newContentPaths = event2Json.data.nodes.map((node) => api.content.ContentPath.fromString(node.newPath.substr("/content".length)));
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

    export class ContentServerEvent extends api.event.Event {

        private changes: ContentServerChange[];

        constructor(changes: ContentServerChange[]) {
            super();
            this.changes = changes || [];
        }

        getContentChanges(): ContentServerChange[] {
            return this.changes;
        }

        toString(): string {
            return "ContentServerEvent: [" +
                   this.changes.map((change) => change.toString()).join(", ") +
                   "]";
        }

        static on(handler: (event: ContentServerEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentServerEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: api.app.Event2Json): ContentServerEvent {
            var changes = ContentServerChange.fromJson(json);
            return new ContentServerEvent(changes);
        }
    }

}