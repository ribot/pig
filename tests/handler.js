var sinon = require( 'sinon' )
    assert = require( 'assert' ),
    piggie = require( '../index' ),
    Response = process.env.PIGGIE_COVERAGE ? require( '../lib-cov/response' ) : require( '../lib/response' );

describe( 'Handler', function() {
  beforeEach( function() {
    piggie.reset();
  } );


  it( 'can register event handler', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( 'String' );
    } );

    // Test the handler doesn't error
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send' );
    piggie.send( res, 'event' );
    assert( spy.called );
  } );


  it( 'should error when no handler is enabled', function() {
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'error' );
    piggie.send( res, 'event' );

    assert( spy.called );
  } );


  it( 'should error when attempting to register a path twice', function() {
    // Setup the first handler
    piggie.handle( 'event', function( data, res ) {
      res.send( 'String' );
    } );

    // Attempt to setup the second handler
    try {
      piggie.handle( 'event', function( data, res ) {
        res.send( 'String' );
      } );
    } catch ( e ) {
      return assert( true );
    }

    assert( false );
  } );
} );
