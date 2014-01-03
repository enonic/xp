module api.ui.dialog{

    export interface ModalDialogConfig {

        title:string;
        width:number;
        height:number;
        idPrefix?:string;
    }

    export class ModalDialog extends api.dom.DivEl {

        private config:ModalDialogConfig;

        private title:ModalDialogTitle;

        private contentPanel:ModalDialogContentPanel;

        private buttonRow:ModalDialogButtonRow;

        private cancelAction:api.ui.Action;

        private actions:api.ui.Action[] = [];

        constructor(config:ModalDialogConfig) {
            super(config.idPrefix != null ? config.idPrefix : "ModalDialog", "modal-dialog");

            this.config = config;
            var el = this.getEl();
            el.setDisplay("none");
            el.setWidth(this.config.width + "px").setHeight(this.config.height + "px");
            el.setZindex(30001);

            // center element...
            el.setPosition("fixed").
                setTop("50%").setLeft("50%").
                setMarginLeft("-" + (this.config.width / 2) + "px").
                setMarginTop("-" + (this.config.height / 2) + "px");

            this.title = new ModalDialogTitle(this.config.title);
            this.appendChild(this.title);

            this.contentPanel = new ModalDialogContentPanel();
            this.appendChild(this.contentPanel);

            this.buttonRow = new ModalDialogButtonRow();
            this.appendChild(this.buttonRow);
        }

        setCancelAction(action:api.ui.Action) {
            this.cancelAction = action;
            this.addAction(action);
        }

        getCancelAction():api.ui.Action {
            return this.cancelAction;
        }

        setTitle(value:string) {
            this.title.setTitle(value);
        }

        appendChildToContentPanel(child:api.dom.Element) {
            this.contentPanel.appendChild(child);
        }

        addAction(action:api.ui.Action) {
            this.actions.push(action);
            this.buttonRow.addAction(action);
        }

        show() {
            // experimenting with transitions
            jQuery(this.getEl().getHTMLElement()).show(100);
        }

        hide() {
            // experimenting with transitions
            jQuery(this.getEl().getHTMLElement()).hide(100);
        }

        close() {

            api.ui.BodyMask.get().deActivate();

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();
        }

        open() {

            api.ui.BodyMask.get().activate();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            api.ui.KeyBindings.get().bindKeys(api.ui.Action.getKeyBindings(this.actions));
        }
    }

    export class ModalDialogTitle extends api.dom.H2El {

        constructor(title:string) {
            super("ModalDialogTitle");
            this.getEl().setInnerHtml(title);
        }

        setTitle(value:string) {
            this.getEl().setInnerHtml(value);
        }
    }

    export class ModalDialogContentPanel extends api.dom.DivEl {

        constructor() {
            super("ModalDialogContentPanel", "content-panel");
        }
    }

    export class ModalDialogButtonRow extends api.dom.DivEl {

        constructor() {
            super("ModalDialogButtonRow", "button-row");
        }

        addAction(action:api.ui.Action) {

            var button = new DialogButton(action);
            this.appendChild(button);
        }
    }

}
