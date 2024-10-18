import type {
    ErrorController,
    ErrorRequest,
} from '../core/index';

import {expectAssignable} from 'tsd';

const log = {
    info: (message?: string, ...optionalParams: unknown[]) => { /* no-op */ },
};

const myErrorController = {
    handle404: (errorRequest: ErrorRequest) => {
        log.info('404 errorRequest:%s', JSON.stringify(errorRequest, null, 4));
        return {
            status: 404 as const,
        };
    },
    handleError: (errorRequest: ErrorRequest) => {
        log.info('generic errorRequest:%s', JSON.stringify(errorRequest, null, 4));
        return {
            status: 500 as const,
        };
    },
};

expectAssignable<ErrorController>(myErrorController);