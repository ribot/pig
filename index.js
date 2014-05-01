var bridge = window.bridge = require( './lib/bridge' );

bridge.register( 'message', function( data, res ) {
  res.send( 'Hello World!' );
} );

bridge.register( 'message2', function( data, res ) {
  res.send( 'Hello again!' );
} );

bridge.register( 'data', function( data, res ) {
  var result = data.first + data.second;
  res.send( result );
} );
