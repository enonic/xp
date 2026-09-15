import type {
    Content,
    LayoutComponent,
    PageComponent,
    PartComponent,
} from '../core/index';
import {expectType} from 'tsd';

// Applications add their own descriptors to the global maps with declaration merging, so the maps must be interfaces.
declare global {
    interface XpLayoutMap {
        'com.example.app:two-columns': {
            reverse: boolean;
        };
    }

    interface XpPageMap {
        'com.example.app:default': {
            theme: string;
        };
    }

    interface XpPartMap {
        'com.example.app:article-view': {
            title: string;
        };
    }

    interface XpMixin {
        'com-example-app': {
            seo: {
                metaTitle: string;
            };
        };
    }
}

declare const layout: LayoutComponent<'com.example.app:two-columns'>;
expectType<{reverse: boolean}>(layout.config);

declare const page: PageComponent<'com.example.app:default'>;
expectType<{theme: string}>(page.config);

declare const part: PartComponent<'com.example.app:article-view'>;
expectType<{title: string}>(part.config);

declare const content: Content;
expectType<{seo: {metaTitle: string}}>(content.x['com-example-app']);
