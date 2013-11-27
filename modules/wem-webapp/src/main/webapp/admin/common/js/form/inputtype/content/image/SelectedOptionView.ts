module api_form_inputtype_content_image {

    export class SelectedOptionView extends api_ui_combobox.SelectedOptionView<api_content.ContentSummary> {

        private content:api_content.ContentSummary;

        private lastInRow:boolean;

        constructor(option:api_ui_combobox.Option<api_content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        addClickEventListener(listener:(event: Event)=>void) {
            this.getEl().addEventListener("click", (event: Event) => {
                listener(event);
            });
        }

        layout() {

            this.getEl().setBackgroundImage("url(" + this.content.getIconUrl() + "?size=140&thumbnail=false)");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(this.content.getName());
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