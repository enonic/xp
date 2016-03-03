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

        private static openDialogsCounter: number = 0;

        constructor(config: ModalDialogConfig) {
            super("modal-dialog", api.StyleHelper.COMMON_PREFIX);

            this.config = config;

            var wrapper = new api.dom.DivEl("modal-dialog-content-wrapper");
            this.appendChild(wrapper);

            this.cancelAction = this.createDefaultCancelAction();
            this.cancelButton = new DialogButton(this.cancelAction);
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
                        if (element == this.getHTMLElement() || this.isIgnoredElementClicked(<any>element)) {
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

        private isIgnoredElementClicked(element: HTMLElement): boolean {
            if (!!element && !!element.className && !!element.className.indexOf) {
                return element.className.indexOf("mce-") > -1 || element.className.indexOf("html-area-modal-dialog") > -1;
            }
            return false;
        }

        private createDefaultCancelAction() {
            var cancelAction = new api.ui.Action("Cancel", "esc");
            cancelAction.setIconClass("cancel-button-top");
            cancelAction.setLabel("");
            cancelAction.onExecuted(()=> {
                this.close();
            });
            this.actions.push(cancelAction);
            return cancelAction;
        }

        getCancelAction(): api.ui.Action {
            return this.cancelAction;
        }

        addCancelButtonToBottom() {
            var cancelAction = new api.ui.Action("Cancel");
            cancelAction.setIconClass("cancel-button-bottom");
            cancelAction.onExecuted(() => this.cancelAction.execute());
            this.buttonRow.addAction(cancelAction);
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

        protected getResponsiveItem(): api.ui.responsive.ResponsiveItem {
            return this.responsiveItem;
        }

        getButtonRow(): ModalDialogButtonRow {
            return this.buttonRow;
        }

        protected getContentPanel(): ModalDialogContentPanel {
            return this.contentPanel;
        }

        hide() {
            super.hide();
        }

        close() {

            if (ModalDialog.openDialogsCounter == 1) {
                api.ui.mask.BodyMask.get().hide();
            }

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();

            ModalDialog.openDialogsCounter--;
        }

        open() {

            api.ui.mask.BodyMask.get().show();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            api.ui.KeyBindings.get().bindKeys(api.ui.Action.getKeyBindings(this.actions));

            ModalDialog.openDialogsCounter++;
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
