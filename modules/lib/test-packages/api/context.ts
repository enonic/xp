import {get, run} from '/lib/xp/context';
import type {Context} from '/lib/xp/context';

import type {Assert, Equal} from '../assert';

interface Attributes {
    tenant: string;
}

const context = get<Attributes>();
export type GetKeepsAttributes = Assert<Equal<typeof context, Context<Attributes>>>;
export type TenantIsOptional = Assert<Equal<typeof context.attributes.tenant, string | undefined>>;

const result = run({branch: 'draft', principals: ['role:system.admin']}, () => ({ok: true}));
export type RunReturnsCallbackResult = Assert<Equal<typeof result, {ok: boolean}>>;

// @ts-expect-error `principals` holds principal keys
run({principals: ['admin']}, () => null);
