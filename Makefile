REPORTER = spec

clean:
	rm -rf lib-cov coverage.html

test:
	./node_modules/.bin/mocha --reporter $(REPORTER) tests/*

test-w:
	./node_modules/.bin/mocha --reporter $(REPORTER) -w tests/*

.PHONY: test test-w
