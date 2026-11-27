import type {StatsAggregationResult} from '@enonic-types/core';
import {connect} from '/lib/xp/node';
import type {Node, RepoConnection} from '/lib/xp/node';

import type {Assert, Equal} from '../assert';

interface PageData {
    path: string;
}

const repo = connect({repoId: 'com.example', branch: 'master'});
export type ConnectResult = Assert<Equal<typeof repo, RepoConnection>>;

const single = repo.get<PageData>('/page');
export type GetSingle = Assert<Equal<typeof single, Node<PageData> | null>>;

const created = repo.create<PageData>({_name: 'page', path: '/page'});
export type CreateKeepsData = Assert<Equal<typeof created, Node<PageData>>>;

const result = repo.query({
    count: 0,
    aggregations: {sizes: {stats: {field: 'size'}}},
});
export type QueryStats = Assert<Equal<typeof result.aggregations.sizes, StatsAggregationResult>>;

// @ts-expect-error `branch` is required
connect({repoId: 'com.example'});

// @ts-expect-error `repoId` is required
connect({branch: 'master'});
