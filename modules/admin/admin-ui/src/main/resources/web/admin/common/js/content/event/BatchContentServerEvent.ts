module api.content.event {

    export class BatchContentServerEvent extends api.event.Event {

        private events: ContentServerEvent[];

        private type: ContentServerChangeType;

        constructor(events: ContentServerEvent[], type: ContentServerChangeType) {
            super();
            this.events = events || [];
            this.type = type;
        }

        getEvents(): ContentServerEvent[] {
            return this.events;
        }

        getType(): ContentServerChangeType {
            return this.type;
        }

        toString(): string {
            return "BatchContentServerEvent: [" +
                   this.events.map((event) => event.toString()).join(", ") +
                   "]";
        }

        static on(handler: (event: BatchContentServerEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: BatchContentServerEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}