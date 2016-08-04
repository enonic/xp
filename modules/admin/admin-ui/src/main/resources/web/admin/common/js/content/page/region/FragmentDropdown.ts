module api.content.page.region {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryLoader = api.content.resource.ContentSummaryLoader;

    import RichDropdown = api.ui.selector.dropdown.RichDropdown;

    export class FragmentDropdown extends RichDropdown<ContentSummary> {

        constructor(name: string, loader: ContentSummaryLoader) {

            super(name, loader, {
                optionDisplayValueViewer: new ContentSummaryViewer(),
                dataIdProperty: "value"
            });
        }

        protected createOption(fragment: ContentSummary): Option<ContentSummary> {
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
                this.addOption(this.createOption(fragment));
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
