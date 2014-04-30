var bridge = require( './lib/bridge' );

bridge.register( 'request', function( data, res ) {
  res.send( data );
} );

bridge.send( 'request', 'Request', function( err, data ) {
  if ( err ) {
    return console.log( 'Error in request: %s', err );
  }

  console.log( 'Response: %s', data );
} );
