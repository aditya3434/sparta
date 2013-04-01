import sparta.checkers.quals.*;
import sparta.checkers.quals.FlowSinks;
import sparta.checkers.quals.FlowSources.FlowSource;
import sparta.checkers.quals.FlowSinks.FlowSink;

import java.util.List;
import java.io.File;

/**
 * Example assumes a policy file that allows flows
 * from the microphone to the network and
 * {} to email, but nothing else.
 */



@FlowSources({FlowSource.PHONE_NUMBER}) @FlowSinks({FlowSink.NETWORK})
class PolicyTest {

    private @FlowSources({FlowSource.ANY}) Object anySource = new @FlowSources({FlowSource.ANY}) Object();
    private @FlowSources({FlowSource.EXTERNAL_STORAGE, FlowSource.FILESYSTEM, FlowSource.LOCATION})
    @FlowSinks({FlowSink.LOG})
//	//:: error: (forbidden.flow)
    Object esFsLocSource = null;
  //  //:: error: (forbidden.flow)
    private @FlowSinks({FlowSink.LOG}) Object logcatSink = null;
    ////:: error: (forbidden.flow)
    private @FlowSources({FlowSource.ANY}) @FlowSinks({FlowSink.LOG}) Object fsAny() {
        return null;
    }
    ////:: error: (forbidden.flow)
    public void timeToLogCat(final @FlowSources({FlowSource.TIME}) @FlowSinks({FlowSink.LOG}) Object obj) {
    }
 //   //:: error: (forbidden.flow)
    public void micToExt(final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.EXTERNAL_STORAGE}) Object obj) {
    }
 //   //:: error: (forbidden.flow)
    public @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.EXTERNAL_STORAGE}) Object micToExt2(final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({}) Object obj) {
        return null;
    }
   //  //:: error: (forbidden.flow)
    public @FlowSources(FlowSource.ANY) @FlowSinks(FlowSink.EXTERNAL_STORAGE) double fromAny(@FlowSources(FlowSource.LOCATION)  @FlowSinks(FlowSink.EXTERNAL_STORAGE) int x) {
      //  //:: error: (forbidden.flow)
    	return (@FlowSources(FlowSource.ANY) @FlowSinks(FlowSink.EXTERNAL_STORAGE)  double) x;
    }

  //  //:: error: (forbidden.flow)
    public <T extends @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.NETWORK}) Object> T accToNet() { return null; }
    // //:: error: (forbidden.flow)
    public List<? extends @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.FILESYSTEM}) File> accFileToNet() { return null; }

    void test() {
       //  //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.PHONE_NUMBER}) @FlowSinks({FlowSink.LOG}) Object obj = null;
       //  //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.TIME}) @FlowSinks({FlowSink.LOG}) Object anyObj = null;
        // //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.LOG}) Object micToLc = null;

       // //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.EMAIL}) Object accToEm = null;
       //  //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.DISPLAY}) Object accToDi = null;
        // //:: error: (forbidden.flow)
        final String [] arrayOfAccToDEm = new String @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.DISPLAY, FlowSink.EMAIL}) []{};

        // //:: error: (forbidden.flow)
        final String [] arrayOfAccToDEmFs = new String @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.DISPLAY, FlowSink.EMAIL, FlowSink.FILESYSTEM}) []{};
        // //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.FILESYSTEM}) Object accToFs = null;
        // //:: error: (forbidden.flow)
        final @FlowSources({FlowSource.ACCOUNTS}) @FlowSinks({FlowSink.FILESYSTEM, FlowSink.DISPLAY}) Object accToFsDi = null;


        // //:: error: (forbidden.flow)
        List<@FlowSources({FlowSource.MICROPHONE, FlowSource.TIME}) @FlowSinks({FlowSink.EMAIL, FlowSink.FILESYSTEM}) File> maTiFile = null;
        // //:: error: (forbidden.flow)
        List<@FlowSources({FlowSource.MICROPHONE, FlowSource.TIME}) @FlowSinks({FlowSink.EMAIL, FlowSink.NETWORK}) File> maTiFile2 = null;
        // //:: error: (forbidden.flow)
        List<@FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.EMAIL, FlowSink.NETWORK}) File> maFile2 = null;

        
        @FlowSources({FlowSource.PHONE_NUMBER}) @FlowSinks({FlowSink.EMAIL, FlowSink.LOG})
        class Whatever {
//:: error: receiver parameter not applicable for constructor of top-level class: @FlowSources(value = {FlowSource.PHONE_NUMBER}) 
            public Whatever(@FlowSources({FlowSource.PHONE_NUMBER}) @FlowSinks({FlowSink.NETWORK}) Whatever this) {

            }
        }

      /// /:: error: (forbidden.flow)
        final Object whatever = new @FlowSources({FlowSource.PHONE_NUMBER}) @FlowSinks({FlowSink.NETWORK}) Whatever();
      //// :: error: (forbidden.flow)
        final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.RANDOM}) Object micToRandom = null;

      ////:: error: (forbidden.flow)
        final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.NETWORK, FlowSink.EXTERNAL_STORAGE}) Object micToNExt = null;
      ////:: error: (forbidden.flow) 
        final @FlowSources({FlowSource.MICROPHONE}) @FlowSinks({FlowSink.NETWORK, FlowSink.LOG, FlowSink.SMS}) Object micToNetLogMsg = null;
    }

    public static void testInstantiate() {


        final Object obj = new PolicyTest();
    }
}
