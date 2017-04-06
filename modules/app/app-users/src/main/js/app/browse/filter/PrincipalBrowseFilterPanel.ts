import '../../../api.ts';
import {UserTreeGridItem} from '../UserTreeGridItem';
import AggregationGroupView = api.aggregation.AggregationGroupView;
import SearchInputValues = api.query.SearchInputValues;
import Principal = api.security.Principal;
import FindPrincipalsRequest = api.security.FindPrincipalsRequest;
import PrincipalType = api.security.PrincipalType;
import BrowseFilterResetEvent = api.app.browse.filter.BrowseFilterResetEvent;
import BrowseFilterSearchEvent = api.app.browse.filter.BrowseFilterSearchEvent;
import QueryExpr = api.query.expr.QueryExpr;
import CompareExpr = api.query.expr.CompareExpr;
import LogicalExpr = api.query.expr.LogicalExpr;
import ValueExpr = api.query.expr.ValueExpr;
import LogicalOperator = api.query.expr.LogicalOperator;
import LogicalExp = api.query.expr.LogicalExpr;
import FieldExpr = api.query.expr.FieldExpr;
import QueryField = api.query.QueryField;

export class PrincipalBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel<UserTreeGridItem> {

    constructor() {
        super();

        this.initHitsCounter();
    }

    doRefresh() {
        this.searchFacets(true);
    }

    doSearch(elementChanged?: api.dom.Element) {
        this.searchFacets();
    }

    protected resetFacets(supressEvent?: boolean, doResetAll?: boolean) {
        this.searchDataAndHandleResponse('', false);

        if (!supressEvent) { // then fire usual reset event with content grid reloading
            new BrowseFilterResetEvent().fire();
        }
    }

    private searchFacets(isRefresh: boolean = false) {
        let values = this.getSearchInputValues();
        let searchText = values.getTextSearchFieldValue();
        if (!searchText && !this.hasConstraint()) {
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

        let findPrincipalsRequest = new FindPrincipalsRequest()
            .setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE])
            .setSearchQuery(searchString);

        if (this.hasConstraint()) {
            let principalKeys = this.getConstraintItems().map(key => key.getDataId());

            findPrincipalsRequest.setResultFilter(
                (principal: Principal) => principalKeys.some(pr => pr === principal.getKey().toString())
            );
        }

        findPrincipalsRequest
            .sendAndParse()
            .then((result: api.security.FindPrincipalsResult) => {

            let principals = result.getPrincipals();
            if (fireEvent) {
                new BrowseFilterSearchEvent(principals).fire();
            }
            this.updateHitsCounter(principals ? principals.length : 0, api.util.StringHelper.isBlank(searchString));
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private initHitsCounter() {
        this.searchDataAndHandleResponse('', false);
    }

}
