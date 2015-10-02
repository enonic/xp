module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;
    import ContentSummary = api.content.ContentSummary;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    export class PropertiesWidgetItemView extends WidgetItemView {

        private content: ContentSummary;

        constructor() {
            super("properties-widget-item-view");
        }

        public setContent(content: ContentSummary) {
            this.content = content;
        }

        public layout() {
            this.removeChildren();
            if (this.content != undefined) {

                var newDl = new api.dom.DlEl();

                var strings: FieldString[];

                strings = [
                    new FieldString().setName("Type").setValue(this.content.getType().toLocaleString()),
                    new FieldString().setName("Language").setValue(this.content.getLanguage()),
                    new FieldString().setName("Owner").setValue(this.content.getOwner().toString()),
                    new FieldString().setName("Modified").setValue(DateTimeFormatter.createHtmlNoTimestamp(this.content.getModifiedTime())),
                    new FieldString().setName("Created").setValue(DateTimeFormatter.createHtmlNoTimestamp(this.content.getCreatedTime())),
                    new FieldString().setName("Id").setValue(this.content.getId())
                ];

                strings.forEach((stringItem: FieldString) => {
                    stringItem.layout(newDl);
                });
                this.appendChild(newDl);
            }
            super.layout();
        }
    }


    class FieldString {

        private fieldName: string;

        private value: string;

        public setName(name: string): FieldString {
            this.fieldName = name;
            return this;
        }

        public setValue(value: string): FieldString {
            this.value = value;
            return this;
        }

        public layout(parentEl: api.dom.Element) {
            var valueEl = new api.dom.DdDtEl("dt").setHtml(this.value);
            var spanEl = new api.dom.DdDtEl("dd").setHtml(this.fieldName + ": ");
            parentEl.appendChildren(spanEl, valueEl);
        }

    }
}