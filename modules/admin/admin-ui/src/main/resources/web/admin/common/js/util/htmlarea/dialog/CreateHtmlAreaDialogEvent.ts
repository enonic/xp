module api.util.htmlarea.dialog {

    import ContentId = api.content.ContentId;
    import ContentPath = api.content.ContentPath;

    export enum HtmlAreaDialogType {
        ANCHOR, IMAGE, LINK, MACRO
    }

    export class CreateHtmlAreaDialogEvent extends api.event.Event {

        private config: any;

        private type: HtmlAreaDialogType;

        private contentId: ContentId;

        private contentPath: ContentPath;

        constructor(builder: HtmlAreaDialogShownEventBuilder) {
            super();

            this.config = builder.config;
            this.type = builder.type;
            this.contentId = builder.contentId;
            this.contentPath = builder.contentPath;
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

        getContentPath(): ContentPath {
            return this.contentPath;
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

        contentPath: ContentPath;

        setContentId(contentId: ContentId): HtmlAreaDialogShownEventBuilder {
            this.contentId = contentId;
            return this;
        }

        setContentPath(contentPath: ContentPath): HtmlAreaDialogShownEventBuilder {
            this.contentPath = contentPath;
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