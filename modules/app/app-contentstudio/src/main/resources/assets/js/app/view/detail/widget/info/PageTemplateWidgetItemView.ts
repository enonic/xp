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
import EditContentEvent = api.content.event.EditContentEvent;
import PageDescriptor = api.content.page.PageDescriptor;
import PageMode = api.content.page.PageMode;
import ContentTypeName = api.schema.content.ContentTypeName;
import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
import i18n = api.util.i18n;
import GetDefaultPageTemplateRequest = api.content.page.GetDefaultPageTemplateRequest;

export class PageTemplateWidgetItemView extends WidgetItemView {

    private content: ContentSummary;

    private pageTemplateViewer: PageTemplateViewer;

    public static debug: boolean = false;

    constructor() {
        super('page-template-widget-item-view');
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        let content = item.getContentSummary();
        if (!content.equals(this.content)) {
            if (!this.content) {
                this.initListeners();
            }
            this.content = content;

            return this.loadPageTemplate().then((pageTemplateViewer) => this.layout());
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
            } else if (contents.some(content => content.getContentSummary().isPageTemplate())) {
                this.loadPageTemplate().then((pageTemplateViewer) => this.layout());
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
            if (this.pageTemplateViewer) {
                this.appendChild(this.pageTemplateViewer.render());
            }
        });
    }

    private getPageTemplateInfo(content: Content): wemQ.Promise<PageTemplateViewer> {
        let pageTemplateViewer = new PageTemplateViewer();

        if (content.getType().isFragment()) {
            pageTemplateViewer.setPageMode(api.content.page.PageMode.FRAGMENT);
            return wemQ(pageTemplateViewer);
        }

        if (content.isPage()) {

            if (content.getPage().hasTemplate()) {
                pageTemplateViewer.setPageMode(api.content.page.PageMode.FORCED_TEMPLATE);

                return new GetPageTemplateByKeyRequest(content.getPage().getTemplate()).sendAndParse()
                    .then(
                        (pageTemplate: PageTemplate) => {
                            pageTemplateViewer.setPageTemplate(pageTemplate);

                            return wemQ(pageTemplateViewer);
                        }, reason => {
                            return this.tryToSetAutomaticMode(pageTemplateViewer);
                        });
            }

            pageTemplateViewer.setPageMode(api.content.page.PageMode.FORCED_CONTROLLER);

            return new GetPageDescriptorByKeyRequest(content.getPage().getController()).sendAndParse()
                .then((pageDescriptor: PageDescriptor) => {
                    pageTemplateViewer.setPageController(pageDescriptor);

                    return wemQ(pageTemplateViewer);
                });
        }

        return this.tryToSetAutomaticMode(pageTemplateViewer);
    }

    private tryToSetAutomaticMode(pageTemplateViewer: PageTemplateViewer): wemQ.Promise<PageTemplateViewer> {
        return new GetNearestSiteRequest(this.content.getContentId()).sendAndParse().then((site: Site) => {

            return this.loadDefaultModels(site, this.content.getType()).then((defaultModels: DefaultModels) => {

                if (defaultModels && defaultModels.hasPageTemplate()) {
                    pageTemplateViewer.setPageMode(PageMode.AUTOMATIC);
                    pageTemplateViewer.setPageTemplate(defaultModels.getPageTemplate());
                }

                return wemQ<PageTemplateViewer>(pageTemplateViewer);
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
        this.pageTemplateViewer = null;

        return new GetContentByIdRequest(this.content.getContentId()).sendAndParse().then((content: Content) => {
            return this.getPageTemplateInfo(content).then((pageTemplateViewer: PageTemplateViewer) => {
                this.pageTemplateViewer = pageTemplateViewer;
            });
        });
    }
}

class PageTemplateViewer {
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
            return i18n('widget.pagetemplate.automatic');
        case PageMode.FORCED_CONTROLLER:
            return i18n('widget.pagetemplate.forcedcontroller');
        case PageMode.FORCED_TEMPLATE:
            return i18n('widget.pagetemplate.forcedtemplate');
        case PageMode.FRAGMENT:
            return i18n('widget.pagetemplate.fragment');
        default:
            return i18n('widget.pagetemplate.default');
        }
    }

    private isRenderable(): boolean {
        return this.pageMode !== PageMode.NO_CONTROLLER;
    }

    private getPageTemplateLinkEl(): api.dom.AEl {
        const pageTemplateEl = new api.dom.AEl();
        pageTemplateEl.setHtml(this.pageTemplate.getDisplayName());
        pageTemplateEl.setTitle(this.pageTemplate.getPath().toString());

        pageTemplateEl.onClicked(() => {
            new EditContentEvent([ContentSummaryAndCompareStatus.fromContentSummary(this.pageTemplate)]).fire();
        });

        return pageTemplateEl;
    }

    private getEmptyDescriptorEl(): api.dom.SpanEl {
        const emptyEl = new api.dom.SpanEl();

        emptyEl.setHtml(i18n('widget.pagetemplate.notfound'));
        return emptyEl;
    }

    private getDescriptorEl(): api.dom.Element {

        if (!(this.pageTemplate || this.pageController)) {
            return this.getEmptyDescriptorEl();
        }

        if (this.pageTemplate) {
            return this.getPageTemplateLinkEl();
        }

        const spanEl = new api.dom.SpanEl();
        spanEl.setHtml(this.pageController.getDisplayName());
        spanEl.getEl().setTitle(this.pageController.getKey().toString());

        return spanEl;
    }

    render(): api.dom.DivEl {
        let divEl = new api.dom.DivEl('page-template-viewer');

        if (!this.isRenderable()) {
            const noTemplateText = new api.dom.PEl('no-template');
            noTemplateText.setHtml(this.getPageModeString());

            divEl.appendChild(noTemplateText);

            return divEl;
        }

        let pageTemplateView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();

        pageTemplateView.setMainName(this.getPageModeString());

        if (this.pageMode == PageMode.FRAGMENT) {
            pageTemplateView.setIconClass(api.StyleHelper.getCommonIconCls('fragment'));
        } else {
            const descriptorEl = this.getDescriptorEl();
            if (descriptorEl) {
                pageTemplateView.setSubNameElements([descriptorEl]);
            }
            if (this.pageMode == PageMode.AUTOMATIC) {
                pageTemplateView.setIconClass('icon-wand');
            } else if (this.pageMode == PageMode.FORCED_TEMPLATE) {
                pageTemplateView.setIconClass('icon-newspaper');
            } else if (this.pageMode == PageMode.FORCED_CONTROLLER) {
                pageTemplateView.setIconClass('icon-cog');
            }
        }

        divEl.appendChildren(pageTemplateView);

        return divEl;
    }
}
