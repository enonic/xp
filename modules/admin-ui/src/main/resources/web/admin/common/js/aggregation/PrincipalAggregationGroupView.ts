module api.aggregation {

    export class PrincipalAggregationGroupView extends AggregationGroupView {

        initialize() {

            var displayNameMap: string[] = [];

            var mask: api.ui.mask.LoadMask = new api.ui.mask.LoadMask(this);
            this.appendChild(mask);
            this.onRendered((event: api.dom.ElementRenderedEvent) => {
                mask.show();
            });

            // displayNameMap["user"] = "User";
            //  displayNameMap["group"] = "Group";
            mask.remove();


        }


    }

}