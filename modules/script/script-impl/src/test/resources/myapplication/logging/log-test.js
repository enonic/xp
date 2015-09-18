log.debug('debug message');
log.debug('debug message %s', true);
log.debug('debug message %s %s', true, {a: 3});
log.debug('debug message %s', [1,2]);

log.info('info message');
log.info('info message %s', true);
log.info('info message %s %s', true, {a: 3});
log.info('info message %s', [1,2]);

log.warning('warning message');
log.warning('warning message %s', true);
log.warning('warning message %s %s', true, {a: 3});
log.warning('warning message %s', [1,2]);

log.error('error message');
log.error('error message %s', true);
log.error('error message %s %s', true, {a: 3});
log.error('error message %s', [1,2]);
