import type {Content} from '@enonic-types/core';
import {assetUrl, getContent, getSite, getSiteConfig, pageUrl} from '/lib/xp/portal';
import type {Site} from '/lib/xp/portal';

import type {Assert, Equal} from '../assert';

interface SiteConfig {
    theme: string;
}

type Article = Content<{title: string}, 'com.example:article'>;

const content = getContent<Article>();
export type GetContentInfersHit = Assert<Equal<typeof content, Article | null>>;

const site = getSite<SiteConfig>();
export type GetSiteKeepsConfig = Assert<Equal<typeof site, Site<SiteConfig> | null>>;

const config = getSiteConfig<SiteConfig>();
export type GetSiteConfig = Assert<Equal<typeof config, SiteConfig | null>>;

const url = pageUrl({path: '/site/article'});
export type PageUrl = Assert<Equal<typeof url, string>>;

const asset = assetUrl('css/main.css');
export type AssetUrl = Assert<Equal<typeof asset, string>>;

// @ts-expect-error the parameters object is required
pageUrl();
