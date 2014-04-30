var bridge = require( './lib/bridge' );

bridge.send( 'Request', function( err, data ) {
  if ( err ) {
    return console.log( 'Error in request: %s', err );
  }

  console.log( 'Response: %s', data );
} );
