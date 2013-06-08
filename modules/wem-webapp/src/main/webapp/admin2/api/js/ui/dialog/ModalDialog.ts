module api_ui_dialog{

    export interface ModalDialogConfig {

        title:string;
        width:number;
        height:number;
    }

    export class ModalDialog extends api_ui.Component {

        private config:ModalDialogConfig;

        private title:ModalDialogTitle;

        private contentPanel:ModalDialogContentPanel;

        private buttonRow:ModalDialogButtonRow;

        constructor(config:ModalDialogConfig) {

            super("ModalDialog", "div");
            this.config = config;
            var el = this.getEl();
            el.setDisplay("none").addClass("modal-dialog");
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

        setTitle(value:string) {
            this.title.setTitle(value);
        }

        appendChildToContentPanel(child:api_ui.Component) {
            this.contentPanel.appendChild(child);
        }

        addAction(action:api_action.Action) {
            this.buttonRow.addAction(action);
        }

        close() {

            api_ui.BodyMask.get().deActivate();

            this.getEl().setDisplay("none");
            Mousetrap.unbind('esc');
        }

        open() {

            api_ui.BodyMask.get().activate();

            this.getEl().setDisplay("block");
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

        setTitle(value:string) {
            this.getEl().setInnerHtml(value);
        }
    }

    export class ModalDialogContentPanel extends api_ui.Component {

        constructor() {
            super("ModalDialogContentPanel", "div");
            this.getEl().addClass("modal-dialog-content-panel")
        }
    }

    export class ModalDialogButtonRow extends api_ui.Component {

        constructor() {
            super("ModalDialogButtonRow", "div");
            this.getEl().addClass("modal-dialog-button-row")
        }

        addAction(action:api_action.Action) {

            var button = new ModalDialogButton(action);
            this.appendChild(button);
        }
    }

    export class ModalDialogButton extends api_ui.AbstractButton {

        private action:api_action.Action;

        constructor(action:api_action.Action) {
            super("ModalDialogButton", action.getLabel());
            this.action = action;

            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });
            super.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:api_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }
    }

    export class ModalDialogCancelAction extends api_action.Action {
        constructor() {
            super("Cancel");
        }
    }
}
