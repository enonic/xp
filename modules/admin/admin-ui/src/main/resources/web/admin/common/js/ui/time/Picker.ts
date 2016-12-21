module api.ui.time {

    export class Picker extends api.dom.DivEl {

        protected popup: any;

        protected input: api.ui.text.TextInput;

        protected validUserInput: boolean;

        constructor(builder: any, className?: string) {
            super(className);
            this.validUserInput = true;

            this.handleShownEvent();

            this.initData(builder);

            this.initPopup(builder);

            this.initInput(builder);

            this.wrapChildrenAndAppend();

            this.setupListeners(builder);

            this.setupCommonListeners();
        }

        private setupCommonListeners() {
            this.popup.onShown(e => this.addClass("expanded"));
            this.popup.onHidden(e => this.removeClass("expanded"));

            api.util.AppHelper.focusInOut(this, () => {
                this.popup.hide();
            }, 50, false);

            // Prevent focus loss on mouse down
            this.popup.onMouseDown((event: MouseEvent) => {
                event.preventDefault();
            });

            this.input.onClicked((e: MouseEvent) => {
                e.preventDefault();
                this.togglePopupVisibility();
            });

            this.input.onFocus((e: FocusEvent) =>
                setTimeout(() => {
                    if (!this.popup.isVisible()) {
                        e.preventDefault();
                        this.popup.show();
                    }
                }, 150)
            );

            this.popup.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isTabKey(event)) {
                    if (!(document.activeElement == this.input.getEl().getHTMLElement())) {
                        this.popup.hide();
                    }
                }
            });

            this.input.onKeyDown((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isEnterKey(event)) {
                    this.popup.hide();
                    api.dom.FormEl.moveFocusToNextFocusable(this.input);
                    event.stopPropagation();
                    event.preventDefault();
                } else if (api.ui.KeyHelper.isEscKey(event) || api.ui.KeyHelper.isArrowUpKey(event)) {
                    this.popup.hide();
                } else if (api.ui.KeyHelper.isArrowDownKey(event)) {
                    this.popup.show();
                    event.stopPropagation();
                    event.preventDefault();
                }
            });
        }

        public resetBase() {
            this.input.resetBaseValues();
        }

        protected handleShownEvent() {
            // must be implemented by children
        }

        protected initData(builder: any) {
            // must be implemented by children
        }

        protected initPopup(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected initInput(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected wrapChildrenAndAppend() {
            var wrapper = new api.dom.DivEl('wrapper', api.StyleHelper.COMMON_PREFIX);
            wrapper.appendChildren<api.dom.Element>(this.input, this.popup);

            this.appendChild(wrapper);
        }

        protected setupListeners(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected togglePopupVisibility() {
            this.popup.setVisible(!this.popup.isVisible());
        }

        getTextInput(): api.ui.text.TextInput {
            return this.input;
        }

        isDirty(): boolean {
            return this.input.isDirty();
        }

        isValid(): boolean {
            return this.validUserInput;
        }

        updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }
    }
}