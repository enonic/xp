import "../../../../../api.ts";
import {WidgetItemView} from "../../WidgetItemView";

import ContentSummary = api.content.ContentSummary;
import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
import Application = api.application.Application;
import ApplicationKey = api.application.ApplicationKey;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class PropertiesWidgetItemView extends WidgetItemView {

    private content: ContentSummary;

    private list: api.dom.DlEl;

    public static debug = false;

    constructor() {
        super("properties-widget-item-view");
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        var content = item.getContentSummary();
        if (!content.equals(this.content)) {
            if (!this.content) {
                this.initListeners();
            }
            this.content = content;
            return this.layout();
        }

        return wemQ<any>(null);
    }


    private initListeners() {

        let layoutOnPublishStateChange = (contents: ContentSummaryAndCompareStatus[]) => {
            let thisContentId = this.content.getId();

            let content: ContentSummaryAndCompareStatus = contents.filter((content) => {
                return thisContentId == content.getId();
            })[0];

            if (!!content) {
                this.setContentAndUpdateView(content);
            }
        };

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        serverEvents.onContentPublished(layoutOnPublishStateChange);

        //Uncomment the line below if we need to redo the layout on unpublish
        //serverEvents.onContentUnpublished(layoutOnPublishStateChange);
    }

    public layout(): wemQ.Promise<any> {
        if (PropertiesWidgetItemView.debug) {
            console.debug('PropertiesWidgetItemView.layout');
        }

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

        if (this.hasChild(this.list)) {
            this.removeChild(this.list);
        }
        this.list = new api.dom.DlEl();

        var strings: FieldString[];

        strings = [
            new FieldString().setName("Type").setValue(this.content.getType().getLocalName()
                ? this.content.getType().getLocalName() : this.content.getType().toString()),

            new FieldString().setName("Application").setValue(application ? application.getDisplayName() :
                                                              this.content.getType().getApplicationKey().getName()),

            this.content.getLanguage() ? new FieldString().setName("Language").setValue(this.content.getLanguage()) : null,

            this.content.getOwner() ? new FieldString().setName("Owner").setValue(this.content.getOwner().getId()) : null,

            new FieldString().setName("Created").setValue(DateTimeFormatter.createHtml(this.content.getCreatedTime())),

            this.content.getModifiedTime() ? new FieldString().setName("Modified").setValue(
                DateTimeFormatter.createHtml(this.content.getModifiedTime())) : null,

            new FieldString().setName("Publish From").setValue(this.content.getPublishFromTime() ?
                                                               DateTimeFormatter.createHtml(this.content.getPublishFromTime()) : " "),

            new FieldString().setName("Publish To").setValue(this.content.getPublishToTime() ?
                                                             DateTimeFormatter.createHtml(this.content.getPublishToTime()) : " "),

            new FieldString().setName("Id").setValue(this.content.getId())
        ];

        strings.forEach((stringItem: FieldString) => {
            if (stringItem) {
                stringItem.layout(this.list);
            }
        });
        this.removeChildren();
        this.appendChild(this.list);
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
