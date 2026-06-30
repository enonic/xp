import type {PublishInfo} from '../core/index';
import {expectAssignable} from 'tsd';

expectAssignable<PublishInfo>({});
expectAssignable<PublishInfo>({
    from: '2026-06-26T14:00:00Z',
    to: '2026-06-26T15:00:00Z',
    first: '2026-06-20T14:00:00Z',
    time: '2026-06-26T14:05:00Z',
});
