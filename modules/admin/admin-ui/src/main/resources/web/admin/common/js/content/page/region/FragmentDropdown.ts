module api.content.page.region {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryLoader = api.content.ContentSummaryLoader;

    export interface FragmentDropdownConfig {
        loader: ContentSummaryLoader
    }

    export class FragmentDropdown extends Dropdown<ContentSummary> {

        constructor(name: string, config: FragmentDropdownConfig) {

            super(name, <DropdownConfig<ContentSummary>>{
                optionDisplayValueViewer: new ContentSummaryViewer(),
                dataIdProperty: "value"
            });

            config.loader.onLoadedData((event: LoadedDataEvent<ContentSummary>) => {

                var descriptors: ContentSummary[] = event.getData();
                descriptors.forEach((descriptor: ContentSummary) => {

                    var indices: string[] = [];
                    indices.push(descriptor.getDisplayName());
                    indices.push(descriptor.getName().toString());

                    var option = <Option<ContentSummary>>{
                        value: descriptor.getId().toString(),
                        displayValue: descriptor,
                        indices: indices
                    };

                    this.addOption(option);
                });
            });

            this.onExpanded(() => {
                this.removeAllOptions();
                config.loader.load();
            });
        }

        setSelection(descriptor: ContentSummary) {

            if (descriptor) {
                var option = this.getOptionByValue(descriptor.getId().toString());
                if (option) {
                    this.selectOption(option, true);
                }
            } else {
                this.reset();
            }
        }

        getSelection(contentId: ContentId): ContentSummary {
            let id = contentId.toString();
            if (id) {
                var option = this.getOptionByValue(id);
                if (option) {
                    return option.displayValue;
                }
            }
            return null;
        }
    }
}
