import type {Assert, Equal, IsTyped} from './assert';

// The directly installed `@enonic-types/lib-content` is a newer copy that also exports `skewMarker`.
const contentLib = require('/lib/xp/content');
export type ContentIsTyped = Assert<IsTyped<typeof contentLib>>;
export type ContentIsXpCopy = Assert<Equal<'skewMarker' extends keyof typeof contentLib ? true : false, false>>;

const content = contentLib.get({key: '/site'});
export type ContentId = Assert<Equal<NonNullable<typeof content>['_id'], string>>;
