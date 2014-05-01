var async = require( 'async' ),
    bridge = require( './lib/bridge' );

bridge.register( 'message', function( data, res ) {
  res.send( 'Hello World!' );
} );

async.times( 2000, function( n, done ) {
  bridge.send( 'message', {}, done );
}, function( err, results ) {
  console.log( err );
  console.log( results.length );
} );
