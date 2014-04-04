module api.content.inputtype.image {

    export class SelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary> {

        private static IMAGE_SIZE: number = 270;

        private content:api.content.ContentSummary;

        private icon: api.dom.ImgEl;

        constructor(option:api.ui.selector.Option<api.content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {
            this.icon = new api.dom.ImgEl(this.content.getIconUrl()+"?thumbnail=false&size=" + SelectedOptionView.IMAGE_SIZE);
            this.appendChild(this.icon);

            var label = new api.dom.DivEl("label");
            label.getEl().setInnerHtml(this.content.getName().toString());
            this.appendChild(label);
        }

        getIcon(): api.dom.ImgEl {
            return this.icon;
        }
    }
}