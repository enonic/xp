import "../../../api.ts";
import {PrincipalBrowseResetEvent} from "./PrincipalBrowseResetEvent";
import {PrincipalBrowseSearchEvent} from "./PrincipalBrowseSearchEvent";

import AggregationGroupView = api.aggregation.AggregationGroupView;
import SearchInputValues = api.query.SearchInputValues;
import Principal = api.security.Principal;
import FindPrincipalsRequest = api.security.FindPrincipalsRequest;
import PrincipalType = api.security.PrincipalType;

export class PrincipalBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

    constructor() {

        super(null);

        this.onReset(()=> {
            this.resetFacets();
        });

        this.onShown(() => {
            this.refresh();
        });

        this.initHitsCounter();
    }

    doRefresh() {
        this.searchFacets(true);
    }

    doSearch(elementChanged?: api.dom.Element) {
        this.searchFacets();
    }

    private resetFacets(supressEvent?: boolean) {
        this.searchDataAndHandleResponse("", false);

        if (!supressEvent) { // then fire usual reset event with content grid reloading
            new PrincipalBrowseResetEvent().fire();
        }
    }

    private searchFacets(isRefresh: boolean = false) {
        let values = this.getSearchInputValues(),
            searchText = values.getTextSearchFieldValue();
        if (!searchText) {
            this.handleEmptyFilterInput(isRefresh);
            return;
        }

        this.searchDataAndHandleResponse(searchText);
    }

    private handleEmptyFilterInput(isRefresh: boolean) {
        if (isRefresh) {
            this.resetFacets(true);
        } else {
            this.reset();
        }
    }

    private searchDataAndHandleResponse(searchString: string, fireEvent: boolean = true) {
        new FindPrincipalsRequest().setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]).setSearchQuery(
            searchString).sendAndParse().then((result: api.security.FindPrincipalsResult) => {

            let principals = result.getPrincipals();
            if (fireEvent) {
                new PrincipalBrowseSearchEvent(principals).fire();
            }
            this.updateHitsCounter(principals ? principals.length : 0, api.util.StringHelper.isBlank(searchString));
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private initHitsCounter() {
        this.searchDataAndHandleResponse("", false);
    }
}
