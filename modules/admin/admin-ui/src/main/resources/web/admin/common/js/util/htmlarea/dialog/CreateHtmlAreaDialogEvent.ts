module api.util.htmlarea.dialog {

    import ContentId = api.content.ContentId;

    export enum HtmlAreaDialogType {
        ANCHOR,IMAGE,LINK
    }

    export class CreateHtmlAreaDialogEvent extends api.event.Event {

        private config: any;

        private type: HtmlAreaDialogType;

        private contentId: ContentId;

        constructor(builder: HtmlAreaDialogShownEventBuilder) {
            super();

            this.config = builder.config;
            this.type = builder.type;
            this.contentId = builder.contentId;
        }

        getConfig(): any {
            return this.config;
        }

        getType(): HtmlAreaDialogType {
            return this.type;
        }

        getContentId(): ContentId {
            return this.contentId;
        }

        static create(): HtmlAreaDialogShownEventBuilder {
            return new HtmlAreaDialogShownEventBuilder();
        }

        static on(handler: (event: CreateHtmlAreaDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: CreateHtmlAreaDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }

    export class HtmlAreaDialogShownEventBuilder {

        config: any;

        type: HtmlAreaDialogType;

        contentId: ContentId;

        setContentId(contentId: ContentId): HtmlAreaDialogShownEventBuilder {
            this.contentId = contentId;
            return this;
        }

        setType(type: HtmlAreaDialogType): HtmlAreaDialogShownEventBuilder {
            this.type = type;
            return this;
        }

        setConfig(config: any): HtmlAreaDialogShownEventBuilder {
            this.config = config;
            return this;
        }

        build(): CreateHtmlAreaDialogEvent {

            return new CreateHtmlAreaDialogEvent(this);
        }
    }
}