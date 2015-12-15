module api.content.event {

    export interface NodeEventJson extends api.app.EventJson {
        data: NodeEventDataJson;
    }

    export interface NodeEventDataJson {
        nodes: NodeEventNodeJson[];
    }

    export interface NodeEventNodeJson {
        id: string;
        path: string;
        newPath: string;
    }

    export class ContentServerEvent extends api.event.Event {

        private change: ContentServerChange;

        constructor(change: ContentServerChange) {
            super();
            this.change = change;
        }

        getContentChange(): ContentServerChange {
            return this.change;
        }

        toString(): string {
            return "ContentServerEvent: [" + this.change.toString() + "]";
        }

        static on(handler: (event: ContentServerEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentServerEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(nodeEventJson: NodeEventJson): ContentServerEvent {
            var change = ContentServerChange.fromJson(nodeEventJson);
            return new ContentServerEvent(change);
        }
    }
}