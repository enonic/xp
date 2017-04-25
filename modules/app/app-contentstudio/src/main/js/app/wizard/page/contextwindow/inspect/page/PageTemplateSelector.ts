import '../../../../../../api.ts';
import {PageTemplateOption} from './PageTemplateOption';
import {PageTemplateOptionViewer} from './PageTemplateOptionViewer';

import PropertyChangedEvent = api.PropertyChangedEvent;
import ContentId = api.content.ContentId;
import PageTemplateKey = api.content.page.PageTemplateKey;
import PageTemplate = api.content.page.PageTemplate;
import PageTemplateBuilder = api.content.page.PageTemplateBuilder;
import Option = api.ui.selector.Option;
import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
import Dropdown = api.ui.selector.dropdown.Dropdown;
import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
import PageModel = api.content.page.PageModel;
import LiveEditModel = api.liveedit.LiveEditModel;
import PageMode = api.content.page.PageMode;
import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
import PageTemplateLoader = api.content.page.PageTemplateLoader;

export class PageTemplateSelector extends Dropdown<PageTemplateOption> {

    private customizedOption: Option<PageTemplateOption>;

    private autoOption: Option<PageTemplateOption>;

    constructor(liveEditModel: LiveEditModel) {
        super('pageTemplate', <DropdownConfig<PageTemplateOption>>{
            optionDisplayValueViewer: new PageTemplateOptionViewer(liveEditModel.getPageModel().getDefaultPageTemplate())
        });

        this.loadPageTemplates(liveEditModel).then((options: Option<PageTemplateOption>[]) => {

            this.initOptionsList(options);

            this.selectInitialOption(liveEditModel.getPageModel());

            this.initPageModelListeners(liveEditModel.getPageModel());

        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private loadPageTemplates(liveEditModel: LiveEditModel): wemQ.Promise<Option<PageTemplateOption>[]> {

        let deferred = wemQ.defer<Option<PageTemplateOption>[]>();

        let loader = new PageTemplateLoader(new GetPageTemplatesByCanRenderRequest(liveEditModel.getSiteModel().getSiteId(),
            liveEditModel.getContent().getType()));

        loader.onLoadedData((event: LoadedDataEvent<PageTemplate>) => {

            let options: Option<PageTemplateOption>[] = [];

            event.getData().forEach((pageTemplate: PageTemplate) => options.push(this.createPageTemplateOption(pageTemplate)));

            deferred.resolve(options);
        });

        loader.load();

        return deferred.promise;
    }

    private createPageTemplateOption(pageTemplate: PageTemplate): Option<PageTemplateOption> {
        let indices: string[] = [];
        indices.push(pageTemplate.getName().toString());
        indices.push(pageTemplate.getDisplayName());
        indices.push(pageTemplate.getController().toString());

        let option = {
            value: pageTemplate.getId().toString(),
            displayValue: new PageTemplateOption(pageTemplate),
            indices: indices
        };

        return option;
    }

    private initOptionsList(options: Option<PageTemplateOption>[]) {
        this.autoOption = {value: '__auto__', displayValue: new PageTemplateOption()};
        this.addOption(this.autoOption);

        options.forEach((option: Option<PageTemplateOption>) => {
            this.addOption(option);
        });

        this.customizedOption = this.createCustomizedOption();
        this.addOption(this.customizedOption);
    }

    private selectInitialOption(pageModel: PageModel) {
        if (pageModel.isCustomized()) {
            this.selectOption(this.customizedOption, true);
        } else if (pageModel.hasTemplate()) {
            this.selectTemplate(pageModel.getTemplateKey());
        } else {
            this.selectOption(this.autoOption, true);
        }
    }

    private initPageModelListeners(pageModel: PageModel) {
        pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
            if (event.getPropertyName() === PageModel.PROPERTY_TEMPLATE && this !== event.getSource()) {
                let pageTemplateKey = <PageTemplateKey>event.getNewValue();
                if (pageTemplateKey) {
                    this.selectTemplate(pageTemplateKey);
                } else {
                    this.selectOption(this.autoOption, true);
                }
            } else if (event.getPropertyName() === PageModel.PROPERTY_CONTROLLER && event.getNewValue()) {
                this.selectOption(this.customizedOption, true);
            }
        });

        pageModel.onReset(() => {
            this.selectOption(this.autoOption, true);
        });
    }

    private selectTemplate(template: PageTemplateKey) {
        let optionToSelect = this.getOptionByValue(template.toString());
        if (optionToSelect) {
            this.selectOption(optionToSelect, true);
        }
    }

    private createCustomizedOption(): Option<PageTemplateOption> {
        let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;
        let pageTemplate: PageTemplate = (<PageTemplateBuilder> new PageTemplateBuilder()
            .setData(new api.data.PropertyTree())
            .setDisplayName(pageTemplateDisplayName[pageTemplateDisplayName.Custom]))
            .build();
        let option = {
            value: 'Customized',
            displayValue: new PageTemplateOption(pageTemplate)
        };

        return option;
    }
}
