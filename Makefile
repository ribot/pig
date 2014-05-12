REPORTER = spec

clean:
	rm -rf lib-cov coverage.html

test:
	./node_modules/.bin/mocha --reporter $(REPORTER) tests/*

test-w:
	./node_modules/.bin/mocha --reporter $(REPORTER) -w tests/*

test-cov: lib-cov
	@PIGGIE_COVERAGE=1 $(MAKE) test REPORTER=html-cov > coverage.html

lib-cov:
	@jscoverage lib lib-cov

.PHONY: test test-w
