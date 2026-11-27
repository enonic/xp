// Import resolution through `paths` is spot-checked on a few libs; `require-all.ts` covers every lib.
import type {Content} from '@enonic-types/core';
import {get} from '/lib/xp/content';
import {run} from '/lib/xp/context';
import {connect} from '/lib/xp/node';
import type {RepoConnection} from '/lib/xp/node';
import {getContent} from '/lib/xp/portal';

import type {Assert, Equal, IsTyped} from './assert';

export type AppIsTyped = Assert<IsTyped<typeof app>>;

const content = get({key: '/site'});
export type ContentResult = Assert<Equal<typeof content, Content | null>>;

const connection = connect({repoId: 'com.enonic.cms.default', branch: 'master'});
export type ConnectResult = Assert<Equal<typeof connection, RepoConnection>>;

const result = run({branch: 'draft'}, () => 42);
export type RunResult = Assert<Equal<typeof result, number>>;

const portalContent = getContent();
export type PortalContent = Assert<Equal<typeof portalContent, Content | null>>;

// @ts-expect-error `key` is required
get({});

// @ts-expect-error `repoId` and `branch` are required
connect({});

// @ts-expect-error the callback is required
run({branch: 'draft'});
