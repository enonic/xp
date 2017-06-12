module api.ui.dialog {

    import DivEl = api.dom.DivEl;
    import Action = api.ui.Action;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import Element = api.dom.Element;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;

    export interface ConfirmationConfig {
        question?: string;
        yesCallback: () => void;
        noCallback?: () => void;
    }

    export interface ModalDialogConfig {
        title?: string;
        buttonRow?: ButtonRow;
        confirmation?: ConfirmationConfig;
        closeIconCallback?: () => void;
    }

    export class ModalDialog extends DivEl {

        protected header: api.ui.dialog.ModalDialogHeader;

        private contentPanel: ModalDialogContentPanel;

        private buttonRow: ButtonRow;

        private cancelAction: Action;

        protected closeIcon: DivEl;

        protected confirmationDialog: ConfirmationDialog;

        private static openDialogsCounter: number = 0;

        private tabbable: api.dom.Element[];

        private listOfClickIgnoredElements: api.dom.Element[] = [];

        private onClosedListeners: {(): void;}[] = [];

        private closeIconCallback: () => void;

        public static debug: boolean = false;

        constructor(config: ModalDialogConfig = <ModalDialogConfig>{}) {
            super('modal-dialog', api.StyleHelper.COMMON_PREFIX);

            let wrapper = new DivEl('modal-dialog-content-wrapper');
            this.appendChild(wrapper);

            this.buttonRow = config.buttonRow || new ButtonRow();

            this.cancelAction = this.createDefaultCancelAction();
            this.closeIconCallback = config.closeIconCallback || (() => {
                    if (this.cancelAction) {
                        this.cancelAction.execute();
                    }
                });
            this.closeIcon = new DivEl('cancel-button-top');
            this.closeIcon.onClicked(this.closeIconCallback);
            wrapper.appendChild(this.closeIcon);

            this.header = this.createHeader(config.title || '');
            wrapper.appendChild(this.header);

            this.contentPanel = new ModalDialogContentPanel();
            wrapper.appendChild(this.contentPanel);

            let push = new DivEl('modal-dialog-content-push');
            wrapper.appendChild(push);

            let footer = new DivEl('modal-dialog-footer');
            this.appendChild(footer);

            footer.appendChild(this.buttonRow);

            this.initConfirmationDialog(config.confirmation);
            this.initListeners();
        }

        private initConfirmationDialog(confirmation: ConfirmationConfig) {
            if (confirmation) {
                const {yesCallback, noCallback, question = 'You have made changes to the form, do you want to apply them?'} = confirmation;

                this.confirmationDialog = new ConfirmationDialog()
                    .setQuestion(question)
                    .setYesCallback(yesCallback || (() => {
                            this.close();
                        }));
                if (noCallback) {
                    this.confirmationDialog.setNoCallback(noCallback);
                }
            }
        }

        private initListeners() {
            ResponsiveManager.onAvailableSizeChanged(this, () => {
                if (this.isVisible()) {
                    this.centerMyself();
                }
            });

            // Set the ResponsiveRanges on first show() call
            const firstTimeResize = () => {
                ResponsiveManager.fireResizeEvent();
                this.unShown(firstTimeResize);
            };
            this.onShown(firstTimeResize);

            this.handleClickOutsideDialog();
            this.handleFocusInOutEvents();
        }

        private handleClickOutsideDialog() {
            const mouseClickListener: (event: MouseEvent) => void = (event: MouseEvent) => {
                const noConfirmationDialog = !this.confirmationDialog || !this.confirmationDialog.isVisible();
                if (this.isVisible() && noConfirmationDialog) {
                    for (let element = event.target; element; element = (<any>element).parentNode) {
                        if (element === this.getHTMLElement() || this.isIgnoredElementClicked(<any>element)) {
                            return;
                        }
                    }
                    this.closeIconCallback();
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
                this.confirmBeforeClose();
            });
            this.buttonRow.addToActions(cancelAction);
            return cancelAction;
        }

        getCancelAction(): Action {
            return this.cancelAction;
        }

        addCancelButtonToBottom(buttonLabel: string = 'Cancel'): DialogButton {
            let cancelAction = new Action(buttonLabel);
            cancelAction.setIconClass('cancel-button-bottom force-enabled');
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

        prependChildToHeader(child: api.dom.Element) {
            this.header.prependChild(child);
        }

        removeChildFromContentPanel(child: api.dom.Element) {
            this.contentPanel.removeChild(child);
        }

        addAction(action: Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            return this.buttonRow.addAction(action, useDefault, prepend);
        }

        removeAction(actionButton: DialogButton) {
            if (!actionButton) {
                return;
            }

            const action = actionButton.getAction();

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
            el.setMarginLeft(`-${ el.getWidthWithBorder() / 2 }px`);
            el.addClass('centered_horizontally');
        }

        getButtonRow(): ButtonRow {
            return this.buttonRow;
        }

        getContentPanel(): ModalDialogContentPanel {
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

            let keyBindings = Action.getKeyBindings(this.buttonRow.getActions());

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

        isDirty(): boolean {
            return false;
        }

        confirmBeforeClose() {
            if (this.confirmationDialog && this.isDirty()) {
                this.confirmationDialog.open();
            } else {
                this.close();
            }
        }

        close() {
            const isSingleDialogGroup = ModalDialog.openDialogsCounter === 1 ||
                                        (ModalDialog.openDialogsCounter === 2 && !!this.confirmationDialog);
            if (isSingleDialogGroup) {
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

        appendElement(el: Element) {
            el.insertAfterEl(this.titleEl);
        }
    }

    export class ModalDialogContentPanel extends DivEl {

        constructor() {
            super('dialog-content');
        }
    }

    export class ButtonRow extends DivEl {

        private defaultElement: api.dom.Element;

        private buttonContainer: DivEl;

        private actions: Action[] = [];

        constructor() {
            super('dialog-buttons');

            this.buttonContainer = new DivEl('button-container');
            this.appendChild(this.buttonContainer);
        }

        addElement(element: Element) {
            this.buttonContainer.appendChild(element);
        }

        getActions(): Action[] {
            return this.actions;
        }

        addToActions(action: Action) {
            this.actions.push(action);
        }

        addAction(action: Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            const button = new DialogButton(action);
            if (useDefault) {
                this.setDefaultElement(button);
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

            this.actions.push(action);

            return button;
        }

        removeAction(action: Action) {
            const index = this.actions.indexOf(action);
            if (index >= 0) {
                this.actions.splice(index, 1);
            }

            this.buttonContainer.getChildren()
                .filter((button: DialogButton) => button.getAction() == action)
                .forEach((button: DialogButton) => {
                    if (this.defaultElement == button) {
                        this.resetDefaultElement();
                    }
                    this.buttonContainer.removeChild(button);
                });
        }

        setDefaultElement(element: api.dom.Element) {
            this.defaultElement = element;
        }

        resetDefaultElement() {
            this.defaultElement = null;
        }

        focusDefaultAction() {
            if (this.defaultElement) {
                this.defaultElement.giveFocus();
            }
        }
    }

}
