import type {
    ComponentDescriptor,
    Content,
    PageComponent,
    PageComponentWhenAutomaticTemplate,
    PageComponentWhenSpecificTemplate,
} from '../core/index';
import {expectAssignable} from 'tsd';

// On a page using automatic page template, the page component is empty.
const pageComponentWhenAutomaticTemplate = {};
expectAssignable<PageComponentWhenAutomaticTemplate>(pageComponentWhenAutomaticTemplate);
expectAssignable<Partial<Content>>({
    page: pageComponentWhenAutomaticTemplate,
});

// When a specific page template is selected, the PageComponent only contains page, type and template properties.
// #10771 The template property is the content id of the page template.
const pageComponentWhenSpecificTemplate = {
    path: '/' as const,
    type: 'page' as const,
    template: '05f00637-2355-43f5-b199-4333ce7e3fbe',
};
expectAssignable<PageComponentWhenSpecificTemplate>(pageComponentWhenSpecificTemplate);
expectAssignable<Partial<Content>>({
    page: pageComponentWhenSpecificTemplate,
});

// On a customized page the page component contains config, descriptor, path, regions and type properties.
const pageComponent = {
    config: {},
    descriptor: 'com.enonic.app.name:pagename' as ComponentDescriptor,
    path: '/' as const,
    regions: {
        main: {
            components: [],
            name: 'main',
        },
    },
    type: 'page' as const,
};
expectAssignable<PageComponent>(pageComponent);
expectAssignable<Partial<Content>>({
    page: pageComponent,
});