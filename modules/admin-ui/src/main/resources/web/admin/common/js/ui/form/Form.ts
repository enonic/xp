module api.ui.form {

    export class Form extends api.dom.FormEl {

        private fieldsets: api.ui.form.Fieldset[] = [];

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        private validityChangedListeners: {(event: ValidityChangedEvent):void}[] = [];

        constructor(className?: string) {
            super(className);
            this.addClass("form-view");
            this.preventSubmit();

        }

        add(fieldset: Fieldset) {
            fieldset.onFocus((event) => {
                this.notifyFocused(event);
            });

            fieldset.onBlur((event) => {
                this.notifyBlurred(event);
            });

            fieldset.onValidityChanged((event) => {
                this.notifyValidityChanged(event.isValid());
            });

            this.fieldsets.push(fieldset);
            this.appendChild(fieldset);
            return this;
        }

        validate(markInvalid?: boolean): ValidationResult {
            var validationResult: ValidationResult = new ValidationResult();
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldset.validate(validationResult, markInvalid);
            });
            return validationResult;
        }

        setFormData(data: any) {
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldset.setFieldsetData(data);
            });
        }

        getFormData(): any {
            var data = {};
            var fieldsetData;
            this.fieldsets.forEach((fieldset: api.ui.form.Fieldset) => {
                fieldsetData = fieldset.getFieldsetData();
                for (var property in fieldsetData) {
                    if (fieldsetData.hasOwnProperty(property)) {
                        data[property] = fieldsetData[property];
                    }
                }
            });
            return data;
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

        notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners = this.validityChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        notifyValidityChanged(valid: boolean) {
            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void)=> {
                listener.call(this, new ValidityChangedEvent(valid));
            });
        }
    }
}