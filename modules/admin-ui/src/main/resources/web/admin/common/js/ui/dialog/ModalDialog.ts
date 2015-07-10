module api.ui.dialog {

    export interface ModalDialogConfig {
        title: api.ui.dialog.ModalDialogHeader;
    }

    export class ModalDialog extends api.dom.DivEl {

        private config: ModalDialogConfig;

        private title: api.ui.dialog.ModalDialogHeader;

        private contentPanel: ModalDialogContentPanel;

        private buttonRow: ModalDialogButtonRow;

        private cancelAction: api.ui.Action;

        private actions: api.ui.Action[] = [];

        private mouseClickListener: {(MouseEvent): void};

        private responsiveItem: api.ui.responsive.ResponsiveItem;

        private cancelButton: api.ui.button.ActionButton;

        constructor(config: ModalDialogConfig) {
            super("modal-dialog");

            this.config = config;

            var wrapper = new api.dom.DivEl("modal-dialog-content-wrapper");
            this.appendChild(wrapper);

            this.createDefaultCancelAction();
            this.cancelButton = this.createCancelButton();

            wrapper.appendChild(this.cancelButton);

            this.title = this.config.title;
            wrapper.appendChild(this.title);

            this.contentPanel = new ModalDialogContentPanel();
            wrapper.appendChild(this.contentPanel);

            var push = new api.dom.DivEl("modal-dialog-content-push");
            wrapper.appendChild(push);

            var footer = new api.dom.DivEl("modal-dialog-footer");
            this.appendChild(footer);

            this.buttonRow = new ModalDialogButtonRow();
            footer.appendChild(this.buttonRow);

            this.mouseClickListener = (event: MouseEvent) => {
                if (this.isVisible()) {
                    for (var element = event.target; element; element = (<any>element).parentNode) {
                        if (element == this.getHTMLElement()) {
                            return;
                        }
                    }
                    if (this.cancelAction) {
                        this.cancelAction.execute();
                    }
                }
            };

            api.dom.Body.get().onMouseDown(this.mouseClickListener);

            this.onRemoved(() => api.dom.Body.get().unMouseDown(this.mouseClickListener));
            this.onAdded(() => api.dom.Body.get().onMouseDown(this.mouseClickListener));
            this.onShown(() => api.ui.responsive.ResponsiveManager.fireResizeEvent());

            this.responsiveItem =
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (this.isVisible()) {
                    this.centerMyself();
                }
            });
        }

        private createDefaultCancelAction() {
            var cancelAction = new api.ui.Action("Cancel", "esc");
            cancelAction.setIconClass("cancel-button-top");
            cancelAction.setLabel("");
            cancelAction.onExecuted(()=> {
                this.close();
            });
            this.cancelAction = cancelAction;
            this.actions.push(cancelAction);
        }

        getCancelAction(): api.ui.Action {
            return this.cancelAction;
        }

        addCancelButtonToBottom() {
            var cancelAction = new api.ui.Action("Cancel", "esc");
            cancelAction.setIconClass("cancel-button-bottom");
            cancelAction.onExecuted(()=> {
                this.close();
            });
            this.addAction(cancelAction);
        }

        setTitle(value: string) {
            this.title.setTitle(value);
        }

        appendChildToContentPanel(child: api.dom.Element) {
            this.contentPanel.appendChild(child);
        }

        appendChildToTitle(child: api.dom.Element) {
            this.title.appendChild(child);
        }

        removeChildFromContentPanel(child: api.dom.Element) {
            this.contentPanel.removeChild(child);
        }

        addAction(action: api.ui.Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            this.actions.push(action);
            return this.buttonRow.addAction(action, useDefault, prepend);
        }

        show() {
            super.show();
            this.centerMyself();
            this.buttonRow.focusDefaultAction();
        }

        protected centerMyself() {
            var el = this.getEl();
            el.setMarginTop("-" + (el.getHeightWithBorder() / 2) + "px");

            if (this.responsiveItem.isInRangeOrBigger(api.ui.responsive.ResponsiveRanges._540_720)) {
                el.setMarginLeft("-" + (el.getWidthWithBorder() / 2) + "px");
                el.addClass("centered_horizontally");
            } else {
                el.setMarginLeft("0px");
                el.removeClass("centered_horizontally");
            }
        }

        private createCancelButton(): api.ui.button.ActionButton {
            return new DialogButton(this.getCancelAction());
        }

        getButtonRow(): ModalDialogButtonRow {
            return this.buttonRow;
        }

        hide() {
            super.hide();
        }

        close() {

            api.ui.mask.BodyMask.get().hide();

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();
        }

        open() {

            api.ui.mask.BodyMask.get().show();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            api.ui.KeyBindings.get().bindKeys(api.ui.Action.getKeyBindings(this.actions));
        }
    }

    export class ModalDialogHeader extends api.dom.DivEl {

        private titleEl: api.dom.H2El;

        constructor(title: string) {
            super("dialog-header");

            this.titleEl = new api.dom.H2El('title');
            this.titleEl.setHtml(title);
            this.appendChild(this.titleEl);
        }

        setTitle(value: string) {
            this.titleEl.setHtml(value);
        }
    }

    export class ModalDialogContentPanel extends api.dom.DivEl {

        constructor() {
            super("dialog-content");
        }
    }

    export class ModalDialogButtonRow extends api.dom.DivEl {

        private defaultButton: DialogButton;

        private buttonContainer: api.dom.DivEl;

        constructor() {
            super("dialog-buttons");

            this.buttonContainer = new api.dom.DivEl('button-container');
            this.appendChild(this.buttonContainer);
        }

        addAction(action: api.ui.Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            var button = new DialogButton(action);
            if (useDefault) {
                this.defaultButton = button;
            }
            if (prepend) {
                this.buttonContainer.prependChild(button);
            } else {
                this.buttonContainer.appendChild(button);
            }

            action.onPropertyChanged(() => {
                button.setLabel(action.getLabel());
                button.setEnabled(action.isEnabled());
            });
            return button;
        }

        focusDefaultAction() {
            if (this.defaultButton) {
                this.defaultButton.giveFocus();
            }
        }
    }

}
