import {NewContentDialogList} from "./NewContentDialogList";
import {NewContentDialogListItem} from "./NewContentDialogListItem";
import ContentTypeSummary = api.schema.content.ContentTypeSummary;
import Site = api.content.site.Site;
import ApplicationKey = api.application.ApplicationKey;

export class FilterableItemsList extends NewContentDialogList {

    private parentContent: api.content.Content;

    private listItems: NewContentDialogListItem[];

    constructor() {
        super();
    }

    filter(value: string) {
        let valueLowerCase = value ? value.toLowerCase() : undefined;

        let filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
            return (!valueLowerCase || (item.getDisplayName().toLowerCase().indexOf(valueLowerCase) != -1) ||
                    (item.getName().toLowerCase().indexOf(valueLowerCase) != -1));
        });

        this.setItems(filteredItems);
    }

    setParentContent(parent: api.content.Content) {
        this.parentContent = parent;
    }

    createItems(allContentTypes: ContentTypeSummary[], parentSite: Site) {
        let allListItems: NewContentDialogListItem[] = this.createListItems(allContentTypes);
        let siteApplications: ApplicationKey[] = parentSite ? parentSite.getApplicationKeys() : [];
        this.listItems = this.filterByParentContent(allListItems, siteApplications);
        this.setItems(this.listItems.slice());
    }

    private createListItems(contentTypes: ContentTypeSummary[]): NewContentDialogListItem[] {
        let contentTypesByName: {[name: string]: ContentTypeSummary} = {};
        let items: NewContentDialogListItem[] = [];

        contentTypes.forEach((contentType: ContentTypeSummary) => {
            // filter media type descendants out
            let contentTypeName = contentType.getContentTypeName();
            if (!contentTypeName.isMedia() && !contentTypeName.isDescendantOfMedia() && !contentTypeName.isFragment()) {
                contentTypesByName[contentType.getName()] = contentType;
                items.push(NewContentDialogListItem.fromContentType(contentType));
            }
        });

        items.sort(this.compareListItems);
        return items;
    }

    private compareListItems(item1: NewContentDialogListItem, item2: NewContentDialogListItem): number {
        if (item1.getDisplayName().toLowerCase() > item2.getDisplayName().toLowerCase()) {
            return 1;
        } else if (item1.getDisplayName().toLowerCase() < item2.getDisplayName().toLowerCase()) {
            return -1;
        } else if (item1.getName() > item2.getName()) {
            return 1;
        } else if (item1.getName() < item2.getName()) {
            return -1;
        } else {
            return 0;
        }
    }

    private filterByParentContent(items: NewContentDialogListItem[],
                                  siteApplicationKeys: ApplicationKey[]): NewContentDialogListItem[] {
        let createContentFilter = new api.content.util.CreateContentFilter().siteApplicationsFilter(siteApplicationKeys);
        return items.filter((item: NewContentDialogListItem) =>
            createContentFilter.isCreateContentAllowed(this.parentContent, item.getContentType())
        );
    }
}
