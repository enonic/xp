module api.dom {

    export class FormInputEl extends FormItemEl {

        private dirtyChangedListeners: {(dirty: boolean):void}[] = [];

        private valueChangedListeners: {(event: api.ValueChangedEvent):void}[] = [];

        private originalValue: string;

        private oldValue: string;

        private dirty: boolean = false;

        public static debug: boolean = false;

        constructor(tagName: string, className?: string, prefix?: string, originalValue?: string) {
            super(tagName, className, prefix);
            this.addClass('form-input');

            this.originalValue = !api.util.StringHelper.isBlank(originalValue) ? originalValue : "";
            this.oldValue = String(this.originalValue);

            if (this.originalValue != "") {
                // Descendant class might override my methods
                // therefore set value on added to make sure it's ready
                this.onAdded((event) => {
                    if (FormInputEl.debug) {
                        console.debug(this.toString() + '.onAdded: setting original value = ' + this.originalValue);
                    }
                    // use doSetValue because descendants might override setValue method (i.e. CheckBox, RadioGroup)
                    this.doSetValue(originalValue, true);
                    // doGetValue instead of value in case input trims/modifies value (i.e. textarea strips \r chars)
                    this.originalValue = this.doGetValue();
                });
            }

            this.onChange((event: Event) => {
                this.refreshDirtyState();
                this.refreshValueChanged();
            });
        }

        getValue(): string {
            return this.doGetValue();
        }

        /**
         * Gets value of the input (i.e gets checked for checkbox, instead of the value attribute)
         * @returns {string}
         */
        protected doGetValue(): string {
            return this.getEl().getValue();
        }

        /**
         * Takes care of the set value routine.
         * Note that it behaves differently for different elements:
         * "button", "reset", and "submit" - defines the text on the button
         * "text", "password", and "hidden" - defines the initial (default) value
         * "checkbox", "radio", "image" - defines the value sent on submit
         * @param value
         * @param silent
         * @param userInput indicates that dirty flag should be updated,
         * otherwise original value will be updated if not dirty
         * @returns {api.dom.FormInputEl}
         */
        setValue(value: string, silent?: boolean, userInput?: boolean): FormInputEl {
            if (FormInputEl.debug) {
                console.groupCollapsed(this.toString() + '.setValue(' + value + ')');
            }
            // let userInput force value update
            if (this.oldValue != value || userInput) {
                if (FormInputEl.debug) {
                    console.debug('update value from "' + this.oldValue + '" to "' + value + '"');
                }
                this.doSetValue(value, silent);
                this.refreshValueChanged(silent);

                if (!userInput && !this.dirty && this.originalValue != value) {
                    // update original value if not dirty and not user input
                    // to keep the dirty state consistent
                    if (FormInputEl.debug) {
                        console.debug('not dirty and not user input, update original value from "' + this.originalValue + '" to "' + value +
                                      '"');
                    }
                    // doGetValue instead of value in case input trims/modifies value (i.e. textarea strips \r chars)
                    this.originalValue = this.doGetValue();
                } else {
                    // update dirty according to new value and original value
                    // to keep dirty state consistent
                    this.refreshDirtyState(silent);
                }
            }
            if (FormInputEl.debug) {
                console.groupEnd();
            }
            return this;
        }

        /**
         * Does actual value setting (i.e sets input value, or checked for checkbox, instead of value attribute)
         * all necessary events are thrown in wrapping setValue
         * @param value
         * @param silent
         */
        protected doSetValue(value: string, silent?: boolean) {
            this.getEl().setValue(value);
        }

        isDirty(): boolean {
            return this.originalValue != this.doGetValue();
        }

        toString(): string {
            return api.ClassHelper.getClassName(this) + '[' + this.getId() + ']';
        }

        private setDirty(dirty: boolean, silent?: boolean) {
            if (this.dirty != dirty) {
                this.dirty = dirty;
                if (FormInputEl.debug) {
                    console.debug(this.toString() + ' dirty changed to ' + dirty);
                }
                if (!silent) {
                    this.notifyDirtyChanged(dirty);
                }
            }
        }

        /**
         * Call to refresh dirty state and fire an event if input was changed outside setValue
         * @param silent
         */
        protected refreshDirtyState(silent?: boolean) {
            this.setDirty(this.isDirty(), silent);
        }

        /**
         * Call to refresh old value and fire an event if input was changed outside setValue
         * @param silent
         */
        protected refreshValueChanged(silent?: boolean) {
            var value = this.doGetValue();

            if (this.oldValue != value) {
                if (FormInputEl.debug) {
                    console.debug(this.toString() + ' value changed to "' + value + '"');
                }
                if (!silent) {
                    this.notifyValueChanged(new api.ValueChangedEvent(this.oldValue, value));
                }
                this.oldValue = "" + value;
            }
        }

        onChange(listener: (event: Event) => void) {
            this.getEl().addEventListener("change", listener);
        }

        unChange(listener: (event: Event) => void) {
            this.getEl().removeEventListener("change", listener);
        }

        onInput(listener: (event: Event) => void) {
            this.getEl().addEventListener("input", listener);
        }

        unInput(listener: (event: Event) => void) {
            this.getEl().removeEventListener("input", listener);
        }

        onDirtyChanged(listener: (dirty: boolean) => void) {
            this.dirtyChangedListeners.push(listener);
        }

        unDirtyChanged(listener: (dirty: boolean) => void) {
            this.dirtyChangedListeners = this.dirtyChangedListeners.filter((curr) => {
                return listener !== curr;
            })
        }

        private notifyDirtyChanged(dirty: boolean) {
            this.dirtyChangedListeners.forEach((listener) => {
                listener(dirty);
            })
        }

        onValueChanged(listener: (event: api.ValueChangedEvent) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ValueChangedEvent) => void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return listener !== curr;
            })
        }

        private notifyValueChanged(event: api.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}