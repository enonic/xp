module api.ui.dialog{

    export interface ModalDialogConfig {
        title:api.ui.dialog.ModalDialogHeader;
    }

    export class ModalDialog extends api.dom.DivEl {

        private config:ModalDialogConfig;

        private title:api.ui.dialog.ModalDialogHeader;

        private contentPanel:ModalDialogContentPanel;

        private buttonRow:ModalDialogButtonRow;

        private cancelAction:api.ui.Action;

        private actions:api.ui.Action[] = [];

        constructor(config:ModalDialogConfig) {
            super("modal-dialog");

            this.config = config;

            this.getEl().setDisplay("none").setZindex(30001).
                setPosition("fixed").setTop("50%").setLeft("50%");

            this.title = this.config.title;
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
            this.centerMyself();
            // experimenting with transitions
            jQuery(this.getEl().getHTMLElement()).show(100);
        }

        private centerMyself() {
            var el = this.getEl();
            var jqel = $(this.getHTMLElement());
            el.setMarginLeft("-" + (jqel.outerWidth() / 2) + "px").
                setMarginTop("-" + (jqel.outerHeight() / 2) + "px");
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

    export class ModalDialogHeader extends api.dom.DivEl {

        private titleEl:api.dom.H2El;

        constructor(title:string) {
            super("dialog-header");

            this.titleEl = new api.dom.H2El('title');
            this.titleEl.setText(title);
            this.appendChild(this.titleEl);
        }

        setTitle(value:string) {
            this.titleEl.setText(value);
        }
    }

    export class ModalDialogContentPanel extends api.dom.DivEl {

        constructor() {
            super("dialog-content");
        }
    }

    export class ModalDialogButtonRow extends api.dom.DivEl {

        constructor() {
            super("dialog-buttons");
        }

        addAction(action:api.ui.Action):DialogButton {
            var button = new DialogButton(action);
            this.appendChild(button);
            return button;
        }
    }

}
