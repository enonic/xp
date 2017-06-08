import '../../../../../api.ts';
import {WidgetItemView} from '../../WidgetItemView';
import {DefaultModels} from '../../../../wizard/page/DefaultModels';
import {DefaultModelsFactory, DefaultModelsFactoryConfig} from '../../../../wizard/page/DefaultModelsFactory';
import Content = api.content.Content;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import GetNearestSiteRequest = api.content.resource.GetNearestSiteRequest;
import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
import PageTemplate = api.content.page.PageTemplate;
import Site = api.content.site.Site;
import Tooltip = api.ui.Tooltip;
import EditContentEvent = api.content.event.EditContentEvent;
import PageDescriptor = api.content.page.PageDescriptor;
import PageMode = api.content.page.PageMode;
import ContentTypeName = api.schema.content.ContentTypeName;
import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;

export class PageTemplateWidgetItemView extends WidgetItemView {

    private content: ContentSummary;

    private pageTemplateObj: PageTemplateObj;

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

            return this.loadPageTemplate().then((pageTemplateObj) => this.layout());
        }

        return wemQ<any>(null);
    }

    private initListeners() {

        let onContentUpdated = (contents: ContentSummaryAndCompareStatus[]) => {
            let thisContentId = this.content.getId();

            let contentSummary: ContentSummaryAndCompareStatus = contents.filter((content) => {
                return thisContentId === content.getId();
            })[0];

            if (contentSummary) {
                this.setContentAndUpdateView(contentSummary);
            }
        };

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        serverEvents.onContentUpdated(onContentUpdated);
    }

    public layout(): wemQ.Promise<any> {
        if (PageTemplateWidgetItemView.debug) {
            console.debug('PageTemplateWidgetItemView.layout');
        }

        return super.layout().then(() => {
            this.removeChildren();
            if (this.pageTemplateObj) {
                this.appendChild(this.pageTemplateObj.render());
            }
        });
    }

    private getPageTemplateInfo(content: Content): wemQ.Promise<PageTemplateObj> {
        let pageTemplateObj = new PageTemplateObj();

        if (content.getType().isFragment()) {
            pageTemplateObj.setPageMode(api.content.page.PageMode.FRAGMENT);
            return wemQ(pageTemplateObj);
        }

        if (content.isPage()) {

            if (content.getPage().hasTemplate()) {
                pageTemplateObj.setPageMode(api.content.page.PageMode.FORCED_TEMPLATE);

                return new GetPageTemplateByKeyRequest(content.getPage().getTemplate()).sendAndParse().then((pageTemplate: PageTemplate) => {
                    pageTemplateObj.setPageTemplate(pageTemplate);

                    return wemQ(pageTemplateObj);
                });
            }

            pageTemplateObj.setPageMode(api.content.page.PageMode.FORCED_CONTROLLER);

            return new GetPageDescriptorByKeyRequest(content.getPage().getController()).sendAndParse().then((pageDescriptor: PageDescriptor) => {
                pageTemplateObj.setPageController(pageDescriptor);

                return wemQ(pageTemplateObj);
            });
        }

        return new GetNearestSiteRequest(this.content.getContentId()).sendAndParse().then((site: Site) => {

            return this.loadDefaultModels(site, content.getType()).then((defaultModels: DefaultModels) => {

                if (defaultModels && defaultModels.hasPageTemplate()) {
                    pageTemplateObj.setPageMode(PageMode.AUTOMATIC);
                    pageTemplateObj.setPageTemplate(defaultModels.getPageTemplate());
                }

                return wemQ<PageTemplateObj>(pageTemplateObj);
            });
        });
    }

    private loadDefaultModels(site: Site, contentType: ContentTypeName): wemQ.Promise<DefaultModels> {

        if (site) {
            return DefaultModelsFactory.create(<DefaultModelsFactoryConfig>{
                siteId: site.getContentId(),
                contentType: contentType,
                applications: site.getApplicationKeys()
            });
        }
        
        if (contentType.isSite()) {
            return wemQ<DefaultModels>(new DefaultModels(null, null));
        }
        
        return wemQ<DefaultModels>(null);
    }

    private loadPageTemplate(): wemQ.Promise<void> {
        this.pageTemplateObj = null;

        return new GetContentByIdRequest(this.content.getContentId()).sendAndParse().then((content: Content) => {
            return this.getPageTemplateInfo(content).then((pageTemplateObj: PageTemplateObj) => {
                this.pageTemplateObj = pageTemplateObj;
            });
        });
    }
}

class PageTemplateObj {
    private pageMode: PageMode;
    private pageTemplate: PageTemplate;
    private pageController: PageDescriptor;

    constructor() {
        this.setPageMode(PageMode.NO_CONTROLLER);
    }

    setPageMode(pageMode: PageMode) {
        this.pageMode = pageMode;
    }

    setPageTemplate(pageTemplate: PageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    setPageController(pageController: PageDescriptor) {
        this.pageController = pageController;
    }

    private getPageModeString(): string {
        switch (this.pageMode) {
            case PageMode.AUTOMATIC:
                return 'Automatic';
            case PageMode.FORCED_CONTROLLER:
                return 'Custom';
            case PageMode.FORCED_TEMPLATE:
                return 'Template';
            case PageMode.FRAGMENT:
                return 'Fragment';
            default:
                return 'Not used';
        }
    }

    private isRenderable() {
        return this.pageMode !== PageMode.FRAGMENT && this.pageMode !== PageMode.NO_CONTROLLER;
    }

    private getDescriptorString() {

        if (!(this.pageTemplate || this.pageController)) {
            return '';
        }
        
        return (this.pageTemplate || this.pageController).getDisplayName();
    }

    render(): api.dom.DivEl {
        let divEl = new api.dom.DivEl();

        const page = new api.dom.DdDtEl('dt').setHtml('Page:');
        const pageValue = new api.dom.DdDtEl('dd').setHtml(this.getPageModeString());

        divEl.appendChildren(page, pageValue);

        if (this.isRenderable()) {

            const descriptor = new api.dom.DdDtEl('dt').setHtml(this.pageTemplate ? 'Page Template:' : 'Controller:');
            const descriptorValue = new api.dom.DdDtEl('dd').setHtml(this.getDescriptorString());

            divEl.appendChildren(descriptor, descriptorValue);
        }

        return divEl;
    }
}

class Field {

    protected name: string;

    protected value: string;

    setName(name: string): Field {
        this.name = name;
        return this;
    }

    setValue(value: string): Field {
        this.value = value;
        return this;
    }

    layout(parentEl: api.dom.Element) {
        const term = new api.dom.DdDtEl('dt').setHtml(this.name);
        const definition = new api.dom.DdDtEl('dd').setHtml(this.value);
        parentEl.appendChildren(term, definition);
    }
}

class FieldLink extends Field {

    private tooltip: string;

    private content: ContentSummary;

    setTooltip(tooltip: string): FieldLink {
        this.tooltip = tooltip;
        return this;
    }

    setContent(content: ContentSummary): FieldLink {
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
