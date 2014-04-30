var bridge = require( './lib/bridge' );

bridge.register( 'request', function( data, res ) {
  if ( data ) {
    res.send( data );
  } else {
    res.error( 'There was an error' );
  }
} );

bridge.send( 'request', false, function( err, data ) {
  if ( err ) {
    return console.log( 'Error in request: %s', err );
  }

  console.log( 'Response: %s', data );
} );
