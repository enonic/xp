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

    interface ContentServerEventItemJson {
        t: string;
        p: string[];
    }

    export interface ContentServerEventJson {
        changes: ContentServerEventItemJson[];
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

        static fromJson(json: ContentServerEventItemJson): ContentServerChange {
            var contentEventType;

            switch (json.t) {
            case 'P':
                contentEventType = ContentServerChangeType.PUBLISH;
                break;
            case 'C':
                contentEventType = ContentServerChangeType.CREATE;
                break;
            case 'U':
                contentEventType = ContentServerChangeType.UPDATE;
                break;
            case 'X':
                contentEventType = ContentServerChangeType.DELETE;
                break;
            case 'D':
                contentEventType = ContentServerChangeType.DUPLICATE;
                break;
            case 'A':
                contentEventType = ContentServerChangeType.PENDING;
                break;
            case 'R':
                contentEventType = ContentServerChangeType.RENAME;
                break;
            case 'S':
                contentEventType = ContentServerChangeType.SORT;
                break;
            default:
                contentEventType = ContentServerChangeType.UNKNOWN;
            }
            var contentPaths = json.p.map((contentPath) => api.content.ContentPath.fromString(contentPath));
            return new ContentServerChange(contentPaths, contentEventType);
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

        static fromJson(json: ContentServerEventJson): ContentServerEvent {
            var changes = json.changes.map((changeJson) => ContentServerChange.fromJson(changeJson));
            return new ContentServerEvent(changes);
        }
    }

}