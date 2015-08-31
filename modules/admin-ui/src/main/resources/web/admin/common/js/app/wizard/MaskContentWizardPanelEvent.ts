module api.app.wizard {

    import ContentId = api.content.ContentId;

    export class MaskContentWizardPanelEvent extends api.event.Event {

        private contentId: ContentId;

        private mask: boolean;

        constructor(contentId: ContentId, mask: boolean = true) {
            super();

            this.contentId = contentId;
            this.mask = mask;
        }

        isMask(): boolean {
            return this.mask;
        }

        getContentId(): ContentId {
            return this.contentId;
        }

        static on(handler: (event: MaskContentWizardPanelEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: MaskContentWizardPanelEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}