module api.ui.dialog {

    import DivEl = api.dom.DivEl;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class ModalDialog extends api.dom.DivEl {

        protected header: api.ui.dialog.ModalDialogHeader;

        private contentPanel: ModalDialogContentPanel;

        private buttonRow: ModalDialogButtonRow;

        private cancelAction: api.ui.Action;

        private actions: api.ui.Action[] = [];

        private mouseClickListener: (event: MouseEvent) => void;

        private cancelButton: api.dom.DivEl;

        private static openDialogsCounter: number = 0;

        private buttonRowIsFocused: boolean = false;

        private tabbable: api.dom.Element[];

        private forceHorizontalCentering: boolean;

        private listOfClickIgnoredElements: api.dom.Element[] = [];

        public static debug: boolean = false;

        constructor(title: string = "", forceHorizontalCentering: boolean = false) {
            super("modal-dialog", api.StyleHelper.COMMON_PREFIX);

            this.forceHorizontalCentering = forceHorizontalCentering;

            var wrapper = new api.dom.DivEl("modal-dialog-content-wrapper");
            this.appendChild(wrapper);

            this.cancelAction = this.createDefaultCancelAction();
            this.cancelButton = new api.dom.DivEl("cancel-button-top");
            this.cancelButton.onClicked(() => this.cancelAction.execute());
            wrapper.appendChild(this.cancelButton);

            this.header = this.createHeader(title);
            wrapper.appendChild(this.header);

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

            this.onRemoved(() => {
                api.dom.Body.get().unMouseDown(this.mouseClickListener);
            });
            this.onAdded(() => {
                api.dom.Body.get().onMouseDown(this.mouseClickListener);
            });

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item) => {
                if (this.isVisible()) {
                    this.centerMyself();
                }
            });

            let buttonRowFocusOutTimeout;

            api.util.AppHelper.focusInOut(this, () => {
                if (this.hasTabbable() && !this.hasSubDialog()) {
                    // last focusable - Cancel
                    // first focusable - X
                    if (this.buttonRowIsFocused) { // last element lost focus
                        this.tabbable[0].giveFocus();
                    } else {
                        this.tabbable[this.tabbable.length - 1].giveFocus();
                    }
                }
            }, 50, false);

            this.buttonRow.onFocusIn((event) => {
                this.buttonRowIsFocused = true;
                clearTimeout(buttonRowFocusOutTimeout);
            });

