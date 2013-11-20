module api_form_inputtype_content_image {

    export class SelectedOptionView extends api_ui_combobox.ComboBoxSelectedOptionView<SelectedOption> {

        private selectedOption:SelectedOption;

        private lastInRow:boolean;

        constructor(option:api_ui_combobox.Option<SelectedOption>) {
            this.selectedOption = option.displayValue;
            super(option);
        }

        addClickEventListener(listener:(event: Event)=>void) {
            this.getEl().addEventListener("click", (event: Event) => {
                listener(event);
            });
        }

        layout() {

            var content = this.selectedOption.getContent();

            this.getEl().setBackgroundImage("url(" + content.getIconUrl() + "?size=140&thumbnail=false)");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(content.getName());
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