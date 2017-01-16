import "../../../../../../api.ts";
import {PageTemplateSelector} from "./PageTemplateSelector";
import {BaseInspectionPanel} from "../BaseInspectionPanel";
import {PageTemplateForm} from "./PageTemplateForm";
import {PageControllerForm} from "./PageControllerForm";
import {PageControllerSelector} from "./PageControllerSelector";

import PropertyChangedEvent = api.PropertyChangedEvent;
import PropertyTree = api.data.PropertyTree;
import FormContextBuilder = api.form.FormContextBuilder;
import FormView = api.form.FormView;
import FormContext = api.form.FormContext;
import Content = api.content.Content;
import Page = api.content.page.Page;
import SetTemplate = api.content.page.SetTemplate;
import PageModel = api.content.page.PageModel;
import PageMode = api.content.page.PageMode;
import SiteModel = api.content.site.SiteModel;
import LiveEditModel = api.liveedit.LiveEditModel;
import PageTemplate = api.content.page.PageTemplate;
import PageDescriptor = api.content.page.PageDescriptor;
import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
import SetController = api.content.page.SetController;

export class PageInspectionPanel extends BaseInspectionPanel {

    private liveEditModel: LiveEditModel;

    private siteModel: SiteModel;

    private pageModel: PageModel;

    private pageTemplateSelector: PageTemplateSelector;

    private pageTemplateForm: PageTemplateForm;

    private pageControllerForm: PageControllerForm;

    private pageControllerSelector: PageControllerSelector;

    private inspectionHandler: BaseInspectionHandler;

    constructor() {
        super();
    }

    setModel(liveEditModel: LiveEditModel) {

        this.liveEditModel = liveEditModel;
        this.pageModel = liveEditModel.getPageModel();
        this.siteModel = liveEditModel.getSiteModel();

        this.layout();
    }

    private layout() {

        this.pageModel.unReset(this.modelResetListener.bind(this));

        this.removeChildren();

        this.pageTemplateSelector = new PageTemplateSelector();
        this.pageTemplateForm = new PageTemplateForm(this.pageTemplateSelector);
        this.pageTemplateForm.hide();
        this.appendChild(this.pageTemplateForm);

        this.pageControllerSelector = new PageControllerSelector(this.liveEditModel);
        this.pageControllerForm = new PageControllerForm(this.pageControllerSelector);
        this.pageControllerForm.hide();
        this.appendChild(this.pageControllerForm);

        this.inspectionHandler = this.pageModel.isPageTemplate() ? new PageTemplateInspectionHandler() : new ContentInspectionHandler();

        // init page controller selector in case of 'customized' template chosen or no template presents
        if (!this.pageModel.isPageTemplate()) {
            this.pageControllerSelector.load();

            if (this.pageModel.isCustomized()) {
                this.addClass('customized');
            }

            if (this.pageModeImpliesPageControllerShown()) {
                this.pageControllerForm.show();
            }

        }

        this.inspectionHandler.setPageModel(this.pageModel).setPageInspectionPanel(this).setPageControllerForm(
            this.pageControllerForm).setPageTemplateForm(this.pageTemplateForm).setModel(this.liveEditModel);

        this.pageTemplateSelector.onSelection((pageTemplate: PageTemplate) => {
                this.removeClass('customized');
                this.pageModel.setCustomized(false);

                if (pageTemplate) {
                    this.pageControllerForm.hide();
                    new GetPageDescriptorByKeyRequest(pageTemplate.getController())
                        .sendAndParse()
                        .then((pageDescriptor: PageDescriptor) => {
                            let setTemplate = new SetTemplate(this).setTemplate(pageTemplate, pageDescriptor);
                            this.pageModel.setTemplate(setTemplate, true);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).done();
                } else if (this.pageModel.hasDefaultPageTemplate()) {
                    this.pageControllerForm.hide();
                    this.pageModel.setAutomaticTemplate(this, true);
                } else {
                    this.pageModel.reset(this);
                }
            }
        );

        this.pageTemplateSelector.onCustomizedSelected(() => {
            this.addClass('customized');
            this.pageControllerForm.getSelector().reset();
            this.pageControllerForm.show();

            this.pageModel.setCustomized(true);
            this.pageModel.setTemplateContoller();
        });

        this.pageModel.onReset(this.modelResetListener.bind(this));
    }

    modelResetListener() {
        if(this.pageControllerForm) {
            this.pageControllerForm.getSelector().reset();
        }
        if (!this.pageModel.isPageTemplate() && !(this.pageModel.isCustomized() && this.pageModel.hasController())) {
            this.pageControllerForm.hide();
        }
    }

    refreshInspectionHandler(liveEditModel: LiveEditModel) {
        this.inspectionHandler.refreshConfigView(liveEditModel);
    }

    private pageModeImpliesPageControllerShown(): boolean {
        return (this.pageModel.isCustomized() && this.pageModel.hasController()) ||
               this.pageModel.getMode() == PageMode.FORCED_CONTROLLER;
    }
}

class BaseInspectionHandler {

