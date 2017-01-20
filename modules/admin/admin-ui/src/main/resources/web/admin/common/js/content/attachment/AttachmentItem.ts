module api.content.attachment {

    export class AttachmentItem extends api.dom.DivEl {

        private link: api.dom.AEl;

        private removeEl: api.dom.DivEl;

        private value: string;

        constructor(contentId: string, value: string, removeCallback?: (value: any) => void) {
            super('attachment-item');

            this.value = value;

            this.link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + contentId + '/' + value));
            this.link.setHtml(value);

            this.initRemoveButton(removeCallback);
        }

        private initRemoveButton(callback?: (value: any) => void) {
            this.removeEl = new api.dom.DivEl('icon remove');

            this.removeEl.onClicked(() => {
                if (callback) {
                    callback(this.value);
                    this.remove();
                }
            });
        }

        getValue(): string {
            return this.value;
        }

        doRender(): wemQ.Promise<boolean> {
            return super.doRender().then((rendered) => {

                this.removeChildren();
                this.appendChildren(this.removeEl, this.link);

                return rendered;
            });
        }
    }
}