            this.buttonRow.onFocusOut((event) => {
                buttonRowFocusOutTimeout = setTimeout(() => {
                    this.buttonRowIsFocused = false;
                }, 15); // timeout should be > timeout for modal dialog to trigger after
            });
        }

        addClickIgnoredElement(elem: api.dom.Element) {
            this.listOfClickIgnoredElements.push(elem);
        }

        protected createHeader(title: string): api.ui.dialog.ModalDialogHeader {
            return new api.ui.dialog.ModalDialogHeader(title);
        }

        private isIgnoredElementClicked(element: HTMLElement): boolean {
            var ignoredElementClicked = false;
            if (!!element && !!element.className && !!element.className.indexOf) {
                ignoredElementClicked = element.className.indexOf("mce-") > -1 || element.className.indexOf("html-area-modal-dialog") > -1;
            }
            ignoredElementClicked = ignoredElementClicked || this.listOfClickIgnoredElements.some((elem: api.dom.Element) => {
                    return elem.getHTMLElement() == element || elem.getEl().contains(element);
                });
            return ignoredElementClicked;
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

        addCancelButtonToBottom(buttonLabel: string = "Cancel") {
            var cancelAction = new api.ui.Action(buttonLabel);
            cancelAction.setIconClass("cancel-button-bottom");
            cancelAction.onExecuted(() => this.cancelAction.execute());
            this.buttonRow.addAction(cancelAction);
        }

        setTitle(value: string) {
            this.header.setTitle(value);
        }

        appendChildToContentPanel(child: api.dom.Element) {
            this.contentPanel.appendChild(child);
        }

        appendChildToHeader(child: api.dom.Element) {
            this.header.appendChild(child);
        }

        removeChildFromContentPanel(child: api.dom.Element) {
            this.contentPanel.removeChild(child);
        }

        addAction(action: api.ui.Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            this.actions.push(action);
            return this.buttonRow.addAction(action, useDefault, prepend);
        }

        show() {
            api.dom.Body.get().getHTMLElement().classList.add("modal-dialog");
            this.centerMyself();
            super.show();
            this.buttonRow.focusDefaultAction();
        }

        hide() {
            super.hide();
            api.dom.Body.get().getHTMLElement().classList.remove("modal-dialog");
        }

        protected centerMyself() {
            if (ModalDialog.debug) {
                console.debug("ModalDialog.centerMyself", api.ClassHelper.getClassName(this));
            }
            var el = this.getEl();
            el.setMarginTop("-" + (el.getHeightWithBorder() / 2) + "px");

            if (this.forceHorizontalCentering || ResponsiveRanges._540_720.isFitOrBigger(this.getEl().getWidthWithBorder())) {
                this.centerHorisontally();
            } else {
                el.setMarginLeft("0px");
                el.removeClass("centered_horizontally");
            }
        }

        centerHorisontally() {
            var el = this.getEl();
            el.setMarginLeft("-" + (el.getWidthWithBorder() / 2) + "px");
            el.addClass("centered_horizontally");
        }

        getButtonRow(): ModalDialogButtonRow {
            return this.buttonRow;
        }

        protected getContentPanel(): ModalDialogContentPanel {
            return this.contentPanel;
        }

        protected hasSubDialog(): boolean {
            // html area can spawn sub dialogs so check none is open
            return !!api.util.htmlarea.dialog.HTMLAreaDialogHandler.getOpenDialog();
        }

        private hasTabbable(): boolean {
            return !!this.tabbable && this.tabbable.length > 0;
        }

        updateTabbable() {
            this.tabbable = this.getTabbableElements();
        }

        private getTabbedIndex(): number {
            let activeElement = document.activeElement;
            let tabbedIndex = 0;
            if (this.hasTabbable()) {
                for (let i = 0; i < this.tabbable.length; i++) {
                    if (activeElement === this.tabbable[i].getHTMLElement()) {
                        tabbedIndex = i;
                        break;
                    }
                }
            }
            return tabbedIndex;
        }

        private focusNextTabbable() {
            if (this.hasTabbable()) {
                let tabbedIndex = this.getTabbedIndex();
                tabbedIndex = tabbedIndex + 1 >= this.tabbable.length ? 0 : tabbedIndex + 1;
                this.tabbable[tabbedIndex].giveFocus();
            }
        }

        private focusPreviousTabbable() {
            if (this.hasTabbable()) {
                let tabbedIndex = this.getTabbedIndex();
                tabbedIndex = tabbedIndex - 1 < 0 ? this.tabbable.length - 1 : tabbedIndex - 1;
                this.tabbable[tabbedIndex].giveFocus();
            }
        }

        protected overwriteDefaultArrows(element: api.dom.Element) {
            element.onKeyDown((event) => {


                if (api.ui.KeyHelper.isArrowLeftKey(event)) {
                    this.focusPreviousTabbable();
                    event.stopPropagation();
                    event.preventDefault();
                } else if (api.ui.KeyHelper.isArrowRightKey(event)) {
                    this.focusNextTabbable();
                    event.stopPropagation();
                    event.preventDefault();
                }
            });
        }

        open() {

            api.ui.mask.BodyMask.get().show();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            let keyBindings = api.ui.Action.getKeyBindings(this.actions);

            this.updateTabbable();

            keyBindings = keyBindings.concat([
                new KeyBinding("right", (event) => {
                    this.focusNextTabbable();

                    event.stopPropagation();
                    event.preventDefault();
                }),
                new KeyBinding("left", (event) => {
                    this.focusPreviousTabbable();

                    event.stopPropagation();
                    event.preventDefault();
                })
            ]);

            api.ui.KeyBindings.get().bindKeys(keyBindings);

            ModalDialog.openDialogsCounter++;
        }

        close() {
            if (ModalDialog.openDialogsCounter == 1) {
                api.ui.mask.BodyMask.get().hide();
            }

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();

            ModalDialog.openDialogsCounter--;
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

        getLastButton(): api.dom.Element {
            return this.buttonContainer.getLastChild();
        }
    }

}
