module api.content {

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