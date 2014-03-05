module api.form.inputtype.content.image {

    export class SelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary> {

        private content:api.content.ContentSummary;

        private lastInRow:boolean;

        constructor(option:api.ui.selector.combobox.Option<api.content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        addClickEventListener(listener:(event: Event)=>void) {
            this.getEl().addEventListener("click", (event: Event) => {
                listener(event);
            });
        }

        layout() {

            this.getEl().setBackgroundImage("url(" + this.content.getIconUrl() + ")");

            var label = new api.dom.DivEl("label");
            label.getEl().setInnerHtml(this.content.getName().toString());
            this.appendChild(label);
        }

        setLastInRow(value:boolean){
            this.lastInRow = value;
            this.refreshUI();
        }

        isLastInRow():boolean {
            return this.lastInRow;
        }

        private refreshUI() {
            if( this.lastInRow ) {
                this.addClass("last-in-row");
            }
            else {
                this.removeClass("last-in-row");
            }
        }
    }
}