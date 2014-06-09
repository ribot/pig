var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    Pig = require( '../index' ),
    Response = require( '../lib/response' ),
    nativeInterface = require( '../lib/native-interface' );

var pig = new Pig();

describe( 'Native interface', function() {

  beforeEach( function() {
    pig._reset();
  } );

  it( 'should call success with normalised statically typed arguments', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        nativeInterfaceSuccessSpy = sinon.spy( nativeInterface, 'success' );

    dummyResponse.success( {
      name: 'Samuel L Jackson'
    } );

    // Checks arguments length ( callbackId, data )
    assert.strictEqual( 2, nativeInterfaceSuccessSpy.lastCall.args.length );

    // Check all arguments are strings
    nativeInterfaceSuccessSpy.lastCall.args.forEach( function ( arg, index ) {
      assert.equal( typeof arg, 'string' );
    } );

  } );

  it( 'should call fail with normalised statically typed arguments', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        nativeInterfaceFailSpy = sinon.spy( nativeInterface, 'fail' );

    dummyResponse.fail( new Error( 'Enough is enough' ) );

    // Checks arguments length ( callbackId, errorCode, errorName, errorMessage )
    assert.strictEqual( 4, nativeInterfaceFailSpy.lastCall.args.length );

    // Check all arguments are strings
    nativeInterfaceFailSpy.lastCall.args.forEach( function ( arg, index ) {
      assert.equal( typeof arg, 'string' );
    } );

  } );

  it( 'should call event with normalised statically typed arguments', function () {
    var nativeInterfaceEventSpy = sinon.spy( nativeInterface, 'event' );

    pig.emit( 'someEvent', {
      message: 'I\'ve had it with these snakes'
    } );

    // Checks arguments length ( eventName, eventData )
    assert.strictEqual( 2, nativeInterfaceEventSpy.lastCall.args.length );

    // Check all arguments are strings
    nativeInterfaceEventSpy.lastCall.args.forEach( function ( arg, index ) {
      assert.equal( typeof arg, 'string' );
    } );

  } );

} );
