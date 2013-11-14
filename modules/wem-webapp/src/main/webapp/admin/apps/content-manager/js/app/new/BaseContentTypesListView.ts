module app_new {

    export class BaseContentTypesListView extends api_dom.DivEl implements api_event.Observable {

        private contentTypesList:ContentTypesList;

        constructor(idPrefix:string, title:string,className?:string) {
            super(idPrefix, className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml(title);
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();
            this.appendChild(this.contentTypesList);
        }

        addListener(listener:ContentTypesListListener) {
            this.contentTypesList.addListener(listener);
        }

        removeListener(listener:ContentTypesListListener) {
            this.contentTypesList.removeListener(listener);
        }

        refreshContentTypes(recentArray:api_schema_content.ContentTypeName[] ) {

            if (recentArray.length > 0) {

                var requestPromises:JQueryPromise<api_rest.Response>[] = [];
                recentArray.forEach( (name:api_schema_content.ContentTypeName) => {
                    var request = new api_schema_content.GetContentTypeByQualifiedNameRequest(name);
                    requestPromises.push(request.send());
                } );

                jQuery.when.apply(this, requestPromises).
                    done( () => {
                         var responses:api_rest.JsonResponse<api_schema_content_json.ContentTypeSummaryJson>[] = <any>arguments;

                         var contentTypes:api_schema_content.ContentTypeSummary[] = [];

                         for(var i = 0; i < responses.length; i++) {
                            var response = responses[i];
                            var contentType = new api_schema_content.ContentTypeSummary(response.getResult());
                            contentTypes.push(contentType);
                            this.contentTypesList.setContentTypes(contentTypes);
                         }
                     });
            }
            else {
                this.contentTypesList.setContentTypes([]);
            }

        }
    }

}