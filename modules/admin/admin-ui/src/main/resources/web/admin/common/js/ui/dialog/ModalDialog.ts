module api.ui.dialog {

    import DivEl = api.dom.DivEl;
    import Action = api.ui.Action;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import Element = api.dom.Element;

    export interface ModalDialogConfig {
        title?: string,
        buttonRow?: ModalDialogButtonRow,
    }

    export class ModalDialog extends DivEl {

        protected header: api.ui.dialog.ModalDialogHeader;

        private contentPanel: ModalDialogContentPanel;

        private buttonRow: ModalDialogButtonRow;

        private cancelAction: Action;

        private actions: Action[] = [];

        private cancelButton: DivEl;

        private static openDialogsCounter: number = 0;

        private tabbable: api.dom.Element[];

        private listOfClickIgnoredElements: api.dom.Element[] = [];

        private onClosedListeners: {(): void;}[] = [];

        public static debug: boolean = false;

        constructor(config: ModalDialogConfig = <ModalDialogConfig>{}) {
            super('modal-dialog', api.StyleHelper.COMMON_PREFIX);

            let wrapper = new DivEl('modal-dialog-content-wrapper');
            this.appendChild(wrapper);

            this.cancelAction = this.createDefaultCancelAction();
            this.cancelButton = new DivEl('cancel-button-top');
            this.cancelButton.onClicked(() => this.cancelAction.execute());
            wrapper.appendChild(this.cancelButton);

            this.header = this.createHeader(config.title || '');
            wrapper.appendChild(this.header);

            this.contentPanel = new ModalDialogContentPanel();
            wrapper.appendChild(this.contentPanel);

            let push = new DivEl('modal-dialog-content-push');
            wrapper.appendChild(push);

            let footer = new DivEl('modal-dialog-footer');
            this.appendChild(footer);

            this.buttonRow = config.buttonRow || new ModalDialogButtonRow();
            footer.appendChild(this.buttonRow);

            this.initListeners();
        }

        private initListeners() {
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, () => {
                if (this.isVisible()) {
                    this.centerMyself();
                }
            });

            this.handleClickOutsideDialog();
            this.handleFocusInOutEvents();
        }

        private handleClickOutsideDialog() {
            const mouseClickListener: (event: MouseEvent) => void = (event: MouseEvent) => {
                if (this.isVisible()) {
                    for (let element = event.target; element; element = (<any>element).parentNode) {
                        if (element === this.getHTMLElement() || this.isIgnoredElementClicked(<any>element)) {
                            return;
                        }
                    }
                    if (this.cancelAction) {
                        this.cancelAction.execute();
                    }
                }
            };

            this.onRemoved(() => {
                api.dom.Body.get().unMouseDown(mouseClickListener);
            });

            this.onAdded(() => {
                api.dom.Body.get().onMouseDown(mouseClickListener);
            });
        }

        private handleFocusInOutEvents() {
            let buttonRowIsFocused: boolean = false;
            let buttonRowFocusOutTimeout: number;
            const focusOutTimeout: number = 10;

            this.onMouseDown(() => {
                buttonRowIsFocused = false; // making dialog focusOut event give focus to last tabbable elem
            });

            api.util.AppHelper.focusInOut(this, () => {
                if (this.hasTabbable() && !this.hasSubDialog()) {
                    // last focusable - Cancel
                    // first focusable - X
                    if (buttonRowIsFocused) { // last element lost focus
                        this.tabbable[0].giveFocus();
                    } else {
                        this.tabbable[this.tabbable.length - 1].giveFocus();
                    }
                }
            }, focusOutTimeout, false);

            this.buttonRow.onFocusIn(() => {
                buttonRowIsFocused = true;
                clearTimeout(buttonRowFocusOutTimeout);
            });

            this.buttonRow.onFocusOut(() => {
                buttonRowFocusOutTimeout = setTimeout(() => {
                    buttonRowIsFocused = false;
                }, focusOutTimeout + 5); // timeout should be > timeout for modal dialog to trigger after
            });
        }

        protected createHeader(title: string): api.ui.dialog.ModalDialogHeader {
            return new api.ui.dialog.ModalDialogHeader(title);
        }

        addClickIgnoredElement(elem: api.dom.Element) {
            this.listOfClickIgnoredElements.push(elem);
        }

        private isIgnoredElementClicked(element: HTMLElement): boolean {
            let ignoredElementClicked = false;
            if (element && element.className && element.className.indexOf) {
                ignoredElementClicked = element.className.indexOf('mce-') > -1 || element.className.indexOf('html-area-modal-dialog') > -1;
            }
            ignoredElementClicked = ignoredElementClicked || this.listOfClickIgnoredElements.some((elem: api.dom.Element) => {
                    return elem.getHTMLElement() === element || elem.getEl().contains(element);
                });
            return ignoredElementClicked;
        }

        private createDefaultCancelAction() {
            let cancelAction = new Action('Cancel', 'esc', true);
            cancelAction.setIconClass('cancel-button-top');
            cancelAction.setLabel('');
            cancelAction.onExecuted(() => {
                this.close();
            });
            this.actions.push(cancelAction);
            return cancelAction;
        }

        getCancelAction(): Action {
            return this.cancelAction;
        }

        addCancelButtonToBottom(buttonLabel: string = 'Cancel'): DialogButton {
            let cancelAction = new Action(buttonLabel);
            cancelAction.setIconClass('cancel-button-bottom');
            cancelAction.onExecuted(() => this.cancelAction.execute());
            return this.buttonRow.addAction(cancelAction);
        }

        setTitle(value: string) {
            this.header.setTitle(value);
        }

        appendChildToContentPanel(child: api.dom.Element) {
            this.contentPanel.appendChild(child);
        }

        prependChildToContentPanel(child: api.dom.Element) {
            this.contentPanel.prependChild(child);
        }

        appendChildToHeader(child: api.dom.Element) {
            this.header.appendChild(child);
        }

        removeChildFromContentPanel(child: api.dom.Element) {
            this.contentPanel.removeChild(child);
        }

        addAction(action: Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            this.actions.push(action);
            return this.buttonRow.addAction(action, useDefault, prepend);
        }

        removeAction(actionButton: DialogButton) {
            if (!actionButton) {
                return;
            }

            const action = actionButton.getAction();

            const index = this.actions.indexOf(action);
            if (index >= 0) {
                this.actions.splice(index, 1);
            }

            this.buttonRow.removeAction(action);
        }

        show() {
            api.dom.Body.get().getHTMLElement().classList.add('modal-dialog');
            this.centerMyself();
            super.show();
            this.buttonRow.focusDefaultAction();
        }

        hide() {
            api.dom.Body.get().getHTMLElement().classList.remove('modal-dialog');
            super.hide(true);
        }

        protected centerMyself() {
            if (ModalDialog.debug) {
                console.debug('ModalDialog.centerMyself', api.ClassHelper.getClassName(this));
            }
            const el = this.getEl();
            el.setMarginTop(`-${ el.getHeightWithBorder() / 2 }px`);

            if (ResponsiveRanges._540_720.isFitOrBigger(this.getEl().getWidthWithBorder())) {
                this.centerHorisontally();
            } else {
                el.setMarginLeft('0px');
                el.removeClass('centered_horizontally');
            }
        }

        centerHorisontally() {
            const el = this.getEl();
            el.setMarginTop(`-${ el.getWidthWithBorder() / 2 }px`);
            el.addClass('centered_horizontally');
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

        open() {

            api.ui.mask.BodyMask.get().show();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            let keyBindings = Action.getKeyBindings(this.actions);

            this.updateTabbable();

            keyBindings = keyBindings.concat([
                new KeyBinding('right', (event) => {
                    this.focusNextTabbable();

                    event.stopPropagation();
                    event.preventDefault();
                }),
                new KeyBinding('left', (event) => {
                    this.focusPreviousTabbable();

                    event.stopPropagation();
                    event.preventDefault();
                })
            ]);

            api.ui.KeyBindings.get().bindKeys(keyBindings);

            ModalDialog.openDialogsCounter++;
        }

        close() {
            if (ModalDialog.openDialogsCounter === 1) {
                api.ui.mask.BodyMask.get().hide();
            }

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();

            ModalDialog.openDialogsCounter--;
            this.notifyClosed();
        }

        onClosed(onCloseCallback: () => void) {
            this.onClosedListeners.push(onCloseCallback);
        }

        unClosed(listener: {(): void;}) {
            this.onClosedListeners = this.onClosedListeners.filter(function (curr: {(): void;}) {
                return curr !== listener;
            });
        }

        private notifyClosed() {
            this.onClosedListeners.forEach((listener) => {
                listener();
            });
        }
    }

    export class ModalDialogHeader extends DivEl {

        private titleEl: api.dom.H2El;

        constructor(title: string) {
            super('dialog-header');

            this.titleEl = new api.dom.H2El('title');
            this.titleEl.setHtml(title);
            this.appendChild(this.titleEl);
        }

        setTitle(value: string) {
            this.titleEl.setHtml(value);
        }
    }

    export class ModalDialogContentPanel extends DivEl {

        constructor() {
            super('dialog-content');
        }
    }

    export class ModalDialogButtonRow extends DivEl {

        private defaultButton: DialogButton;

        private buttonContainer: DivEl;

        constructor() {
            super('dialog-buttons');

            this.buttonContainer = new DivEl('button-container');
            this.appendChild(this.buttonContainer);
        }

        addElement(element: Element) {
            this.buttonContainer.appendChild(element);
        }

        addAction(action: Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            let button = new DialogButton(action);
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

        removeAction(action: Action) {

            if (this.defaultButton && this.defaultButton.getAction() == action) {
                this.defaultButton = null;
            }

            const buttonToRemove = [];

            this.buttonContainer.getChildren().forEach((button: DialogButton) => {
                if (button.getAction() == action) {
                    buttonToRemove.push(button);
                }
            });

            buttonToRemove.forEach((button) => {
                this.buttonContainer.removeChild(button);
            });
        }

        focusDefaultAction() {
            if (this.defaultButton) {
                this.defaultButton.giveFocus();
            }
        }
    }

}
