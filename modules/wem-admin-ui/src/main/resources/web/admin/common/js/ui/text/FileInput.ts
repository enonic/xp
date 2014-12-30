module api.ui.text {

    import InputEl = api.dom.InputEl;

    export class FileInput extends api.dom.FormInputEl {

        private fileInput: InputEl;
        private textInput: InputEl;
        private fileButton: api.ui.button.Button;

        constructor(className?: string) {
            super("div", "file-input");

            if (className) {
                this.addClass(className);
            }

            this.textInput = new InputEl("text");

            this.fileButton = new api.ui.button.Button();
            this.fileInput = new InputEl("file", "file");
            this.fileButton.removeChildren();
            this.fileButton.appendChild(this.fileInput);

            this.appendChildren([this.textInput, this.fileButton]);
        }

        setPlaceholder(placeholder: string): FileInput {
            this.textInput.setPlaceholder(placeholder);
            return this;
        }

        getPlaceholder(): string {
            return this.textInput.getPlaceholder();
        }

        getValue(): string {
            return this.textInput.getValue();
        }

        setValue(value: string): FileInput {
            this.textInput.setValue(value);
            return this;
        }

        onChange(listener: (event: Event) => void) {
            this.textInput.onChange(listener);
        }

        unChange(listener: (event: Event) => void) {
            this.textInput.unChange(listener);
        }

        onInput(listener: (event: Event) => void) {
            this.textInput.onInput(listener);
        }

        unInput(listener: (event: Event) => void) {
            this.textInput.unInput(listener);
        }

        giveFocus(): boolean {
            return this.textInput.giveFocus();
        }

        giveBlur(): boolean {
            return this.textInput.giveBlur();
        }

    }
}