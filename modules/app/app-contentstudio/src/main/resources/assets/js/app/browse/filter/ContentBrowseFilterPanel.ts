import '../../../api.ts';
import {ContentBrowseSearchData} from './ContentBrowseSearchData';

import ContentQueryRequest = api.content.resource.ContentQueryRequest;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentSummaryJson = api.content.json.ContentSummaryJson;
import ContentQueryResult = api.content.resource.result.ContentQueryResult;
import ContentSummary = api.content.ContentSummary;
import AggregationTypeWrapperJson = api.aggregation.AggregationTypeWrapperJson;
import AggregationGroupView = api.aggregation.AggregationGroupView;
import ContentTypeAggregationGroupView = api.aggregation.ContentTypeAggregationGroupView;
import Aggregation = api.aggregation.Aggregation;
import AggregationFactory = api.aggregation.AggregationFactory;
import SearchInputValues = api.query.SearchInputValues;
import ContentQuery = api.content.query.ContentQuery;
import TermsAggregationQuery = api.query.aggregation.TermsAggregationQuery;
import DateRangeAggregationQuery = api.query.aggregation.DateRangeAggregationQuery;
import DateRange = api.query.aggregation.DateRange;
import QueryExpr = api.query.expr.QueryExpr;
import CompareExpr = api.query.expr.CompareExpr;
import LogicalExpr = api.query.expr.LogicalExpr;
import ValueExpr = api.query.expr.ValueExpr;
import LogicalOperator = api.query.expr.LogicalOperator;
import LogicalExp = api.query.expr.LogicalExpr;
import FieldExpr = api.query.expr.FieldExpr;
import Value = api.data.Value;
import ValueTypes = api.data.ValueTypes;
import QueryField = api.query.QueryField;
import ContentSummaryViewer = api.content.ContentSummaryViewer;
import ActionButton = api.ui.button.ActionButton;
import Action = api.ui.Action;
import BrowseFilterResetEvent = api.app.browse.filter.BrowseFilterResetEvent;
import BrowseFilterRefreshEvent = api.app.browse.filter.BrowseFilterRefreshEvent;
import BrowseFilterSearchEvent = api.app.browse.filter.BrowseFilterSearchEvent;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import i18n = api.util.i18n;

