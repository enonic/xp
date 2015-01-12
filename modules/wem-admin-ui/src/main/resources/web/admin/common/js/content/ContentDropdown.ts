module api.content {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;

    export interface ContentDropdownConfig {

        loader: ContentSummaryLoader;

        allowedContentTypes: string[];
    }

    export class ContentDropdown extends Dropdown<ContentSummary> {

        constructor(name: string, config: ContentDropdownConfig) {

            super(name, <DropdownConfig<ContentSummary>>{
                optionDisplayValueViewer: new api.content.ContentSummaryViewer(),
                dataIdProperty: "value"
            });

            config.loader.setAllowedContentTypes(config.allowedContentTypes);
            config.loader.onLoadedData((event: LoadedDataEvent<ContentSummary>) => {

                var contents: ContentSummary[] = event.getData();
                contents.forEach((content: ContentSummary) => {

                    var indices: string[] = [];
                    indices.push(content.getDisplayName());
                    indices.push(content.getName().toString());

                    var option = <Option<ContentSummary>>{
                        value: content.getContentId().toString(),
                        displayValue: content,
                        indices: indices
                    };

                    this.addOption(option);
                });
            });
        }

        setContent(id: ContentId) {
            var option = this.getOptionByValue(id.toString());
            if (option) {
                this.selectOption(option, true);
            }

        }
    }
}