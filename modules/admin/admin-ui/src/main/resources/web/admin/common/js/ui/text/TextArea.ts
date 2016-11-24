module api.ui.text {

    export class TextArea extends api.dom.FormInputEl {

        private attendant: api.dom.Element;

        private clone: api.dom.Element;

        constructor(name: string, originalValue?: string) {
            super("textarea", "text-area", undefined, originalValue);
            this.getEl().setAttribute("name", name);

            this.onInput((event: Event) => {
                this.refreshDirtyState();
                this.refreshValueChanged();
            });

            this.clone = new api.dom.DivEl('autosize-clone').addClass(this.getEl().getAttribute('class'));
            this.attendant = new api.dom.DivEl('autosize-attendant');
            this.attendant.appendChild(this.clone);

            this.onAdded((event: api.dom.ElementAddedEvent) => {
                this.attendant.insertAfterEl(this);
            });

            this.onShown((event: api.dom.ElementShownEvent) => this.updateSize());
            this.onFocus((event: FocusEvent) => this.updateSize());
            this.onValueChanged((event: api.ValueChangedEvent) => this.updateSize());
            api.dom.WindowDOM.get().onResized((event: UIEvent) => this.updateSize(), this);
        }

        setRows(rows: number) {
            this.getEl().setAttribute("rows", rows.toString());
        }

        setColumns(columns: number) {
            this.getEl().setAttribute("cols", columns.toString());
        }

        private updateSize() {
            if (this.isRendered()) {
                this.clone.getEl().setInnerHtml(this.getValue() + " ");
                this.getEl().setHeightPx(this.clone.getEl().getHeightWithBorder());
            }
        }
    }

}