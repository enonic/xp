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

    private pageModel: PageModel;

    private selectionListeners: {(template: PageTemplate): void}[] = [];

    private customizedOption: Option<PageTemplateOption>;

    private autoOption: Option<PageTemplateOption>;

    constructor() {
        super('pageTemplate', <DropdownConfig<PageTemplateOption>>{
            optionDisplayValueViewer: new PageTemplateOptionViewer()
        });
    }

    setModel(liveEditModel: LiveEditModel) {

        this.pageModel = liveEditModel.getPageModel();

        this.loadPageTemplates(liveEditModel).then((options: Option<PageTemplateOption>[]) => {

            this.initOptionsList(options);

            this.selectInitialOption(options.length);

            this.onOptionSelected(this.handleOptionSelected.bind(this));

            this.initPageModelListeners();

        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private loadPageTemplates(liveEditModel: LiveEditModel): wemQ.Promise<Option<PageTemplateOption>[]> {

        let deferred = wemQ.defer<Option<PageTemplateOption>[]>();

        let options: Option<PageTemplateOption>[] = [];

        let loader = new PageTemplateLoader(new GetPageTemplatesByCanRenderRequest(liveEditModel.getSiteModel().getSiteId(),
            liveEditModel.getContent().getType()));
        loader.setComparator(new api.content.page.PageTemplateByDisplayNameComparator());
        loader.onLoadedData((event: LoadedDataEvent<PageTemplate>) => {

            event.getData().forEach((pageTemplate: PageTemplate) => {

                let indices: string[] = [];
                indices.push(pageTemplate.getName().toString());
                indices.push(pageTemplate.getDisplayName());
                indices.push(pageTemplate.getController().toString());

                let option = {
                    value: pageTemplate.getId().toString(),
                    displayValue: new PageTemplateOption(pageTemplate, this.pageModel),
                    indices: indices
                };
                options.push(option);
            });

            deferred.resolve(options);
        });

        loader.load();
        return deferred.promise;
    }

    private initOptionsList(options: Option<PageTemplateOption>[]) {
        this.autoOption = {value: '__auto__', displayValue: new PageTemplateOption(null, this.pageModel)};
        this.addOption(this.autoOption);

        options.forEach((option: Option<PageTemplateOption>) => {
            this.addOption(option);
        });

        this.customizedOption = this.createCustomizedOption();
        this.addOption(this.customizedOption);
    }

    private selectInitialOption(totalOptions: number) {
        if (this.pageModel.isCustomized()) {
            this.selectRow(totalOptions);
        } else if (this.pageModel.hasTemplate()) {
            this.selectTemplate(this.pageModel.getTemplateKey());
        } else {
            this.selectOption(this.autoOption, true);
        }
    }

    private handleOptionSelected(event: OptionSelectedEvent<PageTemplateOption>) {
        const selectedOption: PageTemplateOption = event.getOption().displayValue;
        const previousOption: PageTemplateOption = event.getPreviousOption().displayValue;

        if (selectedOption.equals(previousOption)) {
            return;
        }

        if (selectedOption.isCustom()) { // no reload => no confirmation dialog
            this.notifySelection(selectedOption.getPageTemplate());
            return;
        }

        if (previousOption.isAuto() && selectedOption.isDefault()) { // auto to default => no reload => no confirmation dialog
            this.notifySelection(selectedOption.getPageTemplate());
            return;
        }

        api.ui.dialog.ConfirmationDialog.get()
            .setQuestion(
                'Switching to the page template will result in losing all custom changes made to the page. Are you sure?')
            .setCloseCallback(() => {
                this.selectOption(event.getPreviousOption(), true); // reverting selection back
            })
            .setYesCallback(() => {
                this.notifySelection(selectedOption.getPageTemplate());
            }).open();
    }

    private initPageModelListeners() {
        this.pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
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

        this.pageModel.onReset(() => {
            this.selectOption(this.autoOption, true);
        });
    }

    private selectTemplate(template: PageTemplateKey) {
        let optionToSelect = this.getOptionByValue(template.toString());
        if (optionToSelect) {
            this.selectOption(optionToSelect, true);
        }
    }

    onSelection(listener: (event: PageTemplate)=>void) {
        this.selectionListeners.push(listener);
    }

    unSelection(listener: (event: PageTemplate)=>void) {
        this.selectionListeners.filter((currentListener: (event: PageTemplate)=>void) => {
            return listener !== currentListener;
        });
    }

    private notifySelection(item: PageTemplate) {
        this.selectionListeners.forEach((listener: (event: PageTemplate)=>void) => {
            listener(item);
        });
    }

    private createCustomizedOption(): Option<PageTemplateOption> {
        let pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;
        let pageTemplate: PageTemplate = (<PageTemplateBuilder> new PageTemplateBuilder()
            .setData(new api.data.PropertyTree())
            .setDisplayName(pageTemplateDisplayName[pageTemplateDisplayName.Custom]))
            .build();
        let option = {
            value: 'Customized',
            displayValue: new PageTemplateOption(pageTemplate, this.pageModel)
        };

        return option;
    }
}
