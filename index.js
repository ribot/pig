module.exports = process.env.PIGGIE_COVERAGE
  ? require( './lib-cov/bridge' )
  : require( './lib/bridge' );
