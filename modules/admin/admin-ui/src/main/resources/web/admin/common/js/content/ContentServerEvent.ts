module api.content {

    export interface NodeEventJson extends api.app.EventJson {
        data: NodeEventDataJson;
    }

    export interface NodeEventDataJson {
        nodes: Event2NodeJson[];
    }

    export interface Event2NodeJson {
        id: string;
        path: string;
        newPath: string;
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

        static fromJson(nodeEventJson: NodeEventJson): ContentServerEvent {
            var changes = ContentServerChange.fromJson(nodeEventJson);
            return new ContentServerEvent(changes);
        }
    }
}