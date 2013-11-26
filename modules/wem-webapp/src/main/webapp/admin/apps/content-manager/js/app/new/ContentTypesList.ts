module app_new {

    export class ContentTypesList extends api_dom.DivEl implements api_event.Observable {

        private ul:api_dom.UlEl;

        private contentTypes:ContentTypes;

        private siteRootContentTypes:SiteRootContentTypes;

        private listeners:ContentTypesListListener[] = [];

        constructor(idPrefix:string, title:string, className?:string) {
            super(idPrefix, className);

            this.createHeader(title);

            this.ul = new api_dom.UlEl();
            this.ul.setClass('content-type-list');
            this.appendChild(this.ul);
        }

        createHeader(title:string) {
            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml(title);
            this.appendChild(h4);
        }

        addListener(listener:ContentTypesListListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelected(contentTypeListItem:ContentTypeListItem) {
            this.listeners.forEach((listener:ContentTypesListListener) => {
                listener.onSelected(contentTypeListItem);
            });
        }

        setContentTypes(contentTypes:ContentTypes, siteRootContentTypes:SiteRootContentTypes) {
            this.contentTypes = contentTypes;
            this.siteRootContentTypes = siteRootContentTypes;
            this.layoutList(this.contentTypes.get());
        }

        filter(property:string, value:string) {
            if (!value || value.length == 0) {
                this.clearFilter();
            }
            var filteredContentTypes:api_schema_content.ContentTypeSummary[] = [];
            var regexp = new RegExp(value, 'i');

            var contentTypes = this.contentTypes.get();
            for (var i = 0; i < contentTypes.length; i++) {
                var contentType = contentTypes[i];
                if (regexp.test(contentType[property])) {
                    filteredContentTypes.push(contentType);
                }
            }
            this.layoutList(filteredContentTypes);
        }

        clearFilter():ContentTypesList {
            this.layoutList(this.contentTypes.get());
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

        private createListItem(contentType:api_schema_content.ContentTypeSummary):ContentTypeListItemView {

            var isSiteRoot = this.siteRootContentTypes.isSiteRoot(contentType.getName());
            var listItem = new ContentTypeListItem(contentType, isSiteRoot);
            var listItemView = new ContentTypeListItemView(listItem);

            listItemView.getEl().addEventListener("click", () => {
                this.notifySelected(listItem);
            });
            return listItemView;
        }
    }

}