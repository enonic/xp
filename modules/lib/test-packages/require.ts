import type {Assert, Equal, IsTyped, IsUnknown} from './assert';

export type AppIsTyped = Assert<IsTyped<typeof app>>;
export type LogIsTyped = Assert<IsTyped<typeof log>>;
export type BeanBridgeIsTyped = Assert<IsTyped<typeof __>>;

const contentLib = require('/lib/xp/content');
const contextLib = require('/lib/xp/context');

const content = contentLib.get({key: '/site'});
export type ContentId = Assert<Equal<NonNullable<typeof content>['_id'], string>>;
export type ContentIsNullable = Assert<Equal<null extends typeof content ? true : false, true>>;

const result = contextLib.run({branch: 'draft'}, () => 42);
export type RunResult = Assert<Equal<typeof result, number>>;

// @ts-expect-error `key` is required
contentLib.get({});

// @ts-expect-error the callback is required
contextLib.run({branch: 'draft'});

const unknownLib = require('/lib/custom/missing');
export type UnknownLibIsUnknown = Assert<IsUnknown<typeof unknownLib>>;
