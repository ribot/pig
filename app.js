var async = require( 'async' ),
    bridge = require( './lib/bridge' );

bridge.register( 'request', function( data, res ) {
  setTimeout( function() {
    res.send( 1 );
  }, Math.random() * 1000 );
} );

async.times( 2000, function( n, done ) {
  bridge.send( 'request', {}, done );
}, function( err, results ) {
  console.log( err );
  console.log( results.length );
} );
