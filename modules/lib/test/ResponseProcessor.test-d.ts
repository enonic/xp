import type {
    Request,
    MappedResponse,
    Response,
    ResponseProcessorController,
} from '../core/index';

import {
    expectAssignable,
    // expectDeprecated,
    // expectDocCommentIncludes,
    // expectError,
    // expectNever,
	// expectNotAssignable,
    // printType,
    // expectNotDeprecated,
    // expectNotType,
    // expectType,
    // printType,
} from 'tsd';

// const log = {
//     info: (message?: string, ...optionalParams: unknown[]) => { /* no-op */ },
// };

// Testing examples from HTTP Filters
// https://developer.enonic.com/docs/xp/stable/framework/processors

// ────────────────────────────────────────────────────────────────────────────
// dynamically adds a bodyEnd page contribution to the response
// ────────────────────────────────────────────────────────────────────────────
const responseProcessorController1 = {
    responseProcessor: (_req: Request, res: MappedResponse) => {
        const trackingScript = '<script src="http://some.cdn/js/tracker.js"></script>';
    
        // Check if contribution field exists, if not create it
        const bodyEnd = res.pageContributions.bodyEnd;
        if (!bodyEnd) {
            res.pageContributions.bodyEnd = [];
        }
    
        // Add contribution
        (res.pageContributions.bodyEnd as string[]).push(trackingScript);
    
        return res as Response;
    },
};
expectAssignable<ResponseProcessorController>(responseProcessorController1);
