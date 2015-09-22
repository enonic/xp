module api.content {

    export class ContentsPublishedEvent extends api.event.Event {

        private contentIds: api.content.ContentId[];

        constructor(contentIds: api.content.ContentId[]) {
            super();
            this.contentIds = contentIds;
        }

        public getContentIds(): api.content.ContentId[] {
            return this.contentIds;
        }

        static on(handler: (event: ContentsPublishedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentsPublishedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}