export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel<ContentSummaryAndCompareStatus> {

    static CONTENT_TYPE_AGGREGATION_NAME: string = 'contentTypes';
    static LAST_MODIFIED_AGGREGATION_NAME: string = 'lastModified';
    static CONTENT_TYPE_AGGREGATION_DISPLAY_NAME: string = i18n('field.contentTypes');
    static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = i18n('field.lastModified');

    private contentTypeAggregation: ContentTypeAggregationGroupView;
    private lastModifiedAggregation: AggregationGroupView;

    private dependenciesSection: DependenciesSection;

    constructor() {

        super();

        this.initAggregationGroupView([this.contentTypeAggregation, this.lastModifiedAggregation]);
    }

    protected getGroupViews(): api.aggregation.AggregationGroupView[] {
        this.contentTypeAggregation = new ContentTypeAggregationGroupView(
            ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME,
            ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_DISPLAY_NAME);

        this.lastModifiedAggregation = new AggregationGroupView(
            ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME,
            ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

        return [this.contentTypeAggregation, this.lastModifiedAggregation];
    }

    protected appendExtraSections() {
        super.appendExtraSections();
        this.dependenciesSection = new DependenciesSection(this.removeDependencyItemCallback.bind(this));
        this.appendChild(this.dependenciesSection);
    }

    private removeDependencyItemCallback() {
        this.resetConstraints();
        this.dependenciesSection.reset();
        this.search();
    }

    public setDependencyItem(item: ContentSummary, inbound: boolean) {
        this.dependenciesSection.setInbound(inbound);
        this.setConstraintItems(this.dependenciesSection, [ContentSummaryAndCompareStatus.fromContentSummary(item)]);
    }

    doRefresh() {
        if (!this.isFilteredOrConstrained()) {
            this.handleEmptyFilterInput(true);
        } else {
            this.refreshDataAndHandleResponse(this.createContentQuery());
        }
    }

    doSearch(elementChanged?: api.dom.Element) {
        if (!this.isFilteredOrConstrained()) {
            this.handleEmptyFilterInput();
        } else {
            this.searchDataAndHandleResponse(this.createContentQuery());
        }
    }

    setSelectedItems(items: ContentSummaryAndCompareStatus[]) {
        this.dependenciesSection.reset();

        super.setSelectedItems(items);
    }

    protected isFilteredOrConstrained() {
        return super.isFilteredOrConstrained() || this.dependenciesSection.isActive();
    }

    private handleEmptyFilterInput(isRefresh?: boolean) {
        if (isRefresh) {

            this.resetFacets(true, true).then(() => {
                new BrowseFilterRefreshEvent().fire();
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

        } else { // it's SearchEvent, usual reset with grid reload
            this.reset();
        }
    }

    private createContentQuery(): ContentQuery {
        let contentQuery: ContentQuery = new ContentQuery();
        let values = this.getSearchInputValues();
        this.appendQueryExpression(values, contentQuery);
        this.appendContentTypeFilter(values, contentQuery);
        if (!!this.dependenciesSection && this.dependenciesSection.isOutbound()) {
            this.appendOutboundReferencesFilter(contentQuery);
        }

        let lastModifiedFilter: api.query.filter.Filter = this.appendLastModifiedQuery(values);
        if (lastModifiedFilter != null) {
            contentQuery.addQueryFilter(lastModifiedFilter);
        }

        contentQuery.setSize(ContentQuery.POSTLOAD_SIZE);

        this.appendContentTypesAggregationQuery(contentQuery);
        this.appendLastModifiedAggregationQuery(contentQuery);

        return contentQuery;
    }

    private searchDataAndHandleResponse(contentQuery: ContentQuery) {
        new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).setExpand(api.rest.Expand.SUMMARY).sendAndParse().then(
            (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                this.handleDataSearchResult(contentQuery, contentQueryResult);
            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private refreshDataAndHandleResponse(contentQuery: ContentQuery) {
        new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).setExpand(api.rest.Expand.SUMMARY).sendAndParse().then(
            (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                if (contentQueryResult.getMetadata().getTotalHits() > 0) {
                    this.handleDataSearchResult(contentQuery, contentQueryResult);
                } else {
                    this.handleNoSearchResultOnRefresh(contentQuery);
                }
            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private handleDataSearchResult(contentQuery: ContentQuery,
                                   contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) {
        this.getAggregations(contentQuery, contentQueryResult).then((aggregations: Aggregation[]) => {
            this.updateAggregations(aggregations, true);
            this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits());
            this.toggleAggregationsVisibility(contentQueryResult.getAggregations());
            new BrowseFilterSearchEvent(new ContentBrowseSearchData(contentQueryResult, contentQuery)).fire();
        });
    }

    private handleNoSearchResultOnRefresh(contentQuery: ContentQuery) {
        // remove content type facet from search if both content types and date are filtered
        if (this.contentTypesAndRangeFiltersUsed(contentQuery)) {
            this.refreshDataAndHandleResponse(this.cloneContentQueryNoContentTypes(contentQuery));
        } else if (this.hasSearchStringSet()) { // if still no result and search text is set remove last modified facet
            this.deselectAll();
            this.searchDataAndHandleResponse(this.cloneContentQueryNoAggregations(contentQuery));
        } else {
            this.reset();
        }
    }

    private contentTypesAndRangeFiltersUsed(contentQuery: ContentQuery): boolean {
        return contentQuery.getContentTypes().length > 0 && contentQuery.getQueryFilters().length > 0;
    }

    private cloneContentQueryNoContentTypes(contentQuery: ContentQuery): ContentQuery {
        let newContentQuery: ContentQuery = new ContentQuery().setContentTypeNames([]).setFrom(contentQuery.getFrom()).setQueryExpr(
            contentQuery.getQueryExpr()).setSize(contentQuery.getSize()).setAggregationQueries(
            contentQuery.getAggregationQueries()).setQueryFilters(contentQuery.getQueryFilters()).setMustBeReferencedById(
            contentQuery.getMustBeReferencedById());

        return newContentQuery;
    }

    private cloneContentQueryNoAggregations(contentQuery: ContentQuery): ContentQuery {
        return this.cloneContentQueryNoContentTypes(contentQuery).setQueryFilters([]);
    }

    private getAggregations(contentQuery: ContentQuery,
                            contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>): wemQ.Promise<Aggregation[]> {

        let clonedContentQueryNoContentTypes: ContentQuery = this.cloneContentQueryNoContentTypes(contentQuery);

        if (api.ObjectHelper.objectEquals(contentQuery, clonedContentQueryNoContentTypes)) {
            return wemQ(this.combineAggregations(contentQueryResult, contentQueryResult));
        }

        return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(clonedContentQueryNoContentTypes).setExpand(
            api.rest.Expand.SUMMARY).sendAndParse().then(
            (contentQueryResultNoContentTypesSelected: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                return this.combineAggregations(contentQueryResult, contentQueryResultNoContentTypesSelected);
            });
    }

    private combineAggregations(contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>,
                                queryResultNoContentTypesSelected: ContentQueryResult<ContentSummary,ContentSummaryJson>): Aggregation[] {
        let contentTypesAggr = queryResultNoContentTypesSelected.getAggregations().filter((aggregation) => {
            return aggregation.getName() === ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME;
        });
        let dateModifiedAggr = contentQueryResult.getAggregations().filter((aggregation) => {
            return aggregation.getName() !== ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME;
        });

        let aggregations = [contentTypesAggr[0], dateModifiedAggr[0]];

        return aggregations;
    }

    private initAggregationGroupView(aggregationGroupViews: AggregationGroupView[]) {

        let contentQuery: ContentQuery = this.buildAggregationsQuery();

        new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).sendAndParse().then(
            (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                this.updateAggregations(contentQueryResult.getAggregations(), false);
                this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits(), true);
                this.toggleAggregationsVisibility(contentQueryResult.getAggregations());

                aggregationGroupViews.forEach((aggregationGroupView: AggregationGroupView) => {
                    aggregationGroupView.initialize();
                });
            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    protected resetFacets(suppressEvent?: boolean, doResetAll?: boolean) {

        let contentQuery: ContentQuery = this.buildAggregationsQuery();

        return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).sendAndParse().then(
            (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                this.updateAggregations(contentQueryResult.getAggregations(), doResetAll);
                this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits(), true);
                this.toggleAggregationsVisibility(contentQueryResult.getAggregations());

                if (!suppressEvent) { // then fire usual reset event with content grid reloading
                    if (!!this.dependenciesSection && this.dependenciesSection.isActive()) {
                        new BrowseFilterSearchEvent(new ContentBrowseSearchData(contentQueryResult, contentQuery)).fire();
                    } else {
                        new BrowseFilterResetEvent().fire();
                    }
                }
            }
        ).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });
    }

    private buildAggregationsQuery(): ContentQuery {
        let contentQuery: ContentQuery = new ContentQuery();
        contentQuery.setQueryExpr(new QueryExpr(null));
        contentQuery.setSize(0);

        this.appendFilterByItems(contentQuery);
        this.appendContentTypesAggregationQuery(contentQuery);
        this.appendLastModifiedAggregationQuery(contentQuery);
        if (!!this.dependenciesSection && this.dependenciesSection.isOutbound()) {
            this.appendOutboundReferencesFilter(contentQuery);
        }

        return contentQuery;
    }

    private appendQueryExpression(searchInputValues: SearchInputValues, contentQuery: ContentQuery) {
        let selectionMode = this.hasConstraint();
        let fulltextSearchExpression = this.makeFulltextSearchExpr(searchInputValues);
        let query: QueryExpr;

        if (selectionMode || this.dependenciesSection.isInbound()) {
            query = new QueryExpr(new LogicalExpr(fulltextSearchExpression,
                                            LogicalOperator.AND,
                                            selectionMode ?
                                            this.makeSelectedItemsSearchExpr() : this.makeInboundDependenciesSearchExpr()
                            ));
        } else {
            query = new QueryExpr(fulltextSearchExpression);
        }

        contentQuery.setQueryExpr(query);
    }

    private makeSelectedItemsSearchExpr(): api.query.expr.Expression {
        let selectedItems = this.getSelectionItems();
        let query: QueryExpr;

        selectedItems.forEach((content: ContentSummaryAndCompareStatus) => {
            if (!!query) {
                query = new QueryExpr(new LogicalExpr(query, LogicalOperator.OR,
                    CompareExpr.eq(new FieldExpr(QueryField.ID), ValueExpr.string(content.getId()))));
            } else {
                query = new QueryExpr(CompareExpr.eq(new FieldExpr(QueryField.ID), ValueExpr.string(content.getId())));
            }
        });

        return query;
    }

    private makeInboundDependenciesSearchExpr(): api.query.expr.Expression {
        let dependencyId = this.dependenciesSection.getDependencyId().toString();

        let query: QueryExpr = new QueryExpr(new LogicalExpr(
            CompareExpr.eq(new FieldExpr(QueryField.REFERENCES), ValueExpr.string(dependencyId)),
            LogicalOperator.AND,
            CompareExpr.neq(new FieldExpr(QueryField.ID), ValueExpr.string(dependencyId))));

        return query;
    }

    private makeFulltextSearchExpr(searchInputValues: SearchInputValues): api.query.expr.Expression {

        let searchString: string = searchInputValues.getTextSearchFieldValue();

        return new api.query.FulltextSearchExpressionBuilder().setSearchString(
            searchString).addField(new QueryField(QueryField.DISPLAY_NAME, 5)).addField(new QueryField(QueryField.NAME, 3)).addField(
            new QueryField(QueryField.ALL)).build();
    }

    private appendContentTypeFilter(searchInputValues: SearchInputValues, contentQuery: ContentQuery): void {
        let selectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName(
            ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME);

        let contentTypeNames: ContentTypeName[] = this.parseContentTypeNames(selectedBuckets);

        contentQuery.setContentTypeNames(contentTypeNames);
    }

    private appendFilterByItems(contentQuery: ContentQuery): void {
        if (!!this.dependenciesSection && this.dependenciesSection.isInbound()) {
            contentQuery.setQueryExpr(new QueryExpr(this.makeInboundDependenciesSearchExpr()));

            return;
        }

        if (this.hasConstraint()) {
            contentQuery.setQueryExpr(new QueryExpr(this.makeSelectedItemsSearchExpr()));

            return;
        }
    }

    private appendOutboundReferencesFilter(contentQuery: ContentQuery): void {
        contentQuery.setMustBeReferencedById(this.dependenciesSection.getDependencyId());
    }

    private appendLastModifiedQuery(searchInputValues: api.query.SearchInputValues): api.query.filter.Filter {

        let lastModifiedSelectedBuckets: api.aggregation.Bucket[] = searchInputValues.getSelectedValuesForAggregationName(
            ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME);

        if (lastModifiedSelectedBuckets == null || lastModifiedSelectedBuckets.length === 0) {
            return null;
        }

        if (lastModifiedSelectedBuckets.length === 1) {
            let dateRangeBucket: api.aggregation.DateRangeBucket = <api.aggregation.DateRangeBucket> lastModifiedSelectedBuckets.pop();
            return new api.query.filter.RangeFilter(QueryField.MODIFIED_TIME, ValueExpr.dateTime(dateRangeBucket.getFrom()).getValue(),
                null);
        }

        let booleanFilter: api.query.filter.BooleanFilter = new api.query.filter.BooleanFilter();

        lastModifiedSelectedBuckets.forEach((selectedBucket: api.aggregation.DateRangeBucket) => {
            let rangeFilter: api.query.filter.RangeFilter =
                new api.query.filter.RangeFilter(QueryField.MODIFIED_TIME, ValueExpr.dateTime(selectedBucket.getFrom()).getValue(),
                    null);

            booleanFilter.addShould(<api.query.filter.Filter>rangeFilter);
        });

        return booleanFilter;
    }

    private parseContentTypeNames(buckets: api.aggregation.Bucket[]): ContentTypeName[] {
        let contentTypeNames: ContentTypeName[] = [];

        if (buckets) {
            for (let i = 0; i < buckets.length; i++) {
                let bucket: api.aggregation.Bucket = buckets[i];
                contentTypeNames.push(new ContentTypeName(bucket.getKey()));
            }
        }

        return contentTypeNames;
    }

    private appendContentTypesAggregationQuery(contentQuery: ContentQuery) {
        contentQuery.addAggregationQuery(this.createTermsAggregation((ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME),
            QueryField.CONTENT_TYPE, 0));
    }

    private createTermsAggregation(name: string, fieldName: string, size: number): TermsAggregationQuery {
        let termsAggregation = new TermsAggregationQuery(name);
        termsAggregation.setFieldName(fieldName);
        termsAggregation.setSize(size);
        termsAggregation.setOrderByType(api.query.aggregation.TermsAggregationOrderType.DOC_COUNT);
        termsAggregation.setOrderByDirection(api.query.aggregation.TermsAggregationOrderDirection.DESC);
        return termsAggregation;
    }

    private appendLastModifiedAggregationQuery(contentQuery: ContentQuery) {

        let dateRangeAgg = new DateRangeAggregationQuery((ContentBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME));
        dateRangeAgg.setFieldName(QueryField.MODIFIED_TIME);
        dateRangeAgg.addRange(new DateRange('now-1h', null, i18n('field.lastModified.lessHour')));
        dateRangeAgg.addRange(new DateRange('now-1d', null, i18n('field.lastModified.lessDay')));
        dateRangeAgg.addRange(new DateRange('now-1w', null, i18n('field.lastModified.lessWeek')));

        contentQuery.addAggregationQuery(dateRangeAgg);
    }

    private toggleAggregationsVisibility(aggregations: Aggregation[]) {
        aggregations.forEach((aggregation: api.aggregation.BucketAggregation) => {
            let aggregationIsEmpty = !aggregation.getBuckets().some((bucket: api.aggregation.Bucket) => {
                if (bucket.docCount > 0) {
                    return true;
                }
            });

            let aggregationGroupView = aggregation.getName() === ContentBrowseFilterPanel.CONTENT_TYPE_AGGREGATION_NAME
                ? this.contentTypeAggregation
                : this.lastModifiedAggregation;

            if (aggregationIsEmpty) {
                aggregationGroupView.hide();
            } else {
                aggregationGroupView.show();
            }
        });
    }

}

export class DependenciesSection extends api.app.browse.filter.ConstraintSection<ContentSummaryAndCompareStatus> {
    private viewer: ContentSummaryViewer = new ContentSummaryViewer();

    private inbound: boolean = true;

    constructor(closeCallback: () => void) {
        super('', closeCallback);

        this.addClass('dependency');
        this.viewer.addClass('dependency-item');
        this.appendChild(this.viewer);
    }

    public getDependencyId(): api.content.ContentId {
        return this.getDependencyItem().getContentId();
    }

    public getDependencyItem(): ContentSummaryAndCompareStatus {
        return this.getItems()[0];
    }

    public isInbound(): boolean {
        return this.isActive() && this.inbound;
    }

    public isOutbound(): boolean {
        return this.isActive() && !this.inbound;
    }

    public setInbound(inbound: boolean) {
        this.inbound = inbound;
        this.setLabel(inbound ? i18n('panel.filter.dependencies.inbound') : i18n('panel.filter.dependencies.outbound'));
    }

    public setItems(items: ContentSummaryAndCompareStatus[]) {

        super.setItems(items);

        let dependencyItem = this.getDependencyItem();

        if (!!dependencyItem) {
            this.viewer.setObject(dependencyItem.getContentSummary());
        }
    }
}
