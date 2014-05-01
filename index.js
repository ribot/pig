var bridge = window.bridge = require( './lib/bridge' );

bridge.register( 'message', function( data, res ) {
  res.send( 'Hello World!' );
} );

bridge.register( 'message2', function( data, res ) {
  res.send( 'Hello again!' );
} );