    pageModel: PageModel;

    pageInspectionPanel: PageInspectionPanel;

    configForm: FormView;

    pageControllerForm: PageControllerForm;

    pageTemplateForm: PageTemplateForm;

    private pageDescriptorForm: api.ui.form.Form;

    private propertyChangedListener: (event: PropertyChangedEvent) => void;

    setPageModel(value: PageModel): BaseInspectionHandler {
        this.pageModel = value;
        return this;
    }

    setPageInspectionPanel(value: PageInspectionPanel): BaseInspectionHandler {
        this.pageInspectionPanel = value;
        return this;
    }

    setPageControllerForm(value: PageControllerForm): BaseInspectionHandler {
        this.pageControllerForm = value;
        if (this.pageModel.isPageTemplate()) {
            this.pageDescriptorForm = value;
        }
        return this;
    }

    setPageTemplateForm(value: PageTemplateForm): BaseInspectionHandler {
        this.pageTemplateForm = value;
        if (!this.pageModel.isPageTemplate()) {
            this.pageDescriptorForm = value;
        }
        return this;
    }

    setModel(liveEditModel: LiveEditModel) {
        this.initListener(liveEditModel);

        this.showPageConfig(liveEditModel.getPageModel(), liveEditModel.getFormContext());

        if (liveEditModel.getPageModel().getMode() !== PageMode.FRAGMENT) {
            this.pageDescriptorForm.show();
        }
    }

    private initListener(liveEditModel: LiveEditModel) {
        let pageModel = liveEditModel.getPageModel();

        if (this.propertyChangedListener) {
            liveEditModel.getPageModel().unPropertyChanged(this.propertyChangedListener);
        }

        this.propertyChangedListener = (event: PropertyChangedEvent) => {
            if (this === event.getSource()) {
                return;
            }

            if ([PageModel.PROPERTY_CONFIG, PageModel.PROPERTY_CONTROLLER].indexOf(event.getPropertyName()) == -1) {
                return;
            }

            if (event.getPropertyName() == PageModel.PROPERTY_CONTROLLER) {
                this.pageControllerForm.show();
            } else {
                this.pageDescriptorForm.show();
            }

            let controller = pageModel.getController();
            if (controller) {
                this.refreshConfigForm(controller, pageModel.getConfig(), liveEditModel.getFormContext());
            }
        };

        pageModel.onPropertyChanged(this.propertyChangedListener);
    }

    private refreshConfigForm(pageDescriptor: PageDescriptor, config: PropertyTree, context: FormContext) {
        if (this.configForm) {
            this.configForm.remove();
            this.configForm = null;
        }

        if (!pageDescriptor) {
            return;
        }

        this.configForm =
            new FormView(context ? context : new FormContextBuilder().build(), pageDescriptor.getConfig(), config.getRoot());
        this.pageInspectionPanel.appendChild(this.configForm);
        this.pageModel.setIgnorePropertyChanges(true);
        this.configForm.layout().catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.pageModel.setIgnorePropertyChanges(false);
        }).done();
    }

    refreshConfigView(liveEditModel: LiveEditModel) {
        // must be implemented by children
    }

    protected showPageConfig(pageModel: PageModel, formContext: FormContext) {
        this.refreshConfigForm(pageModel.getDescriptor(), pageModel.getConfig(), formContext);
    }
}

class PageTemplateInspectionHandler extends BaseInspectionHandler {

    setModel(liveEditModel: LiveEditModel) {
        super.setModel(liveEditModel);

        this.pageControllerForm.getSelector().load();
    }
}

class ContentInspectionHandler extends BaseInspectionHandler {

    refreshConfigView(liveEditModel: LiveEditModel) {
        let pageModel = liveEditModel.getPageModel();
        let pageMode = pageModel.getMode();

        if (pageMode == PageMode.FORCED_TEMPLATE || pageMode == PageMode.AUTOMATIC) {
            this.showPageConfig(pageModel, liveEditModel.getFormContext());
        } else {
            throw new Error('Unsupported PageMode: ' + PageMode[pageMode]);
        }
    }

    setModel(liveEditModel: LiveEditModel) {
        super.setModel(liveEditModel);

        this.pageTemplateForm.getSelector().setModel(liveEditModel);
    }

}
