import type {
    PageComponent,
} from '../core/index';
import {expectAssignable} from 'tsd';

// On a page using automatic page template, the page component is empty.
expectAssignable<PageComponent>({});

// When a specific page template is selected, the PageComponent only contains page, type and template properties.
// #10771 The template property is the content id of the page template.
expectAssignable<PageComponent>({
    path: '/',
    type: 'page',
    template: '05f00637-2355-43f5-b199-4333ce7e3fbe',
});

// On a customized page the page component contains config, descriptor, path, regions and type properties.
expectAssignable<PageComponent>({
    config: {},
    descriptor: 'com.enonic.app.name:pagename',
    path: '/',
    regions: {
        main: {
            components: [],
            name: 'main',
        },
    },
    type: 'page',
});