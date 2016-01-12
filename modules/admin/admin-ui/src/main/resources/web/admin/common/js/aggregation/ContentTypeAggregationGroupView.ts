module api.aggregation {

    export class ContentTypeAggregationGroupView extends AggregationGroupView {

        initialize() {

            var displayNameMap: {[name:string]:string} = {};

            var mask: api.ui.mask.LoadMask = new api.ui.mask.LoadMask(this);
            this.appendChild(mask);
            this.onRendered((event: api.dom.ElementRenderedEvent) => {
                mask.show();
            });

            var request = new api.schema.content.GetAllContentTypesRequest();
            request.sendAndParse().done((contentTypes: api.schema.content.ContentTypeSummary[]) => {

                contentTypes.forEach((contentType: api.schema.content.ContentTypeSummary)=> {
                    displayNameMap[contentType.getName().toLowerCase()] = contentType.getDisplayName();
                });

                this.getAggregationViews().forEach((aggregationView: api.aggregation.AggregationView)=> {
                    aggregationView.setDisplayNamesMap(displayNameMap);
                });
                mask.remove();
            });

        }


    }

}