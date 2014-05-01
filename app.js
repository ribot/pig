var async = require( 'async' ),
    bridge = require( './lib/bridge' );

bridge.register( 'message', function( data, res ) {
  res.send( 'Hello World!' );
} );

for ( var i = 0; i < 2000; i++ ) {
  bridge.send( i, 'message', {} );
}
