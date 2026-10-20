import type {BucketsAggregationResult, Content, SingleValueMetricAggregationResult, StatsAggregationResult} from '@enonic-types/core';
import {create, get, getSite, modify, query} from '/lib/xp/content';
import type {ContentFieldsHit, ContentIdHit, Site} from '/lib/xp/content';

import type {Assert, Equal} from '../assert';

interface ArticleData {
    title: string;
    tags: string[];
}

type Article = Content<ArticleData, 'com.example:article'>;

const article = get<Article>({key: '/site/article'});
export type GetInfersHit = Assert<Equal<typeof article, Article | null>>;

const created = create<ArticleData, 'com.example:article'>({
    parentPath: '/site',
    contentType: 'com.example:article',
    data: {title: 'Title', tags: []},
});
export type CreateKeepsData = Assert<Equal<typeof created, Article>>;

const modified = modify<ArticleData, 'com.example:article'>({
    key: '/site/article',
    editor: content => content,
});
export type ModifyKeepsData = Assert<Equal<typeof modified, Article | null>>;

const site = getSite<{theme: string}>({key: '/site'});
export type GetSiteKeepsConfig = Assert<Equal<typeof site, Site<{theme: string}> | null>>;

const aggregated = query<Article, {
    byTag: {terms: {field: 'data.tags'}};
    sizes: {stats: {field: 'attachment.size'}};
    largest: {max: {field: 'attachment.size'}};
}>({
    count: 10,
    aggregations: {
        byTag: {terms: {field: 'data.tags'}},
        sizes: {stats: {field: 'attachment.size'}},
        largest: {max: {field: 'attachment.size'}},
    },
});
export type QueryHits = Assert<Equal<typeof aggregated.hits, Article[]>>;
export type TermsResult = Assert<Equal<typeof aggregated.aggregations.byTag, BucketsAggregationResult<Record<never, never>>>>;
export type StatsResult = Assert<Equal<typeof aggregated.aggregations.sizes, StatsAggregationResult>>;
export type MaxResult = Assert<Equal<typeof aggregated.aggregations.largest, SingleValueMetricAggregationResult>>;

const ids = query({count: 10, returns: 'ids'});
export type IdHits = Assert<Equal<typeof ids.hits, ContentIdHit[]>>;

const fields = query({count: 10, returns: ['displayName']});
export type FieldHits = Assert<Equal<typeof fields.hits, ContentFieldsHit[]>>;

// @ts-expect-error `editor` is required
modify({key: '/site/article'});

// @ts-expect-error `contentType` is required
create({parentPath: '/site', data: {}});
