module api.content.page {

    export enum PageMode {

        AUTOMATIC,              // Content: when page.template is null
        FORCED_TEMPLATE,        // Content: when page.template is not null
        FORCED_CONTROLLER,      // PageTemplate: when page.descriptor is not null
        NO_CONTROLLER           // PageTemplate: when page.descriptor is null
    }
}