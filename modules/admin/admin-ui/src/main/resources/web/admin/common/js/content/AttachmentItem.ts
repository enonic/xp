module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    import Attachment = api.content.attachment.Attachment;
    import AttachmentJson = api.content.attachment.AttachmentJson;
    import AttachmentBuilder = api.content.attachment.AttachmentBuilder;
    import SelectionItem = api.app.browse.SelectionItem;


    export class AttachmentItem extends api.dom.DivEl {

        private view: api.app.NamesView;

        private removeEl: api.dom.DivEl;

        private value: string;

        constructor(value:string, removeCallback?: (value) => void) {
            super("attachment-item");
            this.value = value;
            this.view = new api.app.NamesView().setMainName(value);

            this.initRemoveButton(removeCallback);
        }

        private initRemoveButton(callback?: (value) => void) {
            this.removeEl = new api.dom.DivEl("icon remove");
            this.removeEl.onClicked(() => {
                if (callback) {
                    callback(this.value);
                    this.remove();
                }
            });
        }

        getValue() : string {
            return this.value;
        }

        doRender(): boolean {
            this.removeChildren();
            this.appendChild(this.removeEl);
            this.appendChild(this.view);

            return true;
        }
    }
}