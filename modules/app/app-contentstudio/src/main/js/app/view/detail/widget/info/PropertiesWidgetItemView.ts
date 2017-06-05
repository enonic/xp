import '../../../../../api.ts';
import {WidgetItemView} from '../../WidgetItemView';
import {PageTemplateOption} from '../../../../wizard/page/contextwindow/inspect/page/PageTemplateOption';

import ContentSummary = api.content.ContentSummary;
import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
import Application = api.application.Application;
import ApplicationKey = api.application.ApplicationKey;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import GetNearestSiteRequest = api.content.resource.GetNearestSiteRequest;
import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
import PageTemplate = api.content.page.PageTemplate;
import Site = api.content.site.Site;
import Tooltip = api.ui.Tooltip;
import EditContentEvent = api.content.event.EditContentEvent;

export class PropertiesWidgetItemView extends WidgetItemView {

    private content: ContentSummary;

    private pageTemplate: PageTemplate;

    private list: api.dom.DlEl;

    public static debug: boolean = false;

    constructor() {
        super('properties-widget-item-view');
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        let content = item.getContentSummary();
        if (!content.equals(this.content)) {
            if (!this.content) {
                this.initListeners();
            }
            this.content = content;

            return this.loadPageTemplate().then(() => this.layout());
        }

        return wemQ<any>(null);
    }

    private initListeners() {

        let layoutOnPublishStateChange = (contents: ContentSummaryAndCompareStatus[]) => {
            let thisContentId = this.content.getId();

            let contentSummary: ContentSummaryAndCompareStatus = contents.filter((content) => {
                return thisContentId === content.getId();
            })[0];

            if (contentSummary) {
                this.setContentAndUpdateView(contentSummary);
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
            if (this.content != null) {
                let applicationKey = this.content.getType().getApplicationKey();
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

        const strings: Field[] = [
            new Field().setName('Type')
                .setValue(
                    this.content.getType().getLocalName() ? this.content.getType().getLocalName() : this.content.getType().toString()),

            new Field().setName('Application')
                .setValue(application ? application.getDisplayName() : this.content.getType().getApplicationKey().getName()),

            this.content.getLanguage() ? new Field().setName('Language').setValue(this.content.getLanguage()) : null,

            this.content.getOwner() ? new Field().setName('Owner').setValue(this.content.getOwner().getId()) : null,

            new Field().setName('Created').setValue(DateTimeFormatter.createHtml(this.content.getCreatedTime())),

            this.content.getModifiedTime() ? new Field().setName('Modified')
                                               .setValue(DateTimeFormatter.createHtml(this.content.getModifiedTime())) : null,

            this.content.getPublishFirstTime() ? new Field().setName('First Published')
                                                   .setValue(DateTimeFormatter.createHtml(this.content.getPublishFirstTime())) : null,

            this.content.getPublishFromTime() ? new Field().setName('Publish From')
                                                  .setValue(DateTimeFormatter.createHtml(this.content.getPublishFromTime())) : null,

            this.content.getPublishToTime() ? new Field().setName('Publish To')
                                                .setValue(DateTimeFormatter.createHtml(this.content.getPublishToTime())) : null,

            new Field().setName('Id').setValue(this.content.getId()),

            this.pageTemplate ? new Field().setName('Page Template')
                                  .setValue(this.getPageTemplateName())
                                  .setTooltip(this.isPageTemplateAutogenerated() ? 'Automatic' : null)
                                  .setContent(this.isPageTemplateEditable() ? this.pageTemplate : null) : null
        ];

        strings.forEach((stringItem: Field) => {
            if (stringItem) {
                stringItem.layout(this.list);
            }
        });
        this.removeChildren();
        this.appendChild(this.list);
    }

    private loadPageTemplate(): wemQ.Promise<void> {
        this.pageTemplate = null;
        if (this.content.isPage() || this.content.isSite()) {

            const contentId = this.content.getContentId();
            const contentType = this.content.getType();

            return new GetNearestSiteRequest(contentId).sendAndParse().then((site: Site) => {
                return new GetPageTemplatesByCanRenderRequest(site.getContentId(), contentType).sendAndParse();
            }).then((templates: PageTemplate[]) => {
                if (templates.length > 0) {
                    this.pageTemplate = templates[0];

                }
            });
        }

        this.pageTemplate = null;

        return wemQ<any>(null);
    }

    private getPageTemplateName(): string {
        if (this.pageTemplate) {
            const templateMeta = new PageTemplateOption(this.pageTemplate);
            if (templateMeta.isCustom()) {
                return 'custom';
            } else { // templateMeta.isAuto() || other
                return this.pageTemplate.getDisplayName();
            }
        }
        return 'not used';
    }

    private isPageTemplateEditable(): boolean {
        if (this.pageTemplate) {
            const templateMeta = new PageTemplateOption(this.pageTemplate);
            return templateMeta.isAuto() || !templateMeta.isCustom();
        }
        return false;
    }

    private isPageTemplateAutogenerated(): boolean {
        return !!this.pageTemplate && new PageTemplateOption(this.pageTemplate).isAuto();
    }
}

class Field {

    private name: string;

    private value: string;

    private tooltip: string;

    private content: ContentSummary;

    setName(name: string): Field {
        this.name = name;
        return this;
    }

    setValue(value: string): Field {
        this.value = value;
        return this;
    }

    setTooltip(tooltip: string): Field {
        this.tooltip = tooltip;
        return this;
    }

    setContent(content: ContentSummary): Field {
        this.content = content;
        return this;
    }

    layout(parentEl: api.dom.Element) {
        const term = new api.dom.DdDtEl('dt').setHtml(this.name);
        const definition = new api.dom.DdDtEl('dd');

        if (this.content) {
            const link = new api.dom.AEl();
            link.setHtml(this.value);
            if (this.tooltip) {
                link.setTitle(this.tooltip);
            }
            link.onClicked(() => {
                new EditContentEvent([ContentSummaryAndCompareStatus.fromContentSummary(this.content)]).fire();
            });
            definition.appendChild(link);
        } else {
            definition.setHtml(this.value);
        }
        parentEl.appendChildren(term, definition);
    }
}
