module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;
    import ContentSummary = api.content.ContentSummary;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;

    export class PropertiesWidgetItemView extends WidgetItemView {

        private content: ContentSummary;

        public static debug = false;

        constructor() {
            super("properties-widget-item-view");
        }

        public setContent(content: ContentSummary) {
            if (AttachmentsWidgetItemView.debug) {
                console.debug('PropertiesWidgetItemView.setContent: ', content);
            }
            if (!api.ObjectHelper.equals(content, this.content)) {
                this.content = content;
                return this.layout();
            }
            return wemQ<any>(null);
        }

        public layout(): wemQ.Promise<any> {
            if (PropertiesWidgetItemView.debug) {
                console.debug('PropertiesWidgetItemView.layout');
            }
            this.removeChildren();

            return super.layout().then(() => {
                if (this.content != undefined) {
                    var applicationKey = this.content.getType().getApplicationKey();
                    if (!applicationKey.isSystemReserved()) {
                        return new api.application.GetApplicationRequest(applicationKey).sendAndParse().then((application: Application) => {
                            this.layoutApplication(application);
                        }).catch(() => {
                            this.layoutApplication();
                        });
                    } else {
                        this.layoutApplication();
                    }
                }
            });
        }

        private layoutApplication(application?: Application) {

            var newDl = new api.dom.DlEl();

            var strings: FieldString[];


            strings = [
                new FieldString().setName("Type").setValue(this.content.getType().getLocalName()
                    ? this.content.getType().getLocalName() : this.content.getType().toString()),

                new FieldString().setName("Application").setValue(application ? application.getDisplayName() :
                                                                  this.content.getType().getApplicationKey().getName()),

                this.content.getLanguage() ? new FieldString().setName("Language").setValue(this.content.getLanguage()) : null,

                this.content.getOwner() ? new FieldString().setName("Owner").setValue(this.content.getOwner().getId()) : null,

                this.content.getModifiedTime() ? new FieldString().setName("Modified").
                    setValue(DateTimeFormatter.createHtmlNoTimestamp(this.content.getModifiedTime())) : null,

                new FieldString().setName("Created").setValue(DateTimeFormatter.createHtmlNoTimestamp(this.content.getCreatedTime())),

                new FieldString().setName("Id").setValue(this.content.getId())
            ];

            strings.forEach((stringItem: FieldString) => {
                if (stringItem) {
                    stringItem.layout(newDl);
                }
            });
            this.appendChild(newDl);
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