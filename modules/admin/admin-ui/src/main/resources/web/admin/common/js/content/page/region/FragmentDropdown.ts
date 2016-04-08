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

                var fragments: ContentSummary[] = event.getData();
                fragments.forEach((fragment: ContentSummary) => {
                    this.addOption(this.newOption(fragment));
                });
            });

            this.onExpanded(() => {
                this.removeAllOptions();
                config.loader.load();
            });
        }

        private newOption(fragment: ContentSummary): Option<ContentSummary> {
            let indices: string[] = [];
            indices.push(fragment.getDisplayName());
            indices.push(fragment.getName().toString());

            return <Option<ContentSummary>>{
                value: fragment.getId().toString(),
                displayValue: fragment,
                indices: indices
            };
        }

        addFragmentOption(fragment: ContentSummary) {
            if (fragment) {
                this.addOption(this.newOption(fragment));
            }
        }

        setSelection(fragment: ContentSummary) {

            if (fragment) {
                var option = this.getOptionByValue(fragment.getId().toString());
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
