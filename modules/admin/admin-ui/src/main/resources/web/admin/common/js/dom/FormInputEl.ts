module api.dom {
    export class FormInputEl extends FormItemEl {

        private dirtyChangedListeners: {(dirty: boolean):void}[] = [];

        private originalValue: string;

        private dirty: boolean;

        constructor(tagName: string, className?: string) {
            super(tagName, className);
            this.addClass('form-input');

            this.onChange((event: Event) => this.updateDirtyState());
        }

        getValue(): string {
            return this.getEl().getValue();
        }

        setValue(value: string): FormInputEl {
            if (!this.originalValue) {
                this.originalValue = value;
            }
            this.getEl().setValue(value);
            return this;
        }

        isDirty(): boolean {
            return this.dirty;
        }

        protected setDirty(dirty: boolean) {
            if (this.dirty != dirty) {
                this.dirty = dirty;
                this.notifyDirtyChanged(dirty);
            }
        }

        protected updateDirtyState() {
            this.setDirty(this.originalValue != this.getValue());
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
    }
}