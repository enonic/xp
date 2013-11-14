module app_new {

    export class ContentTypesList extends api_dom.DivEl implements api_event.Observable {

        private ul:api_dom.UlEl;

        private contentTypes:api_schema_content.ContentTypeSummary[];

        private listeners:ContentTypesListListener[] = [];

        constructor() {
            super("ContentTypeList", "content-type-list");

            this.ul = new api_dom.UlEl();
            this.appendChild(this.ul);
        }

        addListener(listener:ContentTypesListListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelected(contentType:api_schema_content.ContentTypeSummary) {
            this.listeners.forEach((listener:ContentTypesListListener) => {
                listener.onSelected(contentType);
            });
        }

        setContentTypes(contentTypes:api_schema_content.ContentTypeSummary[]) {
            this.contentTypes = contentTypes;
            this.layoutList(contentTypes);
        }

        filter(property:string, value:string) {
            if (!value || value.length == 0) {
                this.clearFilter();
            }
            var filteredContentTypes:api_schema_content.ContentTypeSummary[] = [];
            var regexp = new RegExp(value, 'i');

            for (var i = 0; i < this.contentTypes.length; i++) {
                var contentType = this.contentTypes[i];
                if (regexp.test(contentType[property])) {
                    filteredContentTypes.push(contentType);
                }
            }
            this.layoutList(filteredContentTypes);
        }

        clearFilter():ContentTypesList {
            this.layoutList(this.contentTypes);
            return this;
        }

        private layoutList(contentTypes:api_schema_content.ContentTypeSummary[]) {
            this.ul.removeChildren();

            for (var i = 0; i < contentTypes.length; i++) {
                var contentType = contentTypes[i];
                var listItemEl = this.createListItem(contentType);
                this.ul.appendChild(listItemEl);
            }
        }

        private createListItem(contentType:api_schema_content.ContentTypeSummary):ContentTypeListItemEl {

            var listItem = new ContentTypeListItem(contentType.getName(), contentType.getDisplayName(), contentType.getIcon());
            var listItemEl = new ContentTypeListItemEl(listItem);

            listItemEl.getEl().addEventListener("click", () => {
                this.notifySelected(contentType);
            });
            return listItemEl;
        }
    }

    export class ContentTypeListItemEl extends api_dom.LiEl {

        constructor(item:ContentTypeListItem) {
            super("ContentTypeListItem", "content-type-list-item");

            var img = new api_dom.ImgEl(item.getIconUrl());

            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(item.getDisplayName());

            var p = new api_dom.PEl();
            p.getEl().setInnerHtml(item.getName());

            this.appendChild(img);
            this.appendChild(h6);
            this.appendChild(p);
        }
    }
}