module api.content.event {

    export class ActiveContentVersionSetEvent extends api.event.Event {

        private contentId: api.content.ContentId;
        private versionId: string;

        constructor(contentId: api.content.ContentId, versionId: string) {
            this.contentId = contentId;
            this.versionId = versionId;
            super();
        }

        getContentId(): api.content.ContentId {
            return this.contentId;
        }

        getVersionId(): string {
            return this.versionId;
        }

        static on(handler: (event: ActiveContentVersionSetEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ActiveContentVersionSetEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
