module api.ui.form {

    export class FormItem extends api.dom.DivEl {

        private label: api.dom.LabelEl;
        private input: api.dom.FormItemEl;
        private error: api.dom.SpanEl;
        private validator: (input: api.dom.FormItemEl) => string;
        private invalidClass: string = "invalid";

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor(builder: FormItemBuilder) {
            super("input-view");
            this.error = new api.dom.SpanEl("error");
            this.appendChild(this.error);

            this.input = builder.getInput();
            this.input.onFocus((event: FocusEvent) => {
                this.notifyFocused(event);
            });

            this.input.onBlur((event: FocusEvent) => {
                this.notifyBlurred(event);
            });

            if (builder.getLabel()) {
                this.label = new api.dom.LabelEl(builder.getLabel(), this.input);
                if(Validators.required == builder.getValidator()) {
                    this.label.addClass("required");
                }
                this.appendChild(this.label);
            }
            this.appendChild(this.input);

            if(builder.getValidator()) {
                this.validator = builder.getValidator();
            }
        }

        getLabel(): api.dom.LabelEl {
            return this.label;
        }

        getInput(): api.dom.FormItemEl {
            return this.input;
        }

        getValidator(): (input: api.dom.FormItemEl) => string {
            return this.validator;
        }

        validate(validationResult:ValidationResult, markInvalid?: boolean) {
            if (this.validator) {
                var validationMessage = this.validator(this.input);
                if(validationMessage) {
                    validationResult.addError(new ValidationError(this, validationMessage));
                }
                this.notifyValidityChanged(api.util.StringHelper.isBlank(validationMessage));
                if (markInvalid) {
                    if (validationMessage) {
                        this.addClass(this.invalidClass);
                    } else {
                        this.removeClass(this.invalidClass);
                    }
                    this.error.setHtml(validationMessage || "");
                }
            }
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.input.onValidityChanged(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.input.unValidityChanged(listener);
        }

        notifyValidityChanged(valid: boolean) {
            this.input.notifyValidityChanged(valid);
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }

    }

    export class FormItemBuilder {

        private label: string;
        private validator: (el: api.dom.FormInputEl) => string;
        private input: api.dom.FormItemEl;

        constructor(input: api.dom.FormItemEl) {
            if(!input) {
                throw new Error("Input can't be null.");
            }
            this.input = input;
        }

        build() {
            return new FormItem(this);
        }

        getInput(): api.dom.FormItemEl {
            return this.input;
        }

        setLabel(label: string):FormItemBuilder {
            this.label = label;
            return this;
        }

        getLabel(): string {
            return this.label;
        }

        setValidator(validator: (input: api.dom.FormInputEl) => string):FormItemBuilder {
            this.validator = validator;
            return this;
        }

        getValidator(): (input: api.dom.FormInputEl) => string {
            return this.validator;
        }

    }

}