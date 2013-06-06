module api_ui_dialog{

    export class ModalDialog extends api_ui.Component {

        private title:ModalDialogTitle;

        private contentPanel:ModalDialogContentPanel;

        private buttonRow:ModalDialogButtonRow;

        private closeAction:api_action.Action;

        private width:number = 700;

        private height:number = 500;

        constructor(title:string) {

            super("ModalDialog", "div");
            this.getEl().setZindex(30001);
            this.getEl().addClass("modal-dialog");
            this.getEl().addClass("display-none");
            this.getEl().setWidth(this.width + "px");
            this.getEl().setHeight(this.height + "px");

            // center element...
            this.getEl().setPosition("fixed");
            this.getEl().setTop("50%");
            this.getEl().setLeft("50%");
            this.getEl().setMarginLeft("-" + (this.width / 2) + "px");
            this.getEl().setMarginTop("-" + (this.height / 2) + "px");

            this.title = new ModalDialogTitle(title);
            this.appendChild(this.title);

            this.contentPanel = new ModalDialogContentPanel();
            this.appendChild(this.contentPanel);

            this.closeAction = new api_action.Action("Close");
            this.closeAction.addExecutionListener(() => {
                this.close();
            });

        }

        addToButtonRow(comp:api_ui.Component) {
            this.buttonRow.appendChild(comp);
        }

        close() {

            api_ui.BodyMask.get().deActivate();

            this.getEl().removeClass("display-block");
            this.getEl().addClass("display-none");
            Mousetrap.unbind('esc');
        }

        open() {

            api_ui.BodyMask.get().activate();

            this.getEl().removeClass("display-none");
            this.getEl().addClass("display-block");
            Mousetrap.bind('esc', () => {
                this.close();
            });
        }
    }

    export class ModalDialogTitle extends api_ui.Component {

        constructor(title:string) {
            super("ModalDialogTitle", "h2");
            this.getEl().setInnerHtml(title);
        }
    }

    export class ModalDialogContentPanel extends api_ui.Component {

        constructor() {
            super("ModalDialogContentPanel", "div");
        }
    }

    export class ModalDialogButtonRow extends api_ui.Component {

        constructor() {
            super("ModalDialogButtonRow", "div");
        }
    }
}
