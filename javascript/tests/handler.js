var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    Pig = require( '../index' ),
    Response = require( '../lib/response' );

var pig = new Pig();

describe( 'Handler', function() {

  beforeEach( function() {
    pig._reset();
  } );


  it( 'should error when path has not been registered', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseFailSpy = sinon.spy( dummyResponse, 'fail' );

    // Test the handler calls the fail callback
    pig._execute( dummyResponse, 'event' );
    assert( responseFailSpy.called );

  } );


  it( 'should error when attempting to register a path twice', function () {

    // Setup the first handler
    pig.register( 'event', function ( data, response ) { } );

    assert.throws( function () {
      // Attempt to setup the second handler
      pig.register( 'event', function ( data, response ) { } );
    } );

  } );

} );
