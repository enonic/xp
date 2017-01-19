import '../../../api.ts';
import {ContentBrowseResetEvent} from './ContentBrowseResetEvent';
import {ContentBrowseSearchEvent} from './ContentBrowseSearchEvent';
import {ContentBrowseRefreshEvent} from './ContentBrowseRefreshEvent';

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

export class ContentBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

    static CONTENT_TYPE_AGGREGATION_NAME: string = 'contentTypes';
    static LAST_MODIFIED_AGGREGATION_NAME: string = 'lastModified';
    static CONTENT_TYPE_AGGREGATION_DISPLAY_NAME: string = 'Content Types';
    static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = 'Last Modified';

    private contentTypeAggregation: ContentTypeAggregationGroupView;
    private lastModifiedAggregation: AggregationGroupView;

    private dependenciesSection: DependenciesSection;

    constructor() {

        super();

        this.initAggregationGroupView([this.contentTypeAggregation, this.lastModifiedAggregation]);

        this.onReset(()=> {
            this.resetFacets();
        });

        this.onShown(() => {
            this.refresh();
        });
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

    protected appendExtraSection() {
        this.dependenciesSection = new DependenciesSection(this.removeDependencyItemCallback.bind(this));
        this.appendChild(this.dependenciesSection);
    }

    private removeDependencyItemCallback() {
        this.removeClass('has-dependency-item');
        this.dependenciesSection.reset();
        this.search();
    }

    public setDependencyItem(item: ContentSummary, inbound: boolean) {
        this.addClass('has-dependency-item');
        this.dependenciesSection.setItem(item, inbound);
        if (this.dependenciesSection.isActive()) {
            this.reset(true);
            this.search();
        }
    }

    doRefresh() {
        if (!this.isAnyFilterSet()) {
            this.handleEmptyFilterInput(true);
        } else {
            this.refreshDataAndHandleResponse(this.createContentQuery());
        }
    }

    doSearch(elementChanged?: api.dom.Element) {
        if (!this.isAnyFilterSet()) {
            this.handleEmptyFilterInput();
        } else {
            this.searchDataAndHandleResponse(this.createContentQuery());
        }
    }

    private isAnyFilterSet(): boolean {
        return this.hasFilterSet() || this.dependenciesSection.isActive();
    }

    private handleEmptyFilterInput(isRefresh?: boolean) {
        if (isRefresh) {

            this.resetFacets(true, true).then(() => {
                new ContentBrowseRefreshEvent().fire();
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
        this.appendOutboundReferencesFilter(contentQuery);

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
            new ContentBrowseSearchEvent(contentQueryResult, contentQuery).fire();
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

    private resetFacets(suppressEvent?: boolean, doResetAll?: boolean) {

        let contentQuery: ContentQuery = this.buildAggregationsQuery();

        return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(contentQuery).sendAndParse().then(
            (contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {

                this.updateAggregations(contentQueryResult.getAggregations(), doResetAll);
                this.updateHitsCounter(contentQueryResult.getMetadata().getTotalHits(), true);
                this.toggleAggregationsVisibility(contentQueryResult.getAggregations());

                if (!suppressEvent) { // then fire usual reset event with content grid reloading
                    if (!!this.dependenciesSection && this.dependenciesSection.isActive()) {
                        new ContentBrowseSearchEvent(contentQueryResult, contentQuery).fire();
                    } else {
                        new ContentBrowseResetEvent().fire();
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

        this.appendInboundQueryExpr(contentQuery);
        this.appendContentTypesAggregationQuery(contentQuery);
        this.appendLastModifiedAggregationQuery(contentQuery);
        this.appendOutboundReferencesFilter(contentQuery);

        return contentQuery;
    }

    private appendQueryExpression(searchInputValues: SearchInputValues, contentQuery: ContentQuery) {
        let fulltextSearchExpression = this.makeFulltextSearchExpr(searchInputValues);
        let query: QueryExpr;

        if (this.dependenciesSection.isActive() && this.dependenciesSection.isInbound()) {
            query = new QueryExpr(new LogicalExpr(fulltextSearchExpression, LogicalOperator.AND, this.makeInboundDependenciesSearchExpr()));
        } else {
            query = new QueryExpr(fulltextSearchExpression);
        }

        contentQuery.setQueryExpr(query);
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

    private appendInboundQueryExpr(contentQuery: ContentQuery): void {
        if (!!this.dependenciesSection && this.dependenciesSection.isActive() && this.dependenciesSection.isInbound()) {
            contentQuery.setQueryExpr(new QueryExpr(this.makeInboundDependenciesSearchExpr()));
        }
    }

    private appendOutboundReferencesFilter(contentQuery: ContentQuery): void {
        if (!!this.dependenciesSection && this.dependenciesSection.isActive() && !this.dependenciesSection.isInbound()) {
            contentQuery.setMustBeReferencedById(this.dependenciesSection.getDependencyId());
        }
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
        dateRangeAgg.addRange(new DateRange('now-1h', null, '< 1 hour'));
        dateRangeAgg.addRange(new DateRange('now-1d', null, '< 1 day'));
        dateRangeAgg.addRange(new DateRange('now-1w', null, '< 1 week'));

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

export class DependenciesSection extends api.dom.DivEl {

    private inboundLabel: api.dom.LabelEl = new api.dom.LabelEl('Inbound Dependencies');
    private outboundLabel: api.dom.LabelEl = new api.dom.LabelEl('Outbound Dependencies');

    private dependencyItem: ContentSummary;
    private viewer: ContentSummaryViewer = new ContentSummaryViewer();

    private inbound: boolean = true;

    private closeButton: ActionButton;
    private closeCallback: () => void;

    constructor(closeCallback?: () => void) {
        super('dependencies-filter-section');

        this.checkVisibilityState();

        this.closeCallback = closeCallback;

        this.inboundLabel.setVisible(false);
        this.outboundLabel.setVisible(false);
        this.appendChildren(this.inboundLabel, this.outboundLabel);

        this.viewer.addClass('dependency-item');
        this.appendChild(this.viewer);

        this.closeButton = this.appendCloseButton();
    }

    private appendCloseButton(): ActionButton {
        let action = new Action('').onExecuted(() => {
            this.dependencyItem = null;
            this.checkVisibilityState();

            if (!!this.closeCallback) {
                this.closeCallback();
            }
        });
        let button = new ActionButton(action);

        button.addClass('btn-close');
        this.appendChild(button);

        return button;
    }

    public reset() {
        this.dependencyItem = null;
        this.checkVisibilityState();
    }

    public getDependencyId(): api.content.ContentId {
        return this.dependencyItem.getContentId();
    }

    public getDependencyItem(): ContentSummary {
        return this.dependencyItem;
    }

    private checkVisibilityState() {
        this.setVisible(this.isActive());
    }

    public isActive(): boolean {
        return !!this.dependencyItem;
    }

    public isInbound(): boolean {
        return this.inbound;
    }

    public setItem(item: ContentSummary, inbound: boolean) {

        this.inbound = inbound;
        this.showRelevantLabel();

        this.dependencyItem = item;

        if (!!item) {
            this.viewer.setObject(item);
        }

        this.checkVisibilityState();
    }

    private showRelevantLabel() {
        this.inboundLabel.setVisible(this.inbound);
        this.outboundLabel.setVisible(!this.inbound);
    }
}